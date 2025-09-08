package br.org.cecairbar.durvalcrm.domain.model;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.Assert.*;

public class VendaTest {

    private Venda venda;
    private String descricao;
    private BigDecimal valor;
    private OrigemVenda origem;
    private FormaPagamento formaPagamento;

    @Before
    public void setUp() {
        descricao = "Venda de produto";
        valor = new BigDecimal("150.00");
        origem = OrigemVenda.CANTINA;
        formaPagamento = FormaPagamento.PIX;
    }

    @Test
    public void testCriarVenda() {
        venda = Venda.criar(descricao, valor, origem, formaPagamento);

        assertNotNull(venda);
        assertNotNull(venda.getId());
        assertEquals(descricao, venda.getDescricao());
        assertEquals(valor, venda.getValor());
        assertEquals(origem, venda.getOrigem());
        assertEquals(formaPagamento, venda.getFormaPagamento());
        assertNotNull(venda.getDataVenda());
        assertNotNull(venda.getCriadoEm());
        assertNull(venda.getAtualizadoEm());
    }

    @Test
    public void testVendaComBuilder() {
        UUID id = UUID.randomUUID();
        Instant agora = Instant.now();
        
        venda = Venda.builder()
                .id(id)
                .descricao(descricao)
                .valor(valor)
                .origem(origem)
                .formaPagamento(formaPagamento)
                .dataVenda(agora)
                .criadoEm(agora)
                .build();

        assertEquals(id, venda.getId());
        assertEquals(descricao, venda.getDescricao());
        assertEquals(valor, venda.getValor());
        assertEquals(origem, venda.getOrigem());
        assertEquals(formaPagamento, venda.getFormaPagamento());
        assertEquals(agora, venda.getDataVenda());
        assertEquals(agora, venda.getCriadoEm());
    }

    @Test
    public void testAtualizarVenda() {
        venda = Venda.criar(descricao, valor, origem, formaPagamento);
        
        String novaDescricao = "Venda atualizada";
        BigDecimal novoValor = new BigDecimal("200.00");
        OrigemVenda novaOrigem = OrigemVenda.BAZAR;
        FormaPagamento novaFormaPagamento = FormaPagamento.DINHEIRO;

        venda.atualizar(novaDescricao, novoValor, novaOrigem, novaFormaPagamento);

        assertEquals(novaDescricao, venda.getDescricao());
        assertEquals(novoValor, venda.getValor());
        assertEquals(novaOrigem, venda.getOrigem());
        assertEquals(novaFormaPagamento, venda.getFormaPagamento());
        assertNotNull(venda.getAtualizadoEm());
    }

    @Test
    public void testAtualizarVendaComValoresNulos() {
        venda = Venda.criar(descricao, valor, origem, formaPagamento);
        
        venda.atualizar(null, null, null, null);

        assertEquals(descricao, venda.getDescricao());
        assertEquals(valor, venda.getValor());
        assertEquals(origem, venda.getOrigem());
        assertEquals(formaPagamento, venda.getFormaPagamento());
        assertNotNull(venda.getAtualizadoEm());
    }

    @Test
    public void testAtualizarVendaComDescricaoVazia() {
        venda = Venda.criar(descricao, valor, origem, formaPagamento);
        
        venda.atualizar("", null, null, null);

        assertEquals(descricao, venda.getDescricao());
    }

    @Test
    public void testAtualizarVendaComValorZero() {
        venda = Venda.criar(descricao, valor, origem, formaPagamento);
        
        venda.atualizar(null, BigDecimal.ZERO, null, null);

        assertEquals(valor, venda.getValor());
    }

    @Test
    public void testAtualizarVendaComValorNegativo() {
        venda = Venda.criar(descricao, valor, origem, formaPagamento);
        
        venda.atualizar(null, new BigDecimal("-10.00"), null, null);

        assertEquals(valor, venda.getValor());
    }

    @Test
    public void testIsValidaComVendaCompleta() {
        venda = Venda.criar(descricao, valor, origem, formaPagamento);
        
        assertTrue(venda.isValida());
    }

    @Test
    public void testIsValidaComVendaIncompleta() {
        venda = new Venda();
        
        assertFalse(venda.isValida());
    }

    @Test
    public void testIsValidaComIdNulo() {
        venda = Venda.builder()
                .id(null)
                .descricao(descricao)
                .valor(valor)
                .origem(origem)
                .formaPagamento(formaPagamento)
                .dataVenda(Instant.now())
                .criadoEm(Instant.now())
                .build();
        
        assertFalse(venda.isValida());
    }

    @Test
    public void testIsValidaComDescricaoNula() {
        venda = Venda.builder()
                .id(UUID.randomUUID())
                .descricao(null)
                .valor(valor)
                .origem(origem)
                .formaPagamento(formaPagamento)
                .dataVenda(Instant.now())
                .criadoEm(Instant.now())
                .build();
        
        assertFalse(venda.isValida());
    }

    @Test
    public void testIsValidaComDescricaoVazia() {
        venda = Venda.builder()
                .id(UUID.randomUUID())
                .descricao("   ")
                .valor(valor)
                .origem(origem)
                .formaPagamento(formaPagamento)
                .dataVenda(Instant.now())
                .criadoEm(Instant.now())
                .build();
        
        assertFalse(venda.isValida());
    }

    @Test
    public void testIsValidaComValorNulo() {
        venda = Venda.builder()
                .id(UUID.randomUUID())
                .descricao(descricao)
                .valor(null)
                .origem(origem)
                .formaPagamento(formaPagamento)
                .dataVenda(Instant.now())
                .criadoEm(Instant.now())
                .build();
        
        assertFalse(venda.isValida());
    }

    @Test
    public void testIsValidaComValorZero() {
        venda = Venda.builder()
                .id(UUID.randomUUID())
                .descricao(descricao)
                .valor(BigDecimal.ZERO)
                .origem(origem)
                .formaPagamento(formaPagamento)
                .dataVenda(Instant.now())
                .criadoEm(Instant.now())
                .build();
        
        assertFalse(venda.isValida());
    }

    @Test
    public void testIsValidaComOrigemNula() {
        venda = Venda.builder()
                .id(UUID.randomUUID())
                .descricao(descricao)
                .valor(valor)
                .origem(null)
                .formaPagamento(formaPagamento)
                .dataVenda(Instant.now())
                .criadoEm(Instant.now())
                .build();
        
        assertFalse(venda.isValida());
    }

    @Test
    public void testIsValidaComFormaPagamentoNula() {
        venda = Venda.builder()
                .id(UUID.randomUUID())
                .descricao(descricao)
                .valor(valor)
                .origem(origem)
                .formaPagamento(null)
                .dataVenda(Instant.now())
                .criadoEm(Instant.now())
                .build();
        
        assertFalse(venda.isValida());
    }

    @Test
    public void testIsValidaComDataVendaNula() {
        venda = Venda.builder()
                .id(UUID.randomUUID())
                .descricao(descricao)
                .valor(valor)
                .origem(origem)
                .formaPagamento(formaPagamento)
                .dataVenda(null)
                .criadoEm(Instant.now())
                .build();
        
        assertFalse(venda.isValida());
    }

    @Test
    public void testIsValidaComCriadoEmNulo() {
        venda = Venda.builder()
                .id(UUID.randomUUID())
                .descricao(descricao)
                .valor(valor)
                .origem(origem)
                .formaPagamento(formaPagamento)
                .dataVenda(Instant.now())
                .criadoEm(null)
                .build();
        
        assertFalse(venda.isValida());
    }

    @Test
    public void testEqualsAndHashCode() {
        UUID id = UUID.randomUUID();
        Instant agora = Instant.now();
        
        Venda venda1 = Venda.builder()
                .id(id)
                .descricao(descricao)
                .valor(valor)
                .origem(origem)
                .formaPagamento(formaPagamento)
                .dataVenda(agora)
                .criadoEm(agora)
                .build();
                
        Venda venda2 = Venda.builder()
                .id(id)
                .descricao(descricao)
                .valor(valor)
                .origem(origem)
                .formaPagamento(formaPagamento)
                .dataVenda(agora)
                .criadoEm(agora)
                .build();
                
        Venda venda3 = Venda.criar("Outra venda", new BigDecimal("50.00"), OrigemVenda.LIVROS, FormaPagamento.PIX);
        
        assertEquals(venda1, venda2);
        assertNotEquals(venda1, venda3);
        assertEquals(venda1.hashCode(), venda2.hashCode());
        assertNotEquals(venda1.hashCode(), venda3.hashCode());
    }
}