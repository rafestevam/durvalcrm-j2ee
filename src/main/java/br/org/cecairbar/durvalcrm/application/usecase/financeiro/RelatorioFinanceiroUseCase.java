package br.org.cecairbar.durvalcrm.application.usecase.financeiro;

import br.org.cecairbar.durvalcrm.application.dto.ResumoFinanceiroDTO;
import br.org.cecairbar.durvalcrm.application.dto.ReceitaPorCategoriaDTO;
import br.org.cecairbar.durvalcrm.application.dto.DespesaPorCategoriaDTO;
import br.org.cecairbar.durvalcrm.application.dto.FluxoCaixaDTO;

import java.time.LocalDate;
import java.util.List;

public interface RelatorioFinanceiroUseCase {

    /**
     * Obter resumo financeiro geral por período
     * Inclui totais de receitas, despesas e saldo
     */
    ResumoFinanceiroDTO obterResumoFinanceiro(LocalDate dataInicio, LocalDate dataFim);

    /**
     * Obter análise de receitas por categoria
     * Retorna lista com total e percentual de cada categoria
     */
    List<ReceitaPorCategoriaDTO> obterReceitasPorCategoria(LocalDate dataInicio, LocalDate dataFim);

    /**
     * Obter análise de despesas por categoria
     * Retorna lista com total e percentual de cada categoria
     */
    List<DespesaPorCategoriaDTO> obterDespesasPorCategoria(LocalDate dataInicio, LocalDate dataFim);

    /**
     * Obter fluxo de caixa mensal
     * Retorna movimentações mês a mês dentro do período
     */
    FluxoCaixaDTO obterFluxoDeCaixa(LocalDate dataInicio, LocalDate dataFim);

    /**
     * Obter resumo financeiro do mês atual
     */
    ResumoFinanceiroDTO obterResumoMesAtual();

    /**
     * Obter resumo financeiro do ano atual
     */
    ResumoFinanceiroDTO obterResumoAnoAtual();
}
