package br.org.cecairbar.durvalcrm.application.usecase.financeiro;

import br.org.cecairbar.durvalcrm.application.dto.ResumoFinanceiroDTO;
import br.org.cecairbar.durvalcrm.application.dto.ReceitaPorCategoriaDTO;
import br.org.cecairbar.durvalcrm.application.dto.DespesaPorCategoriaDTO;
import br.org.cecairbar.durvalcrm.application.dto.FluxoCaixaDTO;
import br.org.cecairbar.durvalcrm.domain.model.CategoriaFinanceira;
import br.org.cecairbar.durvalcrm.domain.model.Receita;
import br.org.cecairbar.durvalcrm.domain.model.Despesa;
import br.org.cecairbar.durvalcrm.domain.repository.ReceitaRepository;
import br.org.cecairbar.durvalcrm.domain.repository.DespesaRepository;
import br.org.cecairbar.durvalcrm.domain.repository.CategoriaFinanceiraRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class RelatorioFinanceiroUseCaseImpl implements RelatorioFinanceiroUseCase {

    @Inject
    ReceitaRepository receitaRepository;

    @Inject
    DespesaRepository despesaRepository;

    @Inject
    CategoriaFinanceiraRepository categoriaRepository;

    @Override
    public ResumoFinanceiroDTO obterResumoFinanceiro(LocalDate dataInicio, LocalDate dataFim) {
        validarPeriodo(dataInicio, dataFim);

        // Buscar totais
        BigDecimal totalReceitas = receitaRepository.sumByPeriodo(dataInicio, dataFim);
        BigDecimal totalDespesas = despesaRepository.sumByPeriodo(dataInicio, dataFim);
        BigDecimal saldo = totalReceitas.subtract(totalDespesas);

        // Contar transações
        long quantidadeReceitas = receitaRepository.countByPeriodo(dataInicio, dataFim);
        long quantidadeDespesas = despesaRepository.findByPeriodo(dataInicio, dataFim).size();

        return ResumoFinanceiroDTO.builder()
                .periodoInicio(dataInicio)
                .periodoFim(dataFim)
                .totalReceitas(totalReceitas)
                .totalDespesas(totalDespesas)
                .saldo(saldo)
                .quantidadeReceitas(quantidadeReceitas)
                .quantidadeDespesas(quantidadeDespesas)
                .build();
    }

    @Override
    public List<ReceitaPorCategoriaDTO> obterReceitasPorCategoria(LocalDate dataInicio, LocalDate dataFim) {
        validarPeriodo(dataInicio, dataFim);

        // Buscar receitas do período
        List<Receita> receitas = receitaRepository.findByPeriodo(dataInicio, dataFim);

        // Total geral de receitas
        BigDecimal totalGeral = receitas.stream()
                .map(Receita::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalGeral.compareTo(BigDecimal.ZERO) == 0) {
            return new ArrayList<>();
        }

        // Agrupar por categoria
        Map<CategoriaFinanceira, List<Receita>> receitasPorCategoria = receitas.stream()
                .collect(Collectors.groupingBy(Receita::getCategoria));

        // Criar DTOs
        return receitasPorCategoria.entrySet().stream()
                .map(entry -> {
                    CategoriaFinanceira categoria = entry.getKey();
                    List<Receita> receitasCategoria = entry.getValue();

                    BigDecimal totalCategoria = receitasCategoria.stream()
                            .map(Receita::getValor)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal percentual = totalCategoria
                            .divide(totalGeral, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100));

                    long quantidade = receitasCategoria.size();

                    return ReceitaPorCategoriaDTO.builder()
                            .categoriaId(categoria.getId())
                            .categoriaNome(categoria.getNome())
                            .categoriaCor(categoria.getCor())
                            .total(totalCategoria)
                            .percentual(percentual)
                            .quantidade(quantidade)
                            .build();
                })
                .sorted((a, b) -> b.getTotal().compareTo(a.getTotal()))
                .collect(Collectors.toList());
    }

    @Override
    public List<DespesaPorCategoriaDTO> obterDespesasPorCategoria(LocalDate dataInicio, LocalDate dataFim) {
        validarPeriodo(dataInicio, dataFim);

        // Buscar despesas do período
        List<Despesa> despesas = despesaRepository.findByPeriodo(dataInicio, dataFim);

        // Total geral de despesas
        BigDecimal totalGeral = despesas.stream()
                .map(Despesa::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalGeral.compareTo(BigDecimal.ZERO) == 0) {
            return new ArrayList<>();
        }

        // Agrupar por categoria
        Map<CategoriaFinanceira, List<Despesa>> despesasPorCategoria = despesas.stream()
                .collect(Collectors.groupingBy(Despesa::getCategoria));

        // Criar DTOs
        return despesasPorCategoria.entrySet().stream()
                .map(entry -> {
                    CategoriaFinanceira categoria = entry.getKey();
                    List<Despesa> despesasCategoria = entry.getValue();

                    BigDecimal totalCategoria = despesasCategoria.stream()
                            .map(Despesa::getValor)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal percentual = totalCategoria
                            .divide(totalGeral, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100));

                    long quantidade = despesasCategoria.size();

                    return DespesaPorCategoriaDTO.builder()
                            .categoriaId(categoria.getId())
                            .categoriaNome(categoria.getNome())
                            .categoriaCor(categoria.getCor())
                            .total(totalCategoria)
                            .percentual(percentual)
                            .quantidade(quantidade)
                            .build();
                })
                .sorted((a, b) -> b.getTotal().compareTo(a.getTotal()))
                .collect(Collectors.toList());
    }

    @Override
    public FluxoCaixaDTO obterFluxoDeCaixa(LocalDate dataInicio, LocalDate dataFim) {
        validarPeriodo(dataInicio, dataFim);

        // Gerar lista de meses no período
        List<YearMonth> meses = gerarListaDeMeses(dataInicio, dataFim);

        // Criar movimentações mensais
        List<FluxoCaixaDTO.MovimentacaoMensalDTO> movimentacoes = meses.stream()
                .map(mes -> {
                    LocalDate inicioMes = mes.atDay(1);
                    LocalDate fimMes = mes.atEndOfMonth();

                    BigDecimal receitas = receitaRepository.sumByPeriodo(inicioMes, fimMes);
                    BigDecimal despesas = despesaRepository.sumByPeriodo(inicioMes, fimMes);
                    BigDecimal saldo = receitas.subtract(despesas);

                    return FluxoCaixaDTO.MovimentacaoMensalDTO.builder()
                            .mesAno(mes)
                            .receitas(receitas)
                            .despesas(despesas)
                            .saldo(saldo)
                            .build();
                })
                .collect(Collectors.toList());

        // Calcular totais
        BigDecimal totalReceitas = movimentacoes.stream()
                .map(FluxoCaixaDTO.MovimentacaoMensalDTO::getReceitas)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDespesas = movimentacoes.stream()
                .map(FluxoCaixaDTO.MovimentacaoMensalDTO::getDespesas)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saldoTotal = totalReceitas.subtract(totalDespesas);

        return FluxoCaixaDTO.builder()
                .periodoInicio(dataInicio)
                .periodoFim(dataFim)
                .totalReceitasPeriodo(totalReceitas)
                .totalDespesasPeriodo(totalDespesas)
                .saldoPeriodo(saldoTotal)
                .movimentacoes(movimentacoes)
                .build();
    }

    @Override
    public ResumoFinanceiroDTO obterResumoMesAtual() {
        LocalDate hoje = LocalDate.now();
        LocalDate inicioMes = hoje.withDayOfMonth(1);
        LocalDate fimMes = hoje.withDayOfMonth(hoje.lengthOfMonth());

        return obterResumoFinanceiro(inicioMes, fimMes);
    }

    @Override
    public ResumoFinanceiroDTO obterResumoAnoAtual() {
        LocalDate hoje = LocalDate.now();
        LocalDate inicioAno = hoje.withDayOfYear(1);
        LocalDate fimAno = hoje.withDayOfYear(hoje.lengthOfYear());

        return obterResumoFinanceiro(inicioAno, fimAno);
    }

    // Métodos auxiliares

    private void validarPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio.isAfter(dataFim)) {
            throw new BadRequestException("Data de início deve ser anterior à data de fim");
        }
    }

    private List<YearMonth> gerarListaDeMeses(LocalDate dataInicio, LocalDate dataFim) {
        List<YearMonth> meses = new ArrayList<>();
        YearMonth mesAtual = YearMonth.from(dataInicio);
        YearMonth mesFinal = YearMonth.from(dataFim);

        while (!mesAtual.isAfter(mesFinal)) {
            meses.add(mesAtual);
            mesAtual = mesAtual.plusMonths(1);
        }

        return meses;
    }
}
