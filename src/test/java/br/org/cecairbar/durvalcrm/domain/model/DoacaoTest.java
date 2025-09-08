package br.org.cecairbar.durvalcrm.domain.model;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.Assert.*;

public class DoacaoTest {

    private Doacao doacao;
    private Associado associado;
    private UUID id;
    private BigDecimal valor;
    private TipoDoacao tipo;
    private StatusDoacao status;
    private String descricao;
    private LocalDateTime dataDoacao;

    @Before
    public void setUp() {
        id = UUID.randomUUID();
        valor = new BigDecimal("100.00");
        tipo = TipoDoacao.UNICA;
        status = StatusDoacao.PENDENTE;
        descricao = "Doação para o centro espírita";
        dataDoacao = LocalDateTime.now();
        
        associado = new Associado();
        associado.setId(UUID.randomUUID());
        associado.setNomeCompleto("João da Silva");
        associado.setCpf("123.456.789-00");
        associado.setEmail("joao@example.com");
        associado.setAtivo(true);
    }

    @Test
    public void testCriarDoacaoVazia() {
        doacao = new Doacao();
        
        assertNotNull(doacao);
        assertNull(doacao.getId());
        assertNull(doacao.getAssociado());
        assertNull(doacao.getValor());
        assertNull(doacao.getTipo());
        assertNull(doacao.getStatus());
        assertNull(doacao.getDescricao());
        assertNull(doacao.getDataDoacao());
        assertNull(doacao.getDataConfirmacao());
        assertNull(doacao.getCodigoTransacao());
        assertNull(doacao.getMetodoPagamento());
        assertNull(doacao.getCreatedAt());
        assertNull(doacao.getUpdatedAt());
    }

    @Test
    public void testCriarDoacaoComParametros() {
        doacao = new Doacao(id, associado, valor, tipo, status, descricao, dataDoacao);
        
        assertEquals(id, doacao.getId());
        assertEquals(associado, doacao.getAssociado());
        assertEquals(valor, doacao.getValor());
        assertEquals(tipo, doacao.getTipo());
        assertEquals(status, doacao.getStatus());
        assertEquals(descricao, doacao.getDescricao());
        assertEquals(dataDoacao, doacao.getDataDoacao());
        assertNull(doacao.getDataConfirmacao());
        assertNull(doacao.getCodigoTransacao());
        assertNull(doacao.getMetodoPagamento());
    }

    @Test
    public void testSettersAndGetters() {
        doacao = new Doacao();
        LocalDateTime dataConfirmacao = LocalDateTime.now();
        String codigoTransacao = "TRX123456";
        MetodoPagamento metodoPagamento = MetodoPagamento.PIX;
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now();
        
        doacao.setId(id);
        doacao.setAssociado(associado);
        doacao.setValor(valor);
        doacao.setTipo(tipo);
        doacao.setStatus(status);
        doacao.setDescricao(descricao);
        doacao.setDataDoacao(dataDoacao);
        doacao.setDataConfirmacao(dataConfirmacao);
        doacao.setCodigoTransacao(codigoTransacao);
        doacao.setMetodoPagamento(metodoPagamento);
        doacao.setCreatedAt(createdAt);
        doacao.setUpdatedAt(updatedAt);
        
        assertEquals(id, doacao.getId());
        assertEquals(associado, doacao.getAssociado());
        assertEquals(valor, doacao.getValor());
        assertEquals(tipo, doacao.getTipo());
        assertEquals(status, doacao.getStatus());
        assertEquals(descricao, doacao.getDescricao());
        assertEquals(dataDoacao, doacao.getDataDoacao());
        assertEquals(dataConfirmacao, doacao.getDataConfirmacao());
        assertEquals(codigoTransacao, doacao.getCodigoTransacao());
        assertEquals(metodoPagamento, doacao.getMetodoPagamento());
        assertEquals(createdAt, doacao.getCreatedAt());
        assertEquals(updatedAt, doacao.getUpdatedAt());
    }

    @Test
    public void testAlterarStatusDoacao() {
        doacao = new Doacao(id, associado, valor, tipo, status, descricao, dataDoacao);
        
        assertEquals(StatusDoacao.PENDENTE, doacao.getStatus());
        
        doacao.setStatus(StatusDoacao.CONFIRMADA);
        assertEquals(StatusDoacao.CONFIRMADA, doacao.getStatus());
        
        doacao.setStatus(StatusDoacao.CANCELADA);
        assertEquals(StatusDoacao.CANCELADA, doacao.getStatus());
    }

    @Test
    public void testAlterarTipoDoacao() {
        doacao = new Doacao(id, associado, valor, tipo, status, descricao, dataDoacao);
        
        assertEquals(TipoDoacao.UNICA, doacao.getTipo());
        
        doacao.setTipo(TipoDoacao.RECORRENTE);
        assertEquals(TipoDoacao.RECORRENTE, doacao.getTipo());
    }

    @Test
    public void testAlterarMetodoPagamento() {
        doacao = new Doacao();
        
        assertNull(doacao.getMetodoPagamento());
        
        doacao.setMetodoPagamento(MetodoPagamento.PIX);
        assertEquals(MetodoPagamento.PIX, doacao.getMetodoPagamento());
        
        doacao.setMetodoPagamento(MetodoPagamento.DINHEIRO);
        assertEquals(MetodoPagamento.DINHEIRO, doacao.getMetodoPagamento());
    }

    @Test
    public void testDoacaoComValorZero() {
        BigDecimal valorZero = BigDecimal.ZERO;
        doacao = new Doacao(id, associado, valorZero, tipo, status, descricao, dataDoacao);
        
        assertEquals(valorZero, doacao.getValor());
    }

    @Test
    public void testDoacaoComValorNegativo() {
        BigDecimal valorNegativo = new BigDecimal("-50.00");
        doacao = new Doacao(id, associado, valorNegativo, tipo, status, descricao, dataDoacao);
        
        assertEquals(valorNegativo, doacao.getValor());
    }

    @Test
    public void testDoacaoSemAssociado() {
        doacao = new Doacao(id, null, valor, tipo, status, descricao, dataDoacao);
        
        assertNull(doacao.getAssociado());
    }

    @Test
    public void testAlterarAssociado() {
        doacao = new Doacao(id, associado, valor, tipo, status, descricao, dataDoacao);
        
        assertEquals(associado, doacao.getAssociado());
        
        Associado novoAssociado = new Associado();
        novoAssociado.setId(UUID.randomUUID());
        novoAssociado.setNomeCompleto("Maria Santos");
        novoAssociado.setCpf("987.654.321-00");
        
        doacao.setAssociado(novoAssociado);
        assertEquals(novoAssociado, doacao.getAssociado());
    }

    @Test
    public void testConfirmarDoacao() {
        doacao = new Doacao(id, associado, valor, tipo, StatusDoacao.PENDENTE, descricao, dataDoacao);
        LocalDateTime dataConfirmacao = LocalDateTime.now();
        String codigoTransacao = "TRX789012";
        
        doacao.setStatus(StatusDoacao.CONFIRMADA);
        doacao.setDataConfirmacao(dataConfirmacao);
        doacao.setCodigoTransacao(codigoTransacao);
        
        assertEquals(StatusDoacao.CONFIRMADA, doacao.getStatus());
        assertEquals(dataConfirmacao, doacao.getDataConfirmacao());
        assertEquals(codigoTransacao, doacao.getCodigoTransacao());
    }

    @Test
    public void testCancelarDoacao() {
        doacao = new Doacao(id, associado, valor, tipo, StatusDoacao.PENDENTE, descricao, dataDoacao);
        
        doacao.setStatus(StatusDoacao.CANCELADA);
        doacao.setUpdatedAt(LocalDateTime.now());
        
        assertEquals(StatusDoacao.CANCELADA, doacao.getStatus());
        assertNotNull(doacao.getUpdatedAt());
    }

    @Test
    public void testDoacaoComDadosCompletos() {
        LocalDateTime dataConfirmacao = LocalDateTime.now().plusHours(1);
        String codigoTransacao = "TRX456789";
        MetodoPagamento metodoPagamento = MetodoPagamento.PIX;
        LocalDateTime createdAt = LocalDateTime.now().minusDays(2);
        LocalDateTime updatedAt = LocalDateTime.now();
        
        doacao = new Doacao(id, associado, valor, tipo, StatusDoacao.CONFIRMADA, descricao, dataDoacao);
        doacao.setDataConfirmacao(dataConfirmacao);
        doacao.setCodigoTransacao(codigoTransacao);
        doacao.setMetodoPagamento(metodoPagamento);
        doacao.setCreatedAt(createdAt);
        doacao.setUpdatedAt(updatedAt);
        
        assertEquals(id, doacao.getId());
        assertEquals(associado, doacao.getAssociado());
        assertEquals(valor, doacao.getValor());
        assertEquals(tipo, doacao.getTipo());
        assertEquals(StatusDoacao.CONFIRMADA, doacao.getStatus());
        assertEquals(descricao, doacao.getDescricao());
        assertEquals(dataDoacao, doacao.getDataDoacao());
        assertEquals(dataConfirmacao, doacao.getDataConfirmacao());
        assertEquals(codigoTransacao, doacao.getCodigoTransacao());
        assertEquals(metodoPagamento, doacao.getMetodoPagamento());
        assertEquals(createdAt, doacao.getCreatedAt());
        assertEquals(updatedAt, doacao.getUpdatedAt());
    }
}