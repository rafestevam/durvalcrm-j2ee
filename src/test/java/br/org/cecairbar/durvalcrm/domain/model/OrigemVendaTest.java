package br.org.cecairbar.durvalcrm.domain.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class OrigemVendaTest {

    @Test
    public void testEnumValues() {
        OrigemVenda[] valores = OrigemVenda.values();
        
        assertEquals(3, valores.length);
        assertEquals(OrigemVenda.CANTINA, valores[0]);
        assertEquals(OrigemVenda.BAZAR, valores[1]);
        assertEquals(OrigemVenda.LIVROS, valores[2]);
    }

    @Test
    public void testValueOf() {
        assertEquals(OrigemVenda.CANTINA, OrigemVenda.valueOf("CANTINA"));
        assertEquals(OrigemVenda.BAZAR, OrigemVenda.valueOf("BAZAR"));
        assertEquals(OrigemVenda.LIVROS, OrigemVenda.valueOf("LIVROS"));
    }

    @Test
    public void testGetDescricao() {
        assertEquals("Cantina", OrigemVenda.CANTINA.getDescricao());
        assertEquals("Bazar", OrigemVenda.BAZAR.getDescricao());
        assertEquals("Livros", OrigemVenda.LIVROS.getDescricao());
    }

    @Test
    public void testGetDetalhe() {
        assertEquals("Vendas realizadas na cantina", OrigemVenda.CANTINA.getDetalhe());
        assertEquals("Vendas realizadas no bazar", OrigemVenda.BAZAR.getDetalhe());
        assertEquals("Vendas de livros", OrigemVenda.LIVROS.getDetalhe());
    }

    @Test
    public void testFromDescricaoValida() {
        assertEquals(OrigemVenda.CANTINA, OrigemVenda.fromDescricao("Cantina"));
        assertEquals(OrigemVenda.BAZAR, OrigemVenda.fromDescricao("BAZAR"));
        assertEquals(OrigemVenda.LIVROS, OrigemVenda.fromDescricao("livros"));
    }

    @Test
    public void testFromDescricaoInvalida() {
        assertNull(OrigemVenda.fromDescricao("INVALID"));
        assertNull(OrigemVenda.fromDescricao(""));
        assertNull(OrigemVenda.fromDescricao(null));
        assertNull(OrigemVenda.fromDescricao("   "));
    }

    @Test
    public void testIsOrigemValida() {
        assertTrue(OrigemVenda.isOrigemValida("Cantina"));
        assertTrue(OrigemVenda.isOrigemValida("BAZAR"));
        assertTrue(OrigemVenda.isOrigemValida("livros"));
        
        assertFalse(OrigemVenda.isOrigemValida("INVALID"));
        assertFalse(OrigemVenda.isOrigemValida(""));
        assertFalse(OrigemVenda.isOrigemValida(null));
        assertFalse(OrigemVenda.isOrigemValida("   "));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfInvalido() {
        OrigemVenda.valueOf("INVALID");
    }
}