package br.org.cecairbar.durvalcrm.domain.model;

import org.junit.Before;
import org.junit.Test;
import java.util.UUID;

import static org.junit.Assert.*;

public class AssociadoTest {

    private Associado associado;
    private UUID id;

    @Before
    public void setUp() {
        id = UUID.randomUUID();
        associado = new Associado();
    }

    @Test
    public void testCriarAssociadoVazio() {
        assertNotNull(associado);
        assertNull(associado.getId());
        assertNull(associado.getNomeCompleto());
        assertNull(associado.getCpf());
        assertNull(associado.getEmail());
        assertNull(associado.getTelefone());
        assertFalse(associado.isAtivo());
    }

    @Test
    public void testCriarAssociadoCompleto() {
        String nomeCompleto = "João da Silva";
        String cpf = "123.456.789-00";
        String email = "joao@example.com";
        String telefone = "(11) 98765-4321";
        boolean ativo = true;

        Associado associadoCompleto = new Associado(id, nomeCompleto, cpf, email, telefone, ativo);

        assertEquals(id, associadoCompleto.getId());
        assertEquals(nomeCompleto, associadoCompleto.getNomeCompleto());
        assertEquals(cpf, associadoCompleto.getCpf());
        assertEquals(email, associadoCompleto.getEmail());
        assertEquals(telefone, associadoCompleto.getTelefone());
        assertTrue(associadoCompleto.isAtivo());
    }

    @Test
    public void testSettersAndGetters() {
        String nomeCompleto = "Maria Santos";
        String cpf = "987.654.321-00";
        String email = "maria@example.com";
        String telefone = "(21) 91234-5678";

        associado.setId(id);
        associado.setNomeCompleto(nomeCompleto);
        associado.setCpf(cpf);
        associado.setEmail(email);
        associado.setTelefone(telefone);
        associado.setAtivo(true);

        assertEquals(id, associado.getId());
        assertEquals(nomeCompleto, associado.getNomeCompleto());
        assertEquals(cpf, associado.getCpf());
        assertEquals(email, associado.getEmail());
        assertEquals(telefone, associado.getTelefone());
        assertTrue(associado.isAtivo());
    }

    @Test
    public void testEqualsAndHashCode() {
        Associado associado1 = new Associado(id, "João", "123.456.789-00", "joao@example.com", "(11) 98765-4321", true);
        Associado associado2 = new Associado(id, "João", "123.456.789-00", "joao@example.com", "(11) 98765-4321", true);
        Associado associado3 = new Associado(UUID.randomUUID(), "Maria", "987.654.321-00", "maria@example.com", "(21) 91234-5678", false);

        assertEquals(associado1, associado2);
        assertNotEquals(associado1, associado3);
        assertEquals(associado1.hashCode(), associado2.hashCode());
        assertNotEquals(associado1.hashCode(), associado3.hashCode());
    }

    @Test
    public void testToString() {
        associado.setId(id);
        associado.setNomeCompleto("João da Silva");
        associado.setCpf("123.456.789-00");
        associado.setEmail("joao@example.com");
        associado.setTelefone("(11) 98765-4321");
        associado.setAtivo(true);

        String toString = associado.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("João da Silva"));
        assertTrue(toString.contains("123.456.789-00"));
    }

    @Test
    public void testAlterarStatusAtivo() {
        associado.setAtivo(false);
        assertFalse(associado.isAtivo());

        associado.setAtivo(true);
        assertTrue(associado.isAtivo());
    }

    @Test
    public void testAssociadoComDadosNulos() {
        associado.setId(id);
        associado.setNomeCompleto(null);
        associado.setCpf(null);
        associado.setEmail(null);
        associado.setTelefone(null);

        assertEquals(id, associado.getId());
        assertNull(associado.getNomeCompleto());
        assertNull(associado.getCpf());
        assertNull(associado.getEmail());
        assertNull(associado.getTelefone());
    }
}