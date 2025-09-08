package br.org.cecairbar.durvalcrm.domain.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class TipoDoacaoTest {

    @Test
    public void testEnumValues() {
        TipoDoacao[] valores = TipoDoacao.values();
        
        assertEquals(2, valores.length);
        assertEquals(TipoDoacao.UNICA, valores[0]);
        assertEquals(TipoDoacao.RECORRENTE, valores[1]);
    }

    @Test
    public void testValueOf() {
        assertEquals(TipoDoacao.UNICA, TipoDoacao.valueOf("UNICA"));
        assertEquals(TipoDoacao.RECORRENTE, TipoDoacao.valueOf("RECORRENTE"));
    }

    @Test
    public void testGetDescricao() {
        assertEquals("Doação Única", TipoDoacao.UNICA.getDescricao());
        assertEquals("Doação Recorrente", TipoDoacao.RECORRENTE.getDescricao());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfInvalido() {
        TipoDoacao.valueOf("INVALID");
    }
}