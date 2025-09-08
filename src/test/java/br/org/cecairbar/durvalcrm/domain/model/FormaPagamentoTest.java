package br.org.cecairbar.durvalcrm.domain.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class FormaPagamentoTest {

    @Test
    public void testEnumValues() {
        FormaPagamento[] valores = FormaPagamento.values();
        
        assertEquals(2, valores.length);
        assertEquals(FormaPagamento.PIX, valores[0]);
        assertEquals(FormaPagamento.DINHEIRO, valores[1]);
    }

    @Test
    public void testValueOf() {
        assertEquals(FormaPagamento.PIX, FormaPagamento.valueOf("PIX"));
        assertEquals(FormaPagamento.DINHEIRO, FormaPagamento.valueOf("DINHEIRO"));
    }

    @Test
    public void testGetDescricao() {
        assertEquals("PIX", FormaPagamento.PIX.getDescricao());
        assertEquals("Dinheiro", FormaPagamento.DINHEIRO.getDescricao());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfInvalido() {
        FormaPagamento.valueOf("INVALID");
    }
}