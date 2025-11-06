package br.org.cecairbar.durvalcrm.application.usecase.venda;

import br.org.cecairbar.durvalcrm.application.dto.VendaDTO;
import br.org.cecairbar.durvalcrm.application.dto.ResumoVendasDTO;
import br.org.cecairbar.durvalcrm.domain.model.*;
import br.org.cecairbar.durvalcrm.domain.repository.VendaRepository;
import br.org.cecairbar.durvalcrm.domain.repository.ContaBancariaRepository;
import br.org.cecairbar.durvalcrm.domain.repository.RecebimentoRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.BadRequestException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;
import java.math.BigDecimal;

@ApplicationScoped
public class VendaUseCaseImpl implements VendaUseCase {

    @Inject
    VendaRepository vendaRepository;

    @Inject
    ContaBancariaRepository contaBancariaRepository;

    @Inject
    RecebimentoRepository recebimentoRepository;
    
    
    @Override
    @Transactional
    public VendaDTO criar(VendaDTO vendaDTO) {
        // US-067: Integração Automática de Vendas com Contas Bancárias

        // 1. Determinar conta bancária
        UUID contaBancariaId = vendaDTO.getContaBancariaId();
        if (contaBancariaId == null) {
            // Buscar conta automaticamente baseado na forma de pagamento
            contaBancariaId = buscarContaPorFormaPagamento(vendaDTO.getFormaPagamento());
        } else {
            // Validar que a conta existe e está ativa
            validarConta(contaBancariaId);
        }

        // 2. Criar venda
        Venda venda = Venda.criar(
            vendaDTO.getDescricao(),
            vendaDTO.getValor(),
            vendaDTO.getOrigem(),
            vendaDTO.getFormaPagamento()
        );
        venda.setContaBancariaId(contaBancariaId);

        // 3. Salvar venda
        vendaRepository.save(venda);

        // 4. Criar recebimento vinculado à venda
        Recebimento recebimento = criarRecebimentoParaVenda(venda);
        recebimentoRepository.save(recebimento);

        // 5. Vincular recebimento à venda
        venda.setRecebimentoId(recebimento.getId());
        vendaRepository.update(venda);

        // 6. Atualizar saldo da conta
        atualizarSaldoConta(contaBancariaId, venda.getValor());

        // 7. Retornar DTO
        return toDTO(venda);
    }

    /**
     * Busca conta bancária ativa baseada na forma de pagamento.
     * US-067: Integração Automática de Vendas com Contas Bancárias
     */
    private UUID buscarContaPorFormaPagamento(FormaPagamento formaPagamento) {
        // Mapear FormaPagamento para FinalidadeConta
        FinalidadeConta finalidade = formaPagamento.toFinalidadeConta();

        // Buscar contas ativas com a finalidade correspondente
        List<ContaBancaria> contas = contaBancariaRepository
            .findByFinalidadeAndStatus(finalidade, StatusConta.ATIVA);

        if (contas.isEmpty()) {
            throw new BadRequestException(
                String.format("Nenhuma conta ativa configurada para %s. Configure uma conta antes de registrar vendas com esta forma de pagamento.",
                    formaPagamento.getDescricao())
            );
        }

        // Retornar a primeira conta encontrada (pode ser melhorado com lógica de priorização)
        return contas.get(0).getId();
    }

    /**
     * Valida se a conta existe e está ativa.
     * US-067: Integração Automática de Vendas com Contas Bancárias
     */
    private void validarConta(UUID contaBancariaId) {
        ContaBancaria conta = contaBancariaRepository.findById(contaBancariaId)
            .orElseThrow(() -> new NotFoundException("Conta bancária não encontrada"));

        if (conta.getStatus() != StatusConta.ATIVA) {
            throw new BadRequestException("Conta bancária não está ativa");
        }
    }

    /**
     * Cria recebimento vinculado à venda.
     * US-067: Integração Automática de Vendas com Contas Bancárias
     */
    private Recebimento criarRecebimentoParaVenda(Venda venda) {
        Recebimento recebimento = new Recebimento();
        // ID será gerado automaticamente pelo JPA após persist()

        // Converter Instant para LocalDate
        LocalDate dataRecebimento = venda.getDataVenda()
            .atZone(ZoneId.systemDefault())
            .toLocalDate();
        recebimento.setDataRecebimento(dataRecebimento);

        recebimento.setValor(venda.getValor());

        // Converter FormaPagamento para FormaPagamentoRecebimento
        recebimento.setFormaPagamento(venda.getFormaPagamento().toFormaPagamentoRecebimento());

        // Mapear OrigemVenda para OrigemRecebimento
        recebimento.setOrigem(mapearOrigemVenda(venda.getOrigem()));

        recebimento.setContaBancariaId(venda.getContaBancariaId());
        recebimento.setVendaId(venda.getId());

        recebimento.setDescricao(
            String.format("Venda: %s (%s)",
                venda.getDescricao(),
                venda.getOrigem().name())
        );

        recebimento.setCreatedAt(LocalDateTime.now());

        return recebimento;
    }

    /**
     * Mapeia OrigemVenda para OrigemRecebimento.
     * US-067: Integração Automática de Vendas com Contas Bancárias
     */
    private OrigemRecebimento mapearOrigemVenda(OrigemVenda origemVenda) {
        return switch (origemVenda) {
            case CANTINA -> OrigemRecebimento.VENDA_CANTINA;
            case BAZAR -> OrigemRecebimento.TRANSFERENCIA_BAZAR;
            case LIVROS -> OrigemRecebimento.VENDA_PRODUTOS;
        };
    }

    /**
     * Atualiza saldo da conta bancária após criar recebimento.
     * US-067: Integração Automática de Vendas com Contas Bancárias
     */
    private void atualizarSaldoConta(UUID contaBancariaId, BigDecimal valor) {
        ContaBancaria conta = contaBancariaRepository.findById(contaBancariaId)
            .orElseThrow(() -> new NotFoundException("Conta bancária não encontrada"));

        BigDecimal novoSaldo = conta.getSaldoAtual().add(valor);
        conta.setSaldoAtual(novoSaldo);
        contaBancariaRepository.save(conta);
    }
    
    @Override
    public VendaDTO buscarPorId(UUID id) {
        Venda venda = vendaRepository.findById(id);
        if (venda == null) {
            throw new NotFoundException("Venda não encontrada");
        }
        
        return toDTO(venda);
    }
    
    @Override
    @Transactional
    public VendaDTO atualizar(UUID id, VendaDTO vendaDTO) {
        Venda venda = vendaRepository.findById(id);
        if (venda == null) {
            throw new NotFoundException("Venda não encontrada");
        }
        
        // Atualizar venda
        venda.atualizar(
            vendaDTO.getDescricao(),
            vendaDTO.getValor(),
            vendaDTO.getOrigem(),
            vendaDTO.getFormaPagamento()
        );
        
        // Salvar
        vendaRepository.update(venda);
        
        return toDTO(venda);
    }
    
    @Override
    @Transactional
    public void deletar(UUID id) {
        if (!vendaRepository.existsById(id)) {
            throw new NotFoundException("Venda não encontrada");
        }
        vendaRepository.delete(id);
    }
    
    @Override
    public List<VendaDTO> listarTodas() {
        return vendaRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<VendaDTO> listarPorPeriodo(Instant dataInicio, Instant dataFim) {
        if (dataInicio.isAfter(dataFim)) {
            throw new BadRequestException("Data de início deve ser anterior à data de fim");
        }
        
        return vendaRepository.findByPeriodo(dataInicio, dataFim).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<VendaDTO> listarPorOrigem(OrigemVenda origem) {
        return vendaRepository.findByOrigem(origem).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<VendaDTO> listarRecentes() {
        return vendaRepository.findRecentes().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public ResumoVendasDTO obterResumo(Instant dataInicio, Instant dataFim) {
        if (dataInicio.isAfter(dataFim)) {
            throw new BadRequestException("Data de início deve ser anterior à data de fim");
        }
        
        // Buscar dados
        long totalVendas = vendaRepository.countByPeriodo(dataInicio, dataFim);
        BigDecimal valorTotal = vendaRepository.sumValorByPeriodo(dataInicio, dataFim);
        
        // Vendas por origem
        Map<OrigemVenda, Long> vendasPorOrigem = new HashMap<>();
        Map<OrigemVenda, BigDecimal> valoresPorOrigem = new HashMap<>();
        
        for (OrigemVenda origem : OrigemVenda.values()) {
            vendasPorOrigem.put(origem, vendaRepository.countByOrigem(origem));
            valoresPorOrigem.put(origem, vendaRepository.sumValorByOrigemAndPeriodo(origem, dataInicio, dataFim));
        }
        
        return ResumoVendasDTO.criar(dataInicio, dataFim, vendasPorOrigem, valoresPorOrigem, totalVendas, valorTotal);
    }
    
    @Override
    public ResumoVendasDTO obterResumoPorOrigem(OrigemVenda origem, Instant dataInicio, Instant dataFim) {
        if (dataInicio.isAfter(dataFim)) {
            throw new BadRequestException("Data de início deve ser anterior à data de fim");
        }
        
        // Buscar dados apenas da origem específica
        long totalVendas = vendaRepository.findByOrigemAndPeriodo(origem, dataInicio, dataFim).size();
        BigDecimal valorTotal = vendaRepository.sumValorByOrigemAndPeriodo(origem, dataInicio, dataFim);
        
        // Criar mapas apenas com a origem específica
        Map<OrigemVenda, Long> vendasPorOrigem = new HashMap<>();
        Map<OrigemVenda, BigDecimal> valoresPorOrigem = new HashMap<>();
        
        vendasPorOrigem.put(origem, totalVendas);
        valoresPorOrigem.put(origem, valorTotal);
        
        return ResumoVendasDTO.criar(dataInicio, dataFim, vendasPorOrigem, valoresPorOrigem, totalVendas, valorTotal);
    }
    
    // Métodos auxiliares
    private VendaDTO toDTO(Venda venda) {
        return VendaDTO.builder()
                .id(venda.getId())
                .descricao(venda.getDescricao())
                .valor(venda.getValor())
                .origem(venda.getOrigem())
                .formaPagamento(venda.getFormaPagamento())
                .contaBancariaId(venda.getContaBancariaId())
                .recebimentoId(venda.getRecebimentoId())
                .dataVenda(venda.getDataVenda())
                .criadoEm(venda.getCriadoEm())
                .atualizadoEm(venda.getAtualizadoEm())
                .build();
    }
}