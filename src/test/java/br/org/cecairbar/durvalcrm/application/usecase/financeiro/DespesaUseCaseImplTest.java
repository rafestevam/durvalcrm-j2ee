package br.org.cecairbar.durvalcrm.application.usecase.financeiro;

import br.org.cecairbar.durvalcrm.application.dto.DespesaDTO;
import br.org.cecairbar.durvalcrm.application.financeiro.DespesaMapper;
import br.org.cecairbar.durvalcrm.domain.model.Despesa;
import br.org.cecairbar.durvalcrm.domain.model.CategoriaFinanceira;
import br.org.cecairbar.durvalcrm.domain.model.StatusPagamentoDespesa;
import br.org.cecairbar.durvalcrm.domain.model.TipoDespesa;
import br.org.cecairbar.durvalcrm.domain.repository.DespesaRepository;
import br.org.cecairbar.durvalcrm.domain.repository.CategoriaFinanceiraRepository;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DespesaUseCaseImplTest {

    @InjectMocks
    private DespesaUseCaseImpl despesaUseCase;

    @Mock
    private DespesaRepository despesaRepository;

    @Mock
    private CategoriaFinanceiraRepository categoriaRepository;

    @Mock
    private DespesaMapper mapper;

    private UUID despesaId;
    private UUID categoriaId;
    private Despesa despesa;
    private DespesaDTO despesaDTO;
    private CategoriaFinanceira categoria;

    @Before
    public void setUp() {
        despesaId = UUID.randomUUID();
        categoriaId = UUID.randomUUID();

        categoria = new CategoriaFinanceira();
        categoria.setId(categoriaId);
        categoria.setNome("Manutenção");

        despesa = new Despesa();
        despesa.setId(despesaId);
        despesa.setDescricao("Reparo do telhado");
        despesa.setValor(new BigDecimal("500.00"));
        despesa.setDataDespesa(LocalDate.of(2025, 1, 15));
        despesa.setDataVencimento(LocalDate.of(2025, 1, 30));
        despesa.setStatusPagamento(StatusPagamentoDespesa.PENDENTE);
        despesa.setTipoDespesa(TipoDespesa.MANUTENCAO);
        despesa.setCategoria(categoria);

        despesaDTO = new DespesaDTO();
        despesaDTO.setId(despesaId);
        despesaDTO.setDescricao("Reparo do telhado");
        despesaDTO.setValor(new BigDecimal("500.00"));
        despesaDTO.setDataDespesa(LocalDate.of(2025, 1, 15));
        despesaDTO.setDataVencimento(LocalDate.of(2025, 1, 30));
        despesaDTO.setStatusPagamento(StatusPagamentoDespesa.PENDENTE);
        despesaDTO.setTipoDespesa(TipoDespesa.MANUTENCAO);
        despesaDTO.setCategoriaId(categoriaId);
    }

    @Test
    public void testCriarSucesso() {
        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoria));
        when(mapper.toDomain(despesaDTO)).thenReturn(despesa);
        when(despesaRepository.save(despesa)).thenReturn(despesa);
        when(mapper.toDTO(despesa)).thenReturn(despesaDTO);

        DespesaDTO resultado = despesaUseCase.criar(despesaDTO);

        assertNotNull(resultado);
        assertEquals("Reparo do telhado", resultado.getDescricao());

        verify(categoriaRepository).findById(categoriaId);
        verify(mapper).toDomain(despesaDTO);
        verify(despesaRepository).save(despesa);
    }

    @Test(expected = BadRequestException.class)
    public void testCriarDataVencimentoAnteriorDataDespesa() {
        despesaDTO.setDataDespesa(LocalDate.of(2025, 1, 30));
        despesaDTO.setDataVencimento(LocalDate.of(2025, 1, 15));

        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoria));

        despesaUseCase.criar(despesaDTO);
    }

    @Test
    public void testBuscarPorIdExistente() {
        when(despesaRepository.findById(despesaId)).thenReturn(Optional.of(despesa));
        when(mapper.toDTO(despesa)).thenReturn(despesaDTO);

        DespesaDTO resultado = despesaUseCase.buscarPorId(despesaId);

        assertNotNull(resultado);
        assertEquals(despesaId, resultado.getId());

        verify(despesaRepository).findById(despesaId);
        verify(mapper).toDTO(despesa);
    }

    @Test(expected = NotFoundException.class)
    public void testBuscarPorIdInexistente() {
        when(despesaRepository.findById(despesaId)).thenReturn(Optional.empty());

        despesaUseCase.buscarPorId(despesaId);
    }

    @Test
    public void testMarcarComoPagaSucesso() {
        LocalDate dataPagamento = LocalDate.of(2025, 1, 20);

        when(despesaRepository.findById(despesaId)).thenReturn(Optional.of(despesa));
        when(despesaRepository.save(despesa)).thenReturn(despesa);
        when(mapper.toDTO(despesa)).thenReturn(despesaDTO);

        DespesaDTO resultado = despesaUseCase.marcarComoPaga(despesaId, dataPagamento);

        assertNotNull(resultado);
        assertEquals(StatusPagamentoDespesa.PAGO, despesa.getStatusPagamento());
        assertEquals(dataPagamento, despesa.getDataPagamento());

        verify(despesaRepository).findById(despesaId);
        verify(despesaRepository).save(despesa);
    }

    @Test(expected = BadRequestException.class)
    public void testMarcarComoPagaDespesaJaPaga() {
        despesa.setStatusPagamento(StatusPagamentoDespesa.PAGO);

        when(despesaRepository.findById(despesaId)).thenReturn(Optional.of(despesa));

        despesaUseCase.marcarComoPaga(despesaId, LocalDate.now());
    }

    @Test(expected = BadRequestException.class)
    public void testMarcarComoPagaDespesaCancelada() {
        despesa.setStatusPagamento(StatusPagamentoDespesa.CANCELADO);

        when(despesaRepository.findById(despesaId)).thenReturn(Optional.of(despesa));

        despesaUseCase.marcarComoPaga(despesaId, LocalDate.now());
    }

    @Test
    public void testCancelarSucesso() {
        when(despesaRepository.findById(despesaId)).thenReturn(Optional.of(despesa));
        when(despesaRepository.save(despesa)).thenReturn(despesa);
        when(mapper.toDTO(despesa)).thenReturn(despesaDTO);

        DespesaDTO resultado = despesaUseCase.cancelar(despesaId);

        assertNotNull(resultado);
        assertEquals(StatusPagamentoDespesa.CANCELADO, despesa.getStatusPagamento());

        verify(despesaRepository).findById(despesaId);
        verify(despesaRepository).save(despesa);
    }

    @Test(expected = BadRequestException.class)
    public void testCancelarDespesaJaCancelada() {
        despesa.setStatusPagamento(StatusPagamentoDespesa.CANCELADO);

        when(despesaRepository.findById(despesaId)).thenReturn(Optional.of(despesa));

        despesaUseCase.cancelar(despesaId);
    }

    @Test(expected = BadRequestException.class)
    public void testCancelarDespesaJaPaga() {
        despesa.setStatusPagamento(StatusPagamentoDespesa.PAGO);

        when(despesaRepository.findById(despesaId)).thenReturn(Optional.of(despesa));

        despesaUseCase.cancelar(despesaId);
    }

    @Test
    public void testListarPorStatus() {
        List<Despesa> despesas = Arrays.asList(despesa);
        List<DespesaDTO> despesasDTO = Arrays.asList(despesaDTO);

        when(despesaRepository.findByStatus(StatusPagamentoDespesa.PENDENTE)).thenReturn(despesas);
        when(mapper.toDTOList(despesas)).thenReturn(despesasDTO);

        List<DespesaDTO> resultado = despesaUseCase.listarPorStatus(StatusPagamentoDespesa.PENDENTE);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());

        verify(despesaRepository).findByStatus(StatusPagamentoDespesa.PENDENTE);
    }

    @Test
    public void testListarVencidas() {
        LocalDate dataReferencia = LocalDate.of(2025, 2, 1);
        List<Despesa> despesas = Arrays.asList(despesa);
        List<DespesaDTO> despesasDTO = Arrays.asList(despesaDTO);

        when(despesaRepository.findVencidas(dataReferencia)).thenReturn(despesas);
        when(mapper.toDTOList(despesas)).thenReturn(despesasDTO);

        List<DespesaDTO> resultado = despesaUseCase.listarVencidas(dataReferencia);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());

        verify(despesaRepository).findVencidas(dataReferencia);
    }

    @Test
    public void testContarVencidas() {
        LocalDate dataReferencia = LocalDate.of(2025, 2, 1);

        when(despesaRepository.countVencidas(dataReferencia)).thenReturn(5L);

        long resultado = despesaUseCase.contarVencidas(dataReferencia);

        assertEquals(5L, resultado);

        verify(despesaRepository).countVencidas(dataReferencia);
    }

    @Test
    public void testSomarPorPeriodo() {
        LocalDate inicio = LocalDate.of(2025, 1, 1);
        LocalDate fim = LocalDate.of(2025, 1, 31);
        BigDecimal total = new BigDecimal("2000.00");

        when(despesaRepository.sumByPeriodo(inicio, fim)).thenReturn(total);

        BigDecimal resultado = despesaUseCase.somarPorPeriodo(inicio, fim);

        assertNotNull(resultado);
        assertEquals(total, resultado);

        verify(despesaRepository).sumByPeriodo(inicio, fim);
    }

    @Test(expected = BadRequestException.class)
    public void testSomarPorPeriodoDataInvalida() {
        LocalDate inicio = LocalDate.of(2025, 1, 31);
        LocalDate fim = LocalDate.of(2025, 1, 1);

        despesaUseCase.somarPorPeriodo(inicio, fim);
    }
}
