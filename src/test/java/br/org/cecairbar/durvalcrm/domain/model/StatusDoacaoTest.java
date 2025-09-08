package br.org.cecairbar.durvalcrm.domain.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class StatusDoacaoTest {

    @Test
    public void testEnumValues() {
        StatusDoacao[] valores = StatusDoacao.values();
        
        assertEquals(4, valores.length);
        assertEquals(StatusDoacao.PENDENTE, valores[0]);
        assertEquals(StatusDoacao.PROCESSANDO, valores[1]);
        assertEquals(StatusDoacao.CONFIRMADA, valores[2]);
        assertEquals(StatusDoacao.CANCELADA, valores[3]);
    }

    @Test
    public void testValueOf() {
        assertEquals(StatusDoacao.PENDENTE, StatusDoacao.valueOf("PENDENTE"));
        assertEquals(StatusDoacao.PROCESSANDO, StatusDoacao.valueOf("PROCESSANDO"));
        assertEquals(StatusDoacao.CONFIRMADA, StatusDoacao.valueOf("CONFIRMADA"));
        assertEquals(StatusDoacao.CANCELADA, StatusDoacao.valueOf("CANCELADA"));
    }

    @Test
    public void testGetDescricao() {
        assertEquals("Pendente", StatusDoacao.PENDENTE.getDescricao());
        assertEquals("Processando", StatusDoacao.PROCESSANDO.getDescricao());
        assertEquals("Confirmada", StatusDoacao.CONFIRMADA.getDescricao());
        assertEquals("Cancelada", StatusDoacao.CANCELADA.getDescricao());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfInvalido() {
        StatusDoacao.valueOf("INVALID");
    }
}