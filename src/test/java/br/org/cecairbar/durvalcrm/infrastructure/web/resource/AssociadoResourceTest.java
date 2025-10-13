package br.org.cecairbar.durvalcrm.infrastructure.web.resource;

import br.org.cecairbar.durvalcrm.application.dto.AssociadoDTO;
import br.org.cecairbar.durvalcrm.application.usecase.AssociadoUseCase;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AssociadoResourceTest {

    @InjectMocks
    private AssociadoResource associadoResource;

    @Mock
    private AssociadoUseCase associadoUseCase;

    private UUID id;
    private AssociadoDTO associadoDTO;

    @Before
    public void setUp() {
        id = UUID.randomUUID();
        
        associadoDTO = new AssociadoDTO();
        associadoDTO.setId(id);
        associadoDTO.setNomeCompleto("João da Silva");
        associadoDTO.setCpf("123.456.789-00");
        associadoDTO.setEmail("joao@example.com");
        associadoDTO.setTelefone("(11) 98765-4321");
    }

    @Test
    public void testFindAll() {
        List<AssociadoDTO> associados = Arrays.asList(associadoDTO);
        when(associadoUseCase.findAll(null)).thenReturn(associados);
        
        Response response = associadoResource.findAll(null);
        
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(associados, response.getEntity());
        
        verify(associadoUseCase).findAll(null);
    }

    @Test
    public void testFindAllComBusca() {
        List<AssociadoDTO> associados = Arrays.asList(associadoDTO);
        String search = "João";
        when(associadoUseCase.findAll(search)).thenReturn(associados);
        
        Response response = associadoResource.findAll(search);
        
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(associados, response.getEntity());
        
        verify(associadoUseCase).findAll(search);
    }

    @Test
    public void testFindAllVazio() {
        List<AssociadoDTO> associados = Arrays.asList();
        when(associadoUseCase.findAll(null)).thenReturn(associados);
        
        Response response = associadoResource.findAll(null);
        
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertTrue(((List<?>) response.getEntity()).isEmpty());
        
        verify(associadoUseCase).findAll(null);
    }

    @Test
    public void testFindById() {
        when(associadoUseCase.findById(id)).thenReturn(associadoDTO);
        
        Response response = associadoResource.findById(id);
        
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(associadoDTO, response.getEntity());
        
        verify(associadoUseCase).findById(id);
    }

    @Test(expected = NotFoundException.class)
    public void testFindByIdInexistente() {
        when(associadoUseCase.findById(id)).thenThrow(new NotFoundException("Associado não encontrado"));
        
        associadoResource.findById(id);
    }

    @Test
    public void testCreate() {
        AssociadoDTO novoDTO = new AssociadoDTO();
        novoDTO.setNomeCompleto("Maria Santos");
        novoDTO.setCpf("987.654.321-00");
        novoDTO.setEmail("maria@example.com");
        novoDTO.setTelefone("(21) 91234-5678");
        
        AssociadoDTO criadoDTO = new AssociadoDTO();
        criadoDTO.setId(UUID.randomUUID());
        criadoDTO.setNomeCompleto("Maria Santos");
        criadoDTO.setCpf("987.654.321-00");
        criadoDTO.setEmail("maria@example.com");
        criadoDTO.setTelefone("(21) 91234-5678");
        
        when(associadoUseCase.create(novoDTO)).thenReturn(criadoDTO);
        
        Response response = associadoResource.create(novoDTO);
        
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(criadoDTO, response.getEntity());
        assertNotNull(response.getLocation());
        assertTrue(response.getLocation().toString().contains("/associados/" + criadoDTO.getId()));
        
        verify(associadoUseCase).create(novoDTO);
    }

    @Test(expected = WebApplicationException.class)
    public void testCreateCpfDuplicado() {
        AssociadoDTO novoDTO = new AssociadoDTO();
        novoDTO.setNomeCompleto("Maria Santos");
        novoDTO.setCpf("123.456.789-00");
        novoDTO.setEmail("maria@example.com");
        
        when(associadoUseCase.create(novoDTO))
            .thenThrow(new WebApplicationException("CPF já cadastrado", Response.Status.CONFLICT));
        
        associadoResource.create(novoDTO);
    }

    @Test(expected = WebApplicationException.class)
    public void testCreateEmailDuplicado() {
        AssociadoDTO novoDTO = new AssociadoDTO();
        novoDTO.setNomeCompleto("Maria Santos");
        novoDTO.setCpf("999.999.999-99");
        novoDTO.setEmail("joao@example.com");
        
        when(associadoUseCase.create(novoDTO))
            .thenThrow(new WebApplicationException("E-mail já cadastrado", Response.Status.CONFLICT));
        
        associadoResource.create(novoDTO);
    }

    @Test
    public void testUpdate() {
        AssociadoDTO updateDTO = new AssociadoDTO();
        updateDTO.setNomeCompleto("João Silva Atualizado");
        updateDTO.setTelefone("(11) 99999-9999");
        
        AssociadoDTO atualizadoDTO = new AssociadoDTO();
        atualizadoDTO.setId(id);
        atualizadoDTO.setNomeCompleto("João Silva Atualizado");
        atualizadoDTO.setCpf("123.456.789-00");
        atualizadoDTO.setEmail("joao@example.com");
        atualizadoDTO.setTelefone("(11) 99999-9999");
        
        when(associadoUseCase.update(id, updateDTO)).thenReturn(atualizadoDTO);
        
        Response response = associadoResource.update(id, updateDTO);
        
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(atualizadoDTO, response.getEntity());
        
        verify(associadoUseCase).update(id, updateDTO);
    }

    @Test(expected = NotFoundException.class)
    public void testUpdateInexistente() {
        AssociadoDTO updateDTO = new AssociadoDTO();
        updateDTO.setNomeCompleto("Nome Atualizado");
        
        when(associadoUseCase.update(id, updateDTO))
            .thenThrow(new NotFoundException("Associado não encontrado"));
        
        associadoResource.update(id, updateDTO);
    }

    @Test(expected = WebApplicationException.class)
    public void testUpdateEmailDuplicado() {
        AssociadoDTO updateDTO = new AssociadoDTO();
        updateDTO.setEmail("maria@example.com");
        
        when(associadoUseCase.update(id, updateDTO))
            .thenThrow(new WebApplicationException("E-mail já cadastrado", Response.Status.CONFLICT));
        
        associadoResource.update(id, updateDTO);
    }

    @Test
    public void testDelete() {
        doNothing().when(associadoUseCase).delete(id);
        
        Response response = associadoResource.delete(id);
        
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        assertNull(response.getEntity());
        
        verify(associadoUseCase).delete(id);
    }

    @Test(expected = NotFoundException.class)
    public void testDeleteInexistente() {
        doThrow(new NotFoundException("Associado não encontrado"))
            .when(associadoUseCase).delete(id);
        
        associadoResource.delete(id);
    }

    @Test
    public void testDeleteComDependencias() {
        doThrow(new WebApplicationException("Associado possui mensalidades vinculadas", 
                Response.Status.CONFLICT))
            .when(associadoUseCase).delete(id);
        
        try {
            associadoResource.delete(id);
            fail("Deveria ter lançado exceção");
        } catch (WebApplicationException e) {
            assertEquals(Response.Status.CONFLICT.getStatusCode(), e.getResponse().getStatus());
            assertTrue(e.getMessage().contains("mensalidades vinculadas"));
        }
        
        verify(associadoUseCase).delete(id);
    }
}