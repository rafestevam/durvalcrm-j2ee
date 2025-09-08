package br.org.cecairbar.durvalcrm.domain.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class StatusMensalidadeTest {

    @Test
    public void testEnumValues() {
        StatusMensalidade[] valores = StatusMensalidade.values();
        
        assertEquals(3, valores.length);
        assertEquals(StatusMensalidade.PENDENTE, valores[0]);
        assertEquals(StatusMensalidade.PAGA, valores[1]);
        assertEquals(StatusMensalidade.ATRASADA, valores[2]);
    }

    @Test
    public void testValueOf() {
        assertEquals(StatusMensalidade.PENDENTE, StatusMensalidade.valueOf("PENDENTE"));
        assertEquals(StatusMensalidade.PAGA, StatusMensalidade.valueOf("PAGA"));
        assertEquals(StatusMensalidade.ATRASADA, StatusMensalidade.valueOf("ATRASADA"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfInvalido() {
        StatusMensalidade.valueOf("INVALID");
    }
}