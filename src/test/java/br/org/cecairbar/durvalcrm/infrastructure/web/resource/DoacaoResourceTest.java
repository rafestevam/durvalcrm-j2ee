package br.org.cecairbar.durvalcrm.infrastructure.web.resource;

import br.org.cecairbar.durvalcrm.application.doacao.DoacaoDTO;
import br.org.cecairbar.durvalcrm.application.doacao.DoacaoEstatisticasDTO;
import br.org.cecairbar.durvalcrm.application.doacao.DoacaoService;
import br.org.cecairbar.durvalcrm.domain.model.MetodoPagamento;
import br.org.cecairbar.durvalcrm.domain.model.StatusDoacao;
import br.org.cecairbar.durvalcrm.domain.model.TipoDoacao;
import jakarta.ws.rs.core.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DoacaoResourceTest {

    @InjectMocks
    private DoacaoResource doacaoResource;

    @Mock
    private DoacaoService doacaoService;

    private UUID doacaoId;
    private UUID associadoId;
    private DoacaoDTO doacaoDTO;

    @Before
    public void setUp() {
        doacaoId = UUID.randomUUID();
        associadoId = UUID.randomUUID();
        
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
    public void testListar() {
        List<DoacaoDTO> doacoes = Arrays.asList(doacaoDTO);
        when(doacaoService.listarTodas()).thenReturn(doacoes);
        
        Response response = doacaoResource.listar();
        
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(doacoes, response.getEntity());
        verify(doacaoService).listarTodas();
    }

    @Test
    public void testBuscarPorIdExistente() {
        when(doacaoService.buscarPorId(doacaoId)).thenReturn(doacaoDTO);
        
        Response response = doacaoResource.buscarPorId(doacaoId);
        
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(doacaoDTO, response.getEntity());
        verify(doacaoService).buscarPorId(doacaoId);
    }

    @Test
    public void testBuscarPorIdInexistente() {
        when(doacaoService.buscarPorId(doacaoId)).thenReturn(null);
        
        Response response = doacaoResource.buscarPorId(doacaoId);
        
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        verify(doacaoService).buscarPorId(doacaoId);
    }

    @Test
    public void testListarPorAssociado() {
        List<DoacaoDTO> doacoes = Arrays.asList(doacaoDTO);
        when(doacaoService.listarPorAssociado(associadoId)).thenReturn(doacoes);
        
        Response response = doacaoResource.listarPorAssociado(associadoId);
        
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(doacoes, response.getEntity());
        verify(doacaoService).listarPorAssociado(associadoId);
    }

    @Test
    public void testListarPorPeriodo() {
        String inicioStr = "2024-01-01T00:00:00";
        String fimStr = "2024-12-31T23:59:59";
        List<DoacaoDTO> doacoes = Arrays.asList(doacaoDTO);
        
        when(doacaoService.listarPorPeriodo(any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(doacoes);
        
        Response response = doacaoResource.listarPorPeriodo(inicioStr, fimStr);
        
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(doacoes, response.getEntity());
        verify(doacaoService).listarPorPeriodo(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    public void testCriarSucesso() {
        when(doacaoService.criar(doacaoDTO)).thenReturn(doacaoDTO);
        
        Response response = doacaoResource.criar(doacaoDTO);
        
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(doacaoDTO, response.getEntity());
        verify(doacaoService).criar(doacaoDTO);
    }

    @Test
    public void testCriarComErro() {
        when(doacaoService.criar(doacaoDTO))
            .thenThrow(new IllegalArgumentException("Associado não encontrado"));
        
        Response response = doacaoResource.criar(doacaoDTO);
        
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        verify(doacaoService).criar(doacaoDTO);
    }

    @Test
    public void testAtualizarSucesso() {
        when(doacaoService.atualizar(doacaoId, doacaoDTO)).thenReturn(doacaoDTO);
        
        Response response = doacaoResource.atualizar(doacaoId, doacaoDTO);
        
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(doacaoDTO, response.getEntity());
        verify(doacaoService).atualizar(doacaoId, doacaoDTO);
    }

    @Test
    public void testAtualizarComErro() {
        when(doacaoService.atualizar(doacaoId, doacaoDTO))
            .thenThrow(new IllegalArgumentException("Doação não encontrada"));
        
        Response response = doacaoResource.atualizar(doacaoId, doacaoDTO);
        
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        verify(doacaoService).atualizar(doacaoId, doacaoDTO);
    }

    @Test
    public void testConfirmarPagamentoSucesso() {
        DoacaoResource.ConfirmarPagamentoRequest request = new DoacaoResource.ConfirmarPagamentoRequest();
        request.setCodigoTransacao("TRX123");
        request.setMetodoPagamento("PIX");
        
        when(doacaoService.confirmarPagamento(doacaoId, "TRX123", MetodoPagamento.PIX))
            .thenReturn(doacaoDTO);
        
        Response response = doacaoResource.confirmarPagamento(doacaoId, request);
        
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(doacaoDTO, response.getEntity());
        verify(doacaoService).confirmarPagamento(doacaoId, "TRX123", MetodoPagamento.PIX);
    }

    @Test
    public void testConfirmarPagamentoNaoEncontrado() {
        DoacaoResource.ConfirmarPagamentoRequest request = new DoacaoResource.ConfirmarPagamentoRequest();
        request.setCodigoTransacao("TRX123");
        request.setMetodoPagamento("PIX");
        
        when(doacaoService.confirmarPagamento(doacaoId, "TRX123", MetodoPagamento.PIX))
            .thenThrow(new IllegalArgumentException("Doação não encontrada"));
        
        Response response = doacaoResource.confirmarPagamento(doacaoId, request);
        
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void testConfirmarPagamentoStatusInvalido() {
        DoacaoResource.ConfirmarPagamentoRequest request = new DoacaoResource.ConfirmarPagamentoRequest();
        request.setCodigoTransacao("TRX123");
        request.setMetodoPagamento("PIX");
        
        when(doacaoService.confirmarPagamento(doacaoId, "TRX123", MetodoPagamento.PIX))
            .thenThrow(new IllegalStateException("Doação já confirmada"));
        
        Response response = doacaoResource.confirmarPagamento(doacaoId, request);
        
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void testCancelarSucesso() {
        when(doacaoService.cancelar(doacaoId)).thenReturn(doacaoDTO);
        
        Response response = doacaoResource.cancelar(doacaoId);
        
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(doacaoDTO, response.getEntity());
        verify(doacaoService).cancelar(doacaoId);
    }

    @Test
    public void testCancelarNaoEncontrado() {
        when(doacaoService.cancelar(doacaoId))
            .thenThrow(new IllegalArgumentException("Doação não encontrada"));
        
        Response response = doacaoResource.cancelar(doacaoId);
        
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void testCancelarStatusInvalido() {
        when(doacaoService.cancelar(doacaoId))
            .thenThrow(new IllegalStateException("Doação não pode ser cancelada"));
        
        Response response = doacaoResource.cancelar(doacaoId);
        
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void testExcluirSucesso() {
        doNothing().when(doacaoService).excluir(doacaoId);
        
        Response response = doacaoResource.excluir(doacaoId);
        
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(doacaoService).excluir(doacaoId);
    }

    @Test
    public void testExcluirNaoEncontrado() {
        doThrow(new IllegalArgumentException("Doação não encontrada"))
            .when(doacaoService).excluir(doacaoId);
        
        Response response = doacaoResource.excluir(doacaoId);
        
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void testExcluirStatusInvalido() {
        doThrow(new IllegalStateException("Doação não pode ser excluída"))
            .when(doacaoService).excluir(doacaoId);
        
        Response response = doacaoResource.excluir(doacaoId);
        
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void testObterEstatisticas() {
        String inicioStr = "2024-01-01T00:00:00";
        String fimStr = "2024-12-31T23:59:59";
        DoacaoEstatisticasDTO estatisticas = new DoacaoEstatisticasDTO();
        
        when(doacaoService.obterEstatisticas(any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(estatisticas);
        
        Response response = doacaoResource.obterEstatisticas(inicioStr, fimStr);
        
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(estatisticas, response.getEntity());
        verify(doacaoService).obterEstatisticas(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    public void testGerarCodigoPixSucesso() {
        String codigoPix = "00020126580014BR.GOV.BCB.PIX...";
        when(doacaoService.gerarCodigoPix(doacaoId)).thenReturn(codigoPix);
        
        Response response = doacaoResource.gerarCodigoPix(doacaoId);
        
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        
        DoacaoResource.PixResponse pixResponse = (DoacaoResource.PixResponse) response.getEntity();
        assertEquals(codigoPix, pixResponse.getCodigoPix());
        verify(doacaoService).gerarCodigoPix(doacaoId);
    }

    @Test
    public void testGerarCodigoPixNaoEncontrado() {
        when(doacaoService.gerarCodigoPix(doacaoId))
            .thenThrow(new IllegalArgumentException("Doação não encontrada"));
        
        Response response = doacaoResource.gerarCodigoPix(doacaoId);
        
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void testConfirmarPagamentoRequestGettersSetters() {
        DoacaoResource.ConfirmarPagamentoRequest request = new DoacaoResource.ConfirmarPagamentoRequest();
        
        request.setCodigoTransacao("TRX123");
        request.setMetodoPagamento("PIX");
        
        assertEquals("TRX123", request.getCodigoTransacao());
        assertEquals("PIX", request.getMetodoPagamento());
    }

    @Test
    public void testPixResponseGettersSetters() {
        String codigoPix = "00020126580014BR.GOV.BCB.PIX...";
        DoacaoResource.PixResponse response = new DoacaoResource.PixResponse(codigoPix);
        
        assertEquals(codigoPix, response.getCodigoPix());
        
        String novoCodigoPix = "00020126580014BR.GOV.BCB.PIX...NEW";
        response.setCodigoPix(novoCodigoPix);
        assertEquals(novoCodigoPix, response.getCodigoPix());
    }

    @Test
    public void testErrorResponseGettersSetters() {
        String message = "Erro teste";
        DoacaoResource.ErrorResponse response = new DoacaoResource.ErrorResponse(message);
        
        assertEquals(message, response.getMessage());
        
        String newMessage = "Nova mensagem de erro";
        response.setMessage(newMessage);
        assertEquals(newMessage, response.getMessage());
    }
}