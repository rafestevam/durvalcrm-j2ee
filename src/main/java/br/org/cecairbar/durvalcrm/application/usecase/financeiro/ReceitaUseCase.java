package br.org.cecairbar.durvalcrm.application.usecase.financeiro;

import br.org.cecairbar.durvalcrm.application.dto.ReceitaDTO;
import br.org.cecairbar.durvalcrm.domain.model.TipoReceita;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ReceitaUseCase {

    /**
     * Criar nova receita
     */
    ReceitaDTO criar(ReceitaDTO receitaDTO);

    /**
     * Buscar receita por ID
     */
    ReceitaDTO buscarPorId(UUID id);

    /**
     * Atualizar receita
     */
    ReceitaDTO atualizar(UUID id, ReceitaDTO receitaDTO);

    /**
     * Deletar receita
     */
    void deletar(UUID id);

    /**
     * Listar todas as receitas
     */
    List<ReceitaDTO> listarTodas();

    /**
     * Listar receitas por período
     */
    List<ReceitaDTO> listarPorPeriodo(LocalDate dataInicio, LocalDate dataFim);

    /**
     * Listar receitas por data de recebimento
     */
    List<ReceitaDTO> listarPorDataRecebimento(LocalDate dataInicio, LocalDate dataFim);

    /**
     * Listar receitas por tipo
     */
    List<ReceitaDTO> listarPorTipo(TipoReceita tipo);

    /**
     * Listar receitas por tipo e período
     */
    List<ReceitaDTO> listarPorTipoEPeriodo(TipoReceita tipo, LocalDate dataInicio, LocalDate dataFim);

    /**
     * Listar receitas por categoria
     */
    List<ReceitaDTO> listarPorCategoria(UUID categoriaId);

    /**
     * Listar receitas por categoria e período
     */
    List<ReceitaDTO> listarPorCategoriaEPeriodo(UUID categoriaId, LocalDate dataInicio, LocalDate dataFim);

    /**
     * Listar receitas por associado
     */
    List<ReceitaDTO> listarPorAssociado(UUID associadoId);

    /**
     * Somar receitas por período
     */
    BigDecimal somarPorPeriodo(LocalDate dataInicio, LocalDate dataFim);

    /**
     * Somar receitas por categoria e período
     */
    BigDecimal somarPorCategoriaEPeriodo(UUID categoriaId, LocalDate dataInicio, LocalDate dataFim);

    /**
     * Somar receitas por tipo e período
     */
    BigDecimal somarPorTipoEPeriodo(TipoReceita tipo, LocalDate dataInicio, LocalDate dataFim);

    /**
     * Somar receitas por associado e período
     */
    BigDecimal somarPorAssociadoEPeriodo(UUID associadoId, LocalDate dataInicio, LocalDate dataFim);

    /**
     * Verificar se existe receita vinculada a uma origem (Mensalidade, Doação, Venda)
     */
    boolean existePorOrigem(UUID origemId);
}
