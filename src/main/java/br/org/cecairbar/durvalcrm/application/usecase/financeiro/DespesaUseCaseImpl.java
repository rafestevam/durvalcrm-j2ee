package br.org.cecairbar.durvalcrm.application.usecase.financeiro;

import br.org.cecairbar.durvalcrm.application.dto.DespesaDTO;
import br.org.cecairbar.durvalcrm.application.financeiro.DespesaMapper;
import br.org.cecairbar.durvalcrm.domain.model.Despesa;
import br.org.cecairbar.durvalcrm.domain.model.StatusPagamentoDespesa;
import br.org.cecairbar.durvalcrm.domain.model.TipoDespesa;
import br.org.cecairbar.durvalcrm.domain.repository.DespesaRepository;
import br.org.cecairbar.durvalcrm.domain.repository.CategoriaFinanceiraRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class DespesaUseCaseImpl implements DespesaUseCase {

    @Inject
    DespesaRepository despesaRepository;

    @Inject
    CategoriaFinanceiraRepository categoriaRepository;

    @Inject
    DespesaMapper mapper;

    @Override
    @Transactional
    public DespesaDTO criar(DespesaDTO despesaDTO) {
        // Validar categoria existe
        if (despesaDTO.getCategoriaId() != null) {
            categoriaRepository.findById(despesaDTO.getCategoriaId())
                    .orElseThrow(() -> new BadRequestException("Categoria financeira não encontrada"));
        }

        // Validar datas
        if (despesaDTO.getDataVencimento() != null && despesaDTO.getDataDespesa() != null &&
            despesaDTO.getDataVencimento().isBefore(despesaDTO.getDataDespesa())) {
            throw new BadRequestException("Data de vencimento não pode ser anterior à data da despesa");
        }

        // Converter DTO para domain
        Despesa despesa = mapper.toDomain(despesaDTO);

        // Salvar
        despesaRepository.save(despesa);

        // Retornar DTO
        return mapper.toDTO(despesa);
    }

    @Override
    public DespesaDTO buscarPorId(UUID id) {
        Despesa despesa = despesaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Despesa não encontrada"));

        return mapper.toDTO(despesa);
    }

    @Override
    @Transactional
    public DespesaDTO atualizar(UUID id, DespesaDTO despesaDTO) {
        Despesa despesa = despesaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Despesa não encontrada"));

        // Validar categoria existe
        if (despesaDTO.getCategoriaId() != null) {
            categoriaRepository.findById(despesaDTO.getCategoriaId())
                    .orElseThrow(() -> new BadRequestException("Categoria financeira não encontrada"));
        }

        // Validar datas
        if (despesaDTO.getDataVencimento() != null && despesaDTO.getDataDespesa() != null &&
            despesaDTO.getDataVencimento().isBefore(despesaDTO.getDataDespesa())) {
            throw new BadRequestException("Data de vencimento não pode ser anterior à data da despesa");
        }

        // Atualizar
        mapper.updateDomainFromDTO(despesaDTO, despesa);
        despesaRepository.save(despesa);

        return mapper.toDTO(despesa);
    }

    @Override
    @Transactional
    public DespesaDTO marcarComoPaga(UUID id, LocalDate dataPagamento) {
        Despesa despesa = despesaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Despesa não encontrada"));

        if (despesa.getStatusPagamento() == StatusPagamentoDespesa.PAGO) {
            throw new BadRequestException("Despesa já está marcada como paga");
        }

        if (despesa.getStatusPagamento() == StatusPagamentoDespesa.CANCELADO) {
            throw new BadRequestException("Não é possível marcar como paga uma despesa cancelada");
        }

        despesa.setDataPagamento(dataPagamento);
        despesa.setStatusPagamento(StatusPagamentoDespesa.PAGO);
        despesaRepository.save(despesa);

        return mapper.toDTO(despesa);
    }

    @Override
    @Transactional
    public DespesaDTO cancelar(UUID id) {
        Despesa despesa = despesaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Despesa não encontrada"));

        if (despesa.getStatusPagamento() == StatusPagamentoDespesa.CANCELADO) {
            throw new BadRequestException("Despesa já está cancelada");
        }

        if (despesa.getStatusPagamento() == StatusPagamentoDespesa.PAGO) {
            throw new BadRequestException("Não é possível cancelar uma despesa já paga");
        }

        despesa.setStatusPagamento(StatusPagamentoDespesa.CANCELADO);
        despesaRepository.save(despesa);

        return mapper.toDTO(despesa);
    }

    @Override
    @Transactional
    public void deletar(UUID id) {
        Despesa despesa = despesaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Despesa não encontrada"));
        despesaRepository.delete(despesa);
    }

    @Override
    public List<DespesaDTO> listarTodas() {
        List<Despesa> despesas = despesaRepository.findAll();
        return mapper.toDTOList(despesas);
    }

    @Override
    public List<DespesaDTO> listarPorStatus(StatusPagamentoDespesa status) {
        List<Despesa> despesas = despesaRepository.findByStatus(status);
        return mapper.toDTOList(despesas);
    }

    @Override
    public List<DespesaDTO> listarPorTipo(TipoDespesa tipo) {
        List<Despesa> despesas = despesaRepository.findByTipoDespesa(tipo);
        return mapper.toDTOList(despesas);
    }

    @Override
    public List<DespesaDTO> listarPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        validarPeriodo(dataInicio, dataFim);

        List<Despesa> despesas = despesaRepository.findByPeriodo(dataInicio, dataFim);
        return mapper.toDTOList(despesas);
    }

    @Override
    public List<DespesaDTO> listarPorDataVencimento(LocalDate dataInicio, LocalDate dataFim) {
        validarPeriodo(dataInicio, dataFim);

        List<Despesa> despesas = despesaRepository.findByDataVencimento(dataInicio, dataFim);
        return mapper.toDTOList(despesas);
    }

    @Override
    public List<DespesaDTO> listarVencidas(LocalDate dataReferencia) {
        List<Despesa> despesas = despesaRepository.findVencidas(dataReferencia);
        return mapper.toDTOList(despesas);
    }

    @Override
    public List<DespesaDTO> listarPorFornecedor(String fornecedor) {
        List<Despesa> despesas = despesaRepository.findByFornecedor(fornecedor);
        return mapper.toDTOList(despesas);
    }

    @Override
    public List<DespesaDTO> listarPorCategoria(UUID categoriaId) {
        // Validar categoria existe
        categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new NotFoundException("Categoria financeira não encontrada"));

        List<Despesa> despesas = despesaRepository.findByCategoria(categoriaId);
        return mapper.toDTOList(despesas);
    }

    @Override
    public List<DespesaDTO> listarPorCategoriaEPeriodo(UUID categoriaId, LocalDate dataInicio, LocalDate dataFim) {
        validarPeriodo(dataInicio, dataFim);

        // Validar categoria existe
        categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new NotFoundException("Categoria financeira não encontrada"));

        List<Despesa> despesas = despesaRepository.findByCategoriaAndPeriodo(categoriaId, dataInicio, dataFim);
        return mapper.toDTOList(despesas);
    }

    @Override
    public BigDecimal somarPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        validarPeriodo(dataInicio, dataFim);
        return despesaRepository.sumByPeriodo(dataInicio, dataFim);
    }

    @Override
    public BigDecimal somarPorCategoriaEPeriodo(UUID categoriaId, LocalDate dataInicio, LocalDate dataFim) {
        validarPeriodo(dataInicio, dataFim);

        // Validar categoria existe
        categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new NotFoundException("Categoria financeira não encontrada"));

        return despesaRepository.sumByCategoria(categoriaId, dataInicio, dataFim);
    }

    @Override
    public BigDecimal somarPorTipoEPeriodo(TipoDespesa tipo, LocalDate dataInicio, LocalDate dataFim) {
        validarPeriodo(dataInicio, dataFim);
        return despesaRepository.sumByTipo(tipo, dataInicio, dataFim);
    }

    @Override
    public BigDecimal somarPorStatusEPeriodo(StatusPagamentoDespesa status, LocalDate dataInicio, LocalDate dataFim) {
        validarPeriodo(dataInicio, dataFim);
        return despesaRepository.sumByStatus(status, dataInicio, dataFim);
    }

    @Override
    public BigDecimal somarPorFornecedorEPeriodo(String fornecedor, LocalDate dataInicio, LocalDate dataFim) {
        validarPeriodo(dataInicio, dataFim);
        return despesaRepository.sumByFornecedor(fornecedor, dataInicio, dataFim);
    }

    @Override
    public long contarVencidas(LocalDate dataReferencia) {
        return despesaRepository.countVencidas(dataReferencia);
    }

    // Métodos auxiliares
    private void validarPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio.isAfter(dataFim)) {
            throw new BadRequestException("Data de início deve ser anterior à data de fim");
        }
    }
}
