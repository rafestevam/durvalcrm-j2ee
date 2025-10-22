package br.org.cecairbar.durvalcrm.application.usecase.financeiro;

import br.org.cecairbar.durvalcrm.application.dto.DespesaDTO;
import br.org.cecairbar.durvalcrm.domain.model.StatusPagamentoDespesa;
import br.org.cecairbar.durvalcrm.domain.model.TipoDespesa;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface DespesaUseCase {

    /**
     * Criar nova despesa
     */
    DespesaDTO criar(DespesaDTO despesaDTO);

    /**
     * Buscar despesa por ID
     */
    DespesaDTO buscarPorId(UUID id);

    /**
     * Atualizar despesa
     */
    DespesaDTO atualizar(UUID id, DespesaDTO despesaDTO);

    /**
     * Marcar despesa como paga
     */
    DespesaDTO marcarComoPaga(UUID id, LocalDate dataPagamento);

    /**
     * Cancelar despesa
     */
    DespesaDTO cancelar(UUID id);

    /**
     * Deletar despesa
     */
    void deletar(UUID id);

    /**
     * Listar todas as despesas
     */
    List<DespesaDTO> listarTodas();

    /**
     * Listar despesas por status
     */
    List<DespesaDTO> listarPorStatus(StatusPagamentoDespesa status);

    /**
     * Listar despesas por tipo
     */
    List<DespesaDTO> listarPorTipo(TipoDespesa tipo);

    /**
     * Listar despesas por período (data da despesa)
     */
    List<DespesaDTO> listarPorPeriodo(LocalDate dataInicio, LocalDate dataFim);

    /**
     * Listar despesas por data de vencimento
     */
    List<DespesaDTO> listarPorDataVencimento(LocalDate dataInicio, LocalDate dataFim);

    /**
     * Listar despesas vencidas
     */
    List<DespesaDTO> listarVencidas(LocalDate dataReferencia);

    /**
     * Listar despesas por fornecedor
     */
    List<DespesaDTO> listarPorFornecedor(String fornecedor);

    /**
     * Listar despesas por categoria
     */
    List<DespesaDTO> listarPorCategoria(UUID categoriaId);

    /**
     * Listar despesas por categoria e período
     */
    List<DespesaDTO> listarPorCategoriaEPeriodo(UUID categoriaId, LocalDate dataInicio, LocalDate dataFim);

    /**
     * Somar despesas por período
     */
    BigDecimal somarPorPeriodo(LocalDate dataInicio, LocalDate dataFim);

    /**
     * Somar despesas por categoria e período
     */
    BigDecimal somarPorCategoriaEPeriodo(UUID categoriaId, LocalDate dataInicio, LocalDate dataFim);

    /**
     * Somar despesas por tipo e período
     */
    BigDecimal somarPorTipoEPeriodo(TipoDespesa tipo, LocalDate dataInicio, LocalDate dataFim);

    /**
     * Somar despesas por status e período
     */
    BigDecimal somarPorStatusEPeriodo(StatusPagamentoDespesa status, LocalDate dataInicio, LocalDate dataFim);

    /**
     * Somar despesas por fornecedor e período
     */
    BigDecimal somarPorFornecedorEPeriodo(String fornecedor, LocalDate dataInicio, LocalDate dataFim);

    /**
     * Contar despesas vencidas
     */
    long contarVencidas(LocalDate dataReferencia);
}
