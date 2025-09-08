package br.org.cecairbar.durvalcrm.domain.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class MetodoPagamentoTest {

    @Test
    public void testEnumValues() {
        MetodoPagamento[] valores = MetodoPagamento.values();
        
        assertEquals(2, valores.length);
        assertEquals(MetodoPagamento.PIX, valores[0]);
        assertEquals(MetodoPagamento.DINHEIRO, valores[1]);
    }

    @Test
    public void testValueOf() {
        assertEquals(MetodoPagamento.PIX, MetodoPagamento.valueOf("PIX"));
        assertEquals(MetodoPagamento.DINHEIRO, MetodoPagamento.valueOf("DINHEIRO"));
    }

    @Test
    public void testGetDescricao() {
        assertEquals("PIX", MetodoPagamento.PIX.getDescricao());
        assertEquals("Dinheiro", MetodoPagamento.DINHEIRO.getDescricao());
    }

    @Test
    public void testToString() {
        assertEquals("PIX", MetodoPagamento.PIX.toString());
        assertEquals("Dinheiro", MetodoPagamento.DINHEIRO.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfInvalido() {
        MetodoPagamento.valueOf("INVALID");
    }
}