package br.org.cecairbar.durvalcrm.application.doacao;

import br.org.cecairbar.durvalcrm.domain.model.*;
import br.org.cecairbar.durvalcrm.domain.repository.AssociadoRepository;
import br.org.cecairbar.durvalcrm.domain.repository.DoacaoRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DoacaoServiceTest {

    @InjectMocks
    private DoacaoService doacaoService;

    @Mock
    private DoacaoRepository doacaoRepository;

    @Mock
    private AssociadoRepository associadoRepository;

    @Mock
    private DoacaoMapper mapper;

    private UUID doacaoId;
    private UUID associadoId;
    private Doacao doacao;
    private DoacaoDTO doacaoDTO;
    private Associado associado;

    @Before
    public void setUp() {
        doacaoId = UUID.randomUUID();
        associadoId = UUID.randomUUID();
        
        associado = new Associado();
        associado.setId(associadoId);
        associado.setNomeCompleto("João da Silva");
        associado.setCpf("123.456.789-00");
        associado.setEmail("joao@example.com");
        associado.setAtivo(true);
        
        doacao = new Doacao();
        doacao.setId(doacaoId);
        doacao.setAssociado(associado);
        doacao.setValor(new BigDecimal("100.00"));
        doacao.setTipo(TipoDoacao.UNICA);
        doacao.setStatus(StatusDoacao.PENDENTE);
        doacao.setDescricao("Doação teste");
        doacao.setDataDoacao(LocalDateTime.now());
        
        doacaoDTO = new DoacaoDTO();
        doacaoDTO.setId(doacaoId);
        doacaoDTO.setAssociadoId(associadoId);
        doacaoDTO.setValor(new BigDecimal("100.00"));
        doacaoDTO.setTipo(TipoDoacao.UNICA);
        doacaoDTO.setStatus(StatusDoacao.PENDENTE);
        doacaoDTO.setDescricao("Doação teste");
        doacaoDTO.setDataDoacao(LocalDateTime.now());
    }

    @Test
    public void testListarTodas() {
        List<Doacao> doacoes = Arrays.asList(doacao);
        List<DoacaoDTO> doacoesDTO = Arrays.asList(doacaoDTO);
        
        when(doacaoRepository.findAll()).thenReturn(doacoes);
        when(mapper.toDTOList(doacoes)).thenReturn(doacoesDTO);
        
        List<DoacaoDTO> resultado = doacaoService.listarTodas();
        
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(doacaoDTO, resultado.get(0));
        
        verify(doacaoRepository).findAll();
        verify(mapper).toDTOList(doacoes);
    }

    @Test
    public void testBuscarPorIdExistente() {
        when(doacaoRepository.findById(doacaoId)).thenReturn(Optional.of(doacao));
        when(mapper.toDTO(doacao)).thenReturn(doacaoDTO);
        
        DoacaoDTO resultado = doacaoService.buscarPorId(doacaoId);
        
        assertNotNull(resultado);
        assertEquals(doacaoDTO, resultado);
        
        verify(doacaoRepository).findById(doacaoId);
        verify(mapper).toDTO(doacao);
    }

    @Test
    public void testBuscarPorIdInexistente() {
        when(doacaoRepository.findById(doacaoId)).thenReturn(Optional.empty());
        
        DoacaoDTO resultado = doacaoService.buscarPorId(doacaoId);
        
        assertNull(resultado);
        
        verify(doacaoRepository).findById(doacaoId);
        verify(mapper, never()).toDTO(any());
    }

    @Test
    public void testListarPorAssociado() {
        List<Doacao> doacoes = Arrays.asList(doacao);
        List<DoacaoDTO> doacoesDTO = Arrays.asList(doacaoDTO);
        
        when(doacaoRepository.findByAssociado(associadoId)).thenReturn(doacoes);
        when(mapper.toDTOList(doacoes)).thenReturn(doacoesDTO);
        
        List<DoacaoDTO> resultado = doacaoService.listarPorAssociado(associadoId);
        
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(doacaoDTO, resultado.get(0));
        
        verify(doacaoRepository).findByAssociado(associadoId);
        verify(mapper).toDTOList(doacoes);
    }

    @Test
    public void testListarPorPeriodo() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(7);
        LocalDateTime fim = LocalDateTime.now();
        List<Doacao> doacoes = Arrays.asList(doacao);
        List<DoacaoDTO> doacoesDTO = Arrays.asList(doacaoDTO);
        
        when(doacaoRepository.findByPeriodo(inicio, fim)).thenReturn(doacoes);
        when(mapper.toDTOList(doacoes)).thenReturn(doacoesDTO);
        
        List<DoacaoDTO> resultado = doacaoService.listarPorPeriodo(inicio, fim);
        
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(doacaoDTO, resultado.get(0));
        
        verify(doacaoRepository).findByPeriodo(inicio, fim);
        verify(mapper).toDTOList(doacoes);
    }

    @Test
    public void testCriarComAssociado() {
        when(mapper.toEntity(doacaoDTO)).thenReturn(doacao);
        when(associadoRepository.findById(associadoId)).thenReturn(Optional.of(associado));
        when(doacaoRepository.save(any(Doacao.class))).thenReturn(doacao);
        when(mapper.toDTO(doacao)).thenReturn(doacaoDTO);
        
        DoacaoDTO resultado = doacaoService.criar(doacaoDTO);
        
        assertNotNull(resultado);
        assertEquals(doacaoDTO, resultado);
        
        verify(mapper).toEntity(doacaoDTO);
        verify(associadoRepository).findById(associadoId);
        verify(doacaoRepository).save(doacao);
        verify(mapper).toDTO(doacao);
    }

    @Test
    public void testCriarSemAssociado() {
        doacaoDTO.setAssociadoId(null);
        doacao.setAssociado(null);
        
        when(mapper.toEntity(doacaoDTO)).thenReturn(doacao);
        when(doacaoRepository.save(any(Doacao.class))).thenReturn(doacao);
        when(mapper.toDTO(doacao)).thenReturn(doacaoDTO);
        
        DoacaoDTO resultado = doacaoService.criar(doacaoDTO);
        
        assertNotNull(resultado);
        assertEquals(doacaoDTO, resultado);
        
        verify(mapper).toEntity(doacaoDTO);
        verify(associadoRepository, never()).findById(any());
        verify(doacaoRepository).save(doacao);
        verify(mapper).toDTO(doacao);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCriarComAssociadoInexistente() {
        when(mapper.toEntity(doacaoDTO)).thenReturn(doacao);
        when(associadoRepository.findById(associadoId)).thenReturn(Optional.empty());
        
        doacaoService.criar(doacaoDTO);
    }

    @Test
    public void testAtualizar() {
        when(doacaoRepository.findById(doacaoId)).thenReturn(Optional.of(doacao));
        doAnswer(invocation -> null).when(mapper).updateEntityFromDTO(doacaoDTO, doacao);
        when(doacaoRepository.save(doacao)).thenReturn(doacao);
        when(mapper.toDTO(doacao)).thenReturn(doacaoDTO);
        
        DoacaoDTO resultado = doacaoService.atualizar(doacaoId, doacaoDTO);
        
        assertNotNull(resultado);
        assertEquals(doacaoDTO, resultado);
        
        verify(doacaoRepository).findById(doacaoId);
        verify(mapper).updateEntityFromDTO(doacaoDTO, doacao);
        verify(doacaoRepository).save(doacao);
        verify(mapper).toDTO(doacao);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAtualizarDoacaoInexistente() {
        when(doacaoRepository.findById(doacaoId)).thenReturn(Optional.empty());
        
        doacaoService.atualizar(doacaoId, doacaoDTO);
    }

    @Test
    public void testConfirmarPagamento() {
        String codigoTransacao = "TRX123456";
        MetodoPagamento metodoPagamento = MetodoPagamento.PIX;
        
        when(doacaoRepository.findById(doacaoId)).thenReturn(Optional.of(doacao));
        when(doacaoRepository.save(any(Doacao.class))).thenReturn(doacao);
        when(mapper.toDTO(any(Doacao.class))).thenReturn(doacaoDTO);
        
        DoacaoDTO resultado = doacaoService.confirmarPagamento(doacaoId, codigoTransacao, metodoPagamento);
        
        assertNotNull(resultado);
        assertEquals(StatusDoacao.CONFIRMADA, doacao.getStatus());
        assertEquals(codigoTransacao, doacao.getCodigoTransacao());
        assertEquals(metodoPagamento, doacao.getMetodoPagamento());
        assertNotNull(doacao.getDataConfirmacao());
        
        verify(doacaoRepository).findById(doacaoId);
        verify(doacaoRepository).save(doacao);
        verify(mapper).toDTO(doacao);
    }

    @Test
    public void testConfirmarPagamentoStatusProcessando() {
        doacao.setStatus(StatusDoacao.PROCESSANDO);
        String codigoTransacao = "TRX789012";
        MetodoPagamento metodoPagamento = MetodoPagamento.DINHEIRO;
        
        when(doacaoRepository.findById(doacaoId)).thenReturn(Optional.of(doacao));
        when(doacaoRepository.save(any(Doacao.class))).thenReturn(doacao);
        when(mapper.toDTO(any(Doacao.class))).thenReturn(doacaoDTO);
        
        DoacaoDTO resultado = doacaoService.confirmarPagamento(doacaoId, codigoTransacao, metodoPagamento);
        
        assertNotNull(resultado);
        assertEquals(StatusDoacao.CONFIRMADA, doacao.getStatus());
        
        verify(doacaoRepository).findById(doacaoId);
        verify(doacaoRepository).save(doacao);
    }

    @Test(expected = IllegalStateException.class)
    public void testConfirmarPagamentoDoacaoJaConfirmada() {
        doacao.setStatus(StatusDoacao.CONFIRMADA);
        
        when(doacaoRepository.findById(doacaoId)).thenReturn(Optional.of(doacao));
        
        doacaoService.confirmarPagamento(doacaoId, "TRX999", MetodoPagamento.PIX);
    }

    @Test(expected = IllegalStateException.class)
    public void testConfirmarPagamentoDoacaoCancelada() {
        doacao.setStatus(StatusDoacao.CANCELADA);
        
        when(doacaoRepository.findById(doacaoId)).thenReturn(Optional.of(doacao));
        
        doacaoService.confirmarPagamento(doacaoId, "TRX999", MetodoPagamento.PIX);
    }

    @Test
    public void testCancelar() {
        when(doacaoRepository.findById(doacaoId)).thenReturn(Optional.of(doacao));
        when(doacaoRepository.save(any(Doacao.class))).thenReturn(doacao);
        when(mapper.toDTO(any(Doacao.class))).thenReturn(doacaoDTO);
        
        DoacaoDTO resultado = doacaoService.cancelar(doacaoId);
        
        assertNotNull(resultado);
        assertEquals(StatusDoacao.CANCELADA, doacao.getStatus());
        
        verify(doacaoRepository).findById(doacaoId);
        verify(doacaoRepository).save(doacao);
        verify(mapper).toDTO(doacao);
    }

    @Test(expected = IllegalStateException.class)
    public void testCancelarDoacaoConfirmada() {
        doacao.setStatus(StatusDoacao.CONFIRMADA);
        
        when(doacaoRepository.findById(doacaoId)).thenReturn(Optional.of(doacao));
        
        doacaoService.cancelar(doacaoId);
    }

    @Test
    public void testExcluir() {
        when(doacaoRepository.findById(doacaoId)).thenReturn(Optional.of(doacao));
        doNothing().when(doacaoRepository).delete(doacao);
        
        doacaoService.excluir(doacaoId);
        
        verify(doacaoRepository).findById(doacaoId);
        verify(doacaoRepository).delete(doacao);
    }

    @Test(expected = IllegalStateException.class)
    public void testExcluirDoacaoConfirmada() {
        doacao.setStatus(StatusDoacao.CONFIRMADA);
        
        when(doacaoRepository.findById(doacaoId)).thenReturn(Optional.of(doacao));
        
        doacaoService.excluir(doacaoId);
    }

    @Test
    public void testObterEstatisticas() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(30);
        LocalDateTime fim = LocalDateTime.now();
        
        Doacao doacaoConfirmada1 = criarDoacao(StatusDoacao.CONFIRMADA, new BigDecimal("100.00"));
        Doacao doacaoConfirmada2 = criarDoacao(StatusDoacao.CONFIRMADA, new BigDecimal("200.00"));
        Doacao doacaoPendente = criarDoacao(StatusDoacao.PENDENTE, new BigDecimal("50.00"));
        Doacao doacaoProcessando = criarDoacao(StatusDoacao.PROCESSANDO, new BigDecimal("75.00"));
        Doacao doacaoCancelada = criarDoacao(StatusDoacao.CANCELADA, new BigDecimal("150.00"));
        
        List<Doacao> doacoes = Arrays.asList(
            doacaoConfirmada1, doacaoConfirmada2, doacaoPendente, doacaoProcessando, doacaoCancelada
        );
        
        when(doacaoRepository.findByPeriodo(inicio, fim)).thenReturn(doacoes);
        
        DoacaoEstatisticasDTO estatisticas = doacaoService.obterEstatisticas(inicio, fim);
        
        assertNotNull(estatisticas);
        assertEquals(Long.valueOf(5), estatisticas.getTotalDoacoes());
        assertEquals(Long.valueOf(2), estatisticas.getDoacoesConfirmadas());
        assertEquals(Long.valueOf(2), estatisticas.getDoacoesPendentes());
        assertEquals(Long.valueOf(1), estatisticas.getDoacoesCanceladas());
        assertEquals(new BigDecimal("300.00"), estatisticas.getTotalArrecadado());
        assertEquals(new BigDecimal("150.00"), estatisticas.getTicketMedio());
        
        verify(doacaoRepository).findByPeriodo(inicio, fim);
    }

    @Test
    public void testObterEstatisticasSemDoacoesConfirmadas() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(30);
        LocalDateTime fim = LocalDateTime.now();
        
        Doacao doacaoPendente = criarDoacao(StatusDoacao.PENDENTE, new BigDecimal("50.00"));
        Doacao doacaoCancelada = criarDoacao(StatusDoacao.CANCELADA, new BigDecimal("150.00"));
        
        List<Doacao> doacoes = Arrays.asList(doacaoPendente, doacaoCancelada);
        
        when(doacaoRepository.findByPeriodo(inicio, fim)).thenReturn(doacoes);
        
        DoacaoEstatisticasDTO estatisticas = doacaoService.obterEstatisticas(inicio, fim);
        
        assertNotNull(estatisticas);
        assertEquals(Long.valueOf(2), estatisticas.getTotalDoacoes());
        assertEquals(Long.valueOf(0), estatisticas.getDoacoesConfirmadas());
        assertEquals(Long.valueOf(1), estatisticas.getDoacoesPendentes());
        assertEquals(Long.valueOf(1), estatisticas.getDoacoesCanceladas());
        assertEquals(BigDecimal.ZERO, estatisticas.getTotalArrecadado());
        assertEquals(BigDecimal.ZERO, estatisticas.getTicketMedio());
    }

    @Test
    public void testGerarCodigoPix() {
        when(doacaoRepository.findById(doacaoId)).thenReturn(Optional.of(doacao));
        
        String codigoPix = doacaoService.gerarCodigoPix(doacaoId);
        
        assertNotNull(codigoPix);
        assertTrue(codigoPix.startsWith("00020126580014BR.GOV.BCB.PIX01365199e8c3-"));
        
        verify(doacaoRepository).findById(doacaoId);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGerarCodigoPixDoacaoInexistente() {
        when(doacaoRepository.findById(doacaoId)).thenReturn(Optional.empty());
        
        doacaoService.gerarCodigoPix(doacaoId);
    }

    private Doacao criarDoacao(StatusDoacao status, BigDecimal valor) {
        Doacao d = new Doacao();
        d.setId(UUID.randomUUID());
        d.setStatus(status);
        d.setValor(valor);
        d.setTipo(TipoDoacao.UNICA);
        d.setDataDoacao(LocalDateTime.now());
        return d;
    }
}