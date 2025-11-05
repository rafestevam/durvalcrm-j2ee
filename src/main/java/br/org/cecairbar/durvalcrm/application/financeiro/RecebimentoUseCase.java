package br.org.cecairbar.durvalcrm.application.financeiro;

import br.org.cecairbar.durvalcrm.domain.model.ContaBancaria;
import br.org.cecairbar.durvalcrm.domain.model.Recebimento;
import br.org.cecairbar.durvalcrm.domain.model.StatusConta;
import br.org.cecairbar.durvalcrm.domain.repository.ContaBancariaRepository;
import br.org.cecairbar.durvalcrm.domain.repository.RecebimentoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Use Case para operações de Recebimento.
 *
 * US-061: Registro de Recebimentos por Forma de Pagamento
 */
@ApplicationScoped
public class RecebimentoUseCase {

    @Inject
    RecebimentoRepository repository;

    @Inject
    ContaBancariaRepository contaRepository;

    @Inject
    RecebimentoDTOMapper mapper;

    public List<RecebimentoDTO> findAll() {
        return mapper.toDTOList(repository.findAll());
    }

    public List<RecebimentoDTO> findByContaId(UUID contaId) {
        return mapper.toDTOList(repository.findByContaBancariaId(contaId));
    }

    public List<RecebimentoDTO> findByPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        return mapper.toDTOList(repository.findByPeriodo(dataInicio, dataFim));
    }

    public RecebimentoDTO findById(UUID id) {
        return repository.findById(id)
                .map(mapper::toDTO)
                .orElseThrow(() -> new NotFoundException("Recebimento não encontrado"));
    }

    public RecebimentoDTO create(RecebimentoDTO dto) {
        // Validações
        if (dto.getValor() == null || dto.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Valor deve ser maior que zero");
        }

        if (dto.getDataRecebimento().isAfter(LocalDate.now())) {
            throw new BadRequestException("Data de recebimento não pode ser futura");
        }

        // Verificar se a conta existe e está ativa
        ContaBancaria conta = contaRepository.findById(dto.getContaBancariaId())
                .orElseThrow(() -> new NotFoundException("Conta não encontrada"));

        if (conta.getStatus() != StatusConta.ATIVA) {
            throw new BadRequestException("Conta não está ativa");
        }

        // Criar recebimento
        Recebimento recebimento = mapper.toDomain(dto);
        recebimento.setId(null); // Garantir que é novo

        // Salvar recebimento
        Recebimento saved = repository.save(recebimento);

        // Atualizar saldo da conta
        BigDecimal novoSaldo = conta.getSaldoAtual().add(saved.getValor());
        conta.setSaldoAtual(novoSaldo);
        contaRepository.save(conta);

        return mapper.toDTO(saved);
    }

    public void delete(UUID id) {
        Recebimento recebimento = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Recebimento não encontrado"));

        // Atualizar saldo da conta (subtrair o valor do recebimento)
        ContaBancaria conta = contaRepository.findById(recebimento.getContaBancariaId())
                .orElseThrow(() -> new NotFoundException("Conta não encontrada"));

        BigDecimal novoSaldo = conta.getSaldoAtual().subtract(recebimento.getValor());
        conta.setSaldoAtual(novoSaldo);
        contaRepository.save(conta);

        // Excluir recebimento
        repository.delete(id);
    }
}
