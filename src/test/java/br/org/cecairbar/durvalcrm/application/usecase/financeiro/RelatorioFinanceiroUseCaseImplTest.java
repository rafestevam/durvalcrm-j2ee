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

import jakarta.ws.rs.BadRequestException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RelatorioFinanceiroUseCaseImplTest {

    @InjectMocks
    private RelatorioFinanceiroUseCaseImpl relatorioUseCase;

    @Mock
    private ReceitaRepository receitaRepository;

    @Mock
    private DespesaRepository despesaRepository;

    @Mock
    private CategoriaFinanceiraRepository categoriaRepository;

    private LocalDate dataInicio;
    private LocalDate dataFim;

    @Before
    public void setUp() {
        dataInicio = LocalDate.of(2025, 1, 1);
        dataFim = LocalDate.of(2025, 1, 31);
    }

    @Test
    public void testObterResumoFinanceiro() {
        BigDecimal totalReceitas = new BigDecimal("5000.00");
        BigDecimal totalDespesas = new BigDecimal("3000.00");
        long quantidadeReceitas = 10L;
        long quantidadeDespesas = 5L;

        when(receitaRepository.sumByPeriodo(dataInicio, dataFim)).thenReturn(totalReceitas);
        when(despesaRepository.sumByPeriodo(dataInicio, dataFim)).thenReturn(totalDespesas);
        when(receitaRepository.countByPeriodo(dataInicio, dataFim)).thenReturn(quantidadeReceitas);
        when(despesaRepository.findByPeriodo(dataInicio, dataFim)).thenReturn(new ArrayList<>());

        ResumoFinanceiroDTO resultado = relatorioUseCase.obterResumoFinanceiro(dataInicio, dataFim);

        assertNotNull(resultado);
        assertEquals(totalReceitas, resultado.getTotalReceitas());
        assertEquals(totalDespesas, resultado.getTotalDespesas());
        assertEquals(new BigDecimal("2000.00"), resultado.getSaldo());
        assertEquals(Long.valueOf(quantidadeReceitas), resultado.getQuantidadeReceitas());

        verify(receitaRepository).sumByPeriodo(dataInicio, dataFim);
        verify(despesaRepository).sumByPeriodo(dataInicio, dataFim);
        verify(receitaRepository).countByPeriodo(dataInicio, dataFim);
    }

    @Test(expected = BadRequestException.class)
    public void testObterResumoFinanceiroDataInvalida() {
        LocalDate inicio = LocalDate.of(2025, 1, 31);
        LocalDate fim = LocalDate.of(2025, 1, 1);

        relatorioUseCase.obterResumoFinanceiro(inicio, fim);
    }

    @Test
    public void testObterReceitasPorCategoria() {
        UUID categoriaId = UUID.randomUUID();
        CategoriaFinanceira categoria = new CategoriaFinanceira();
        categoria.setId(categoriaId);
        categoria.setNome("Mensalidades");
        categoria.setCor("#00FF00");

        Receita receita1 = new Receita();
        receita1.setCategoria(categoria);
        receita1.setValor(new BigDecimal("300.00"));

        Receita receita2 = new Receita();
        receita2.setCategoria(categoria);
        receita2.setValor(new BigDecimal("200.00"));

        List<Receita> receitas = Arrays.asList(receita1, receita2);

        when(receitaRepository.findByPeriodo(dataInicio, dataFim)).thenReturn(receitas);

        List<ReceitaPorCategoriaDTO> resultado = relatorioUseCase.obterReceitasPorCategoria(dataInicio, dataFim);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());

        ReceitaPorCategoriaDTO relatorio = resultado.get(0);
        assertEquals(categoriaId, relatorio.getCategoriaId());
        assertEquals("Mensalidades", relatorio.getCategoriaNome());
        assertEquals(new BigDecimal("500.00"), relatorio.getTotal());
        assertEquals(Long.valueOf(2), relatorio.getQuantidade());

        verify(receitaRepository).findByPeriodo(dataInicio, dataFim);
    }

    @Test
    public void testObterReceitasPorCategoriaListaVazia() {
        when(receitaRepository.findByPeriodo(dataInicio, dataFim)).thenReturn(new ArrayList<>());

        List<ReceitaPorCategoriaDTO> resultado = relatorioUseCase.obterReceitasPorCategoria(dataInicio, dataFim);

        assertNotNull(resultado);
        assertEquals(0, resultado.size());

        verify(receitaRepository).findByPeriodo(dataInicio, dataFim);
    }

    @Test
    public void testObterDespesasPorCategoria() {
        UUID categoriaId = UUID.randomUUID();
        CategoriaFinanceira categoria = new CategoriaFinanceira();
        categoria.setId(categoriaId);
        categoria.setNome("Manutenção");
        categoria.setCor("#FF0000");

        Despesa despesa1 = new Despesa();
        despesa1.setCategoria(categoria);
        despesa1.setValor(new BigDecimal("150.00"));

        Despesa despesa2 = new Despesa();
        despesa2.setCategoria(categoria);
        despesa2.setValor(new BigDecimal("350.00"));

        List<Despesa> despesas = Arrays.asList(despesa1, despesa2);

        when(despesaRepository.findByPeriodo(dataInicio, dataFim)).thenReturn(despesas);

        List<DespesaPorCategoriaDTO> resultado = relatorioUseCase.obterDespesasPorCategoria(dataInicio, dataFim);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());

        DespesaPorCategoriaDTO relatorio = resultado.get(0);
        assertEquals(categoriaId, relatorio.getCategoriaId());
        assertEquals("Manutenção", relatorio.getCategoriaNome());
        assertEquals(new BigDecimal("500.00"), relatorio.getTotal());
        assertEquals(Long.valueOf(2), relatorio.getQuantidade());

        verify(despesaRepository).findByPeriodo(dataInicio, dataFim);
    }

    @Test
    public void testObterFluxoDeCaixa() {
        // Mock para janeiro (2025-01-01 a 2025-01-31)
        when(receitaRepository.sumByPeriodo(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(new BigDecimal("1000.00"));
        when(despesaRepository.sumByPeriodo(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(new BigDecimal("500.00"));

        FluxoCaixaDTO resultado = relatorioUseCase.obterFluxoDeCaixa(dataInicio, dataFim);

        assertNotNull(resultado);
        assertEquals(dataInicio, resultado.getPeriodoInicio());
        assertEquals(dataFim, resultado.getPeriodoFim());
        assertEquals(new BigDecimal("1000.00"), resultado.getTotalReceitasPeriodo());
        assertEquals(new BigDecimal("500.00"), resultado.getTotalDespesasPeriodo());
        assertEquals(new BigDecimal("500.00"), resultado.getSaldoPeriodo());

        assertNotNull(resultado.getMovimentacoes());
        assertEquals(1, resultado.getMovimentacoes().size());

        verify(receitaRepository, atLeastOnce()).sumByPeriodo(any(LocalDate.class), any(LocalDate.class));
        verify(despesaRepository, atLeastOnce()).sumByPeriodo(any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    public void testObterResumoMesAtual() {
        BigDecimal totalReceitas = new BigDecimal("1500.00");
        BigDecimal totalDespesas = new BigDecimal("800.00");

        when(receitaRepository.sumByPeriodo(any(LocalDate.class), any(LocalDate.class))).thenReturn(totalReceitas);
        when(despesaRepository.sumByPeriodo(any(LocalDate.class), any(LocalDate.class))).thenReturn(totalDespesas);
        when(receitaRepository.countByPeriodo(any(LocalDate.class), any(LocalDate.class))).thenReturn(5L);
        when(despesaRepository.findByPeriodo(any(LocalDate.class), any(LocalDate.class))).thenReturn(new ArrayList<>());

        ResumoFinanceiroDTO resultado = relatorioUseCase.obterResumoMesAtual();

        assertNotNull(resultado);
        assertEquals(totalReceitas, resultado.getTotalReceitas());
        assertEquals(totalDespesas, resultado.getTotalDespesas());
        assertEquals(new BigDecimal("700.00"), resultado.getSaldo());

        verify(receitaRepository).sumByPeriodo(any(LocalDate.class), any(LocalDate.class));
        verify(despesaRepository).sumByPeriodo(any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    public void testObterResumoAnoAtual() {
        BigDecimal totalReceitas = new BigDecimal("60000.00");
        BigDecimal totalDespesas = new BigDecimal("35000.00");

        when(receitaRepository.sumByPeriodo(any(LocalDate.class), any(LocalDate.class))).thenReturn(totalReceitas);
        when(despesaRepository.sumByPeriodo(any(LocalDate.class), any(LocalDate.class))).thenReturn(totalDespesas);
        when(receitaRepository.countByPeriodo(any(LocalDate.class), any(LocalDate.class))).thenReturn(100L);
        when(despesaRepository.findByPeriodo(any(LocalDate.class), any(LocalDate.class))).thenReturn(new ArrayList<>());

        ResumoFinanceiroDTO resultado = relatorioUseCase.obterResumoAnoAtual();

        assertNotNull(resultado);
        assertEquals(totalReceitas, resultado.getTotalReceitas());
        assertEquals(totalDespesas, resultado.getTotalDespesas());
        assertEquals(new BigDecimal("25000.00"), resultado.getSaldo());

        verify(receitaRepository).sumByPeriodo(any(LocalDate.class), any(LocalDate.class));
        verify(despesaRepository).sumByPeriodo(any(LocalDate.class), any(LocalDate.class));
    }
}
