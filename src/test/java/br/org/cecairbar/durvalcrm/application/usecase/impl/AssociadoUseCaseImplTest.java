package br.org.cecairbar.durvalcrm.application.usecase.impl;

import br.org.cecairbar.durvalcrm.application.dto.AssociadoDTO;
import br.org.cecairbar.durvalcrm.application.mapper.AssociadoMapper;
import br.org.cecairbar.durvalcrm.domain.model.Associado;
import br.org.cecairbar.durvalcrm.domain.repository.AssociadoRepository;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AssociadoUseCaseImplTest {

    @InjectMocks
    private AssociadoUseCaseImpl associadoUseCase;

    @Mock
    private AssociadoRepository associadoRepository;

    @Mock
    private AssociadoMapper mapper;

    private UUID id;
    private Associado associado;
    private AssociadoDTO associadoDTO;

    @Before
    public void setUp() {
        id = UUID.randomUUID();
        
        associado = new Associado();
        associado.setId(id);
        associado.setNomeCompleto("João da Silva");
        associado.setCpf("123.456.789-00");
        associado.setEmail("joao@example.com");
        associado.setTelefone("(11) 98765-4321");
        associado.setAtivo(true);
        
        associadoDTO = new AssociadoDTO();
        associadoDTO.setId(id);
        associadoDTO.setNomeCompleto("João da Silva");
        associadoDTO.setCpf("123.456.789-00");
        associadoDTO.setEmail("joao@example.com");
        associadoDTO.setTelefone("(11) 98765-4321");
    }

    @Test
    public void testFindAll() {
        List<Associado> associados = Arrays.asList(associado);
        List<AssociadoDTO> associadosDTO = Arrays.asList(associadoDTO);
        
        when(associadoRepository.findAll(anyString())).thenReturn(associados);
        when(mapper.toDTOList(associados)).thenReturn(associadosDTO);
        
        List<AssociadoDTO> resultado = associadoUseCase.findAll("João");
        
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(associadoDTO, resultado.get(0));
        
        verify(associadoRepository).findAll("João");
        verify(mapper).toDTOList(associados);
    }

    @Test
    public void testFindAllSemFiltro() {
        List<Associado> associados = Arrays.asList(associado);
        List<AssociadoDTO> associadosDTO = Arrays.asList(associadoDTO);
        
        when(associadoRepository.findAll(null)).thenReturn(associados);
        when(mapper.toDTOList(associados)).thenReturn(associadosDTO);
        
        List<AssociadoDTO> resultado = associadoUseCase.findAll(null);
        
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        
        verify(associadoRepository).findAll(null);
        verify(mapper).toDTOList(associados);
    }

    @Test
    public void testFindByIdExistente() {
        when(associadoRepository.findById(id)).thenReturn(Optional.of(associado));
        when(mapper.toDTO(associado)).thenReturn(associadoDTO);
        
        AssociadoDTO resultado = associadoUseCase.findById(id);
        
        assertNotNull(resultado);
        assertEquals(associadoDTO, resultado);
        
        verify(associadoRepository).findById(id);
        verify(mapper).toDTO(associado);
    }

    @Test(expected = NotFoundException.class)
    public void testFindByIdInexistente() {
        when(associadoRepository.findById(id)).thenReturn(Optional.empty());
        
        associadoUseCase.findById(id);
    }

    @Test
    public void testCreateSucesso() {
        AssociadoDTO novoDTO = new AssociadoDTO();
        novoDTO.setNomeCompleto("Maria Santos");
        novoDTO.setCpf("987.654.321-00");
        novoDTO.setEmail("maria@example.com");
        novoDTO.setTelefone("(21) 91234-5678");
        
        Associado novoAssociado = new Associado();
        novoAssociado.setNomeCompleto("Maria Santos");
        novoAssociado.setCpf("987.654.321-00");
        novoAssociado.setEmail("maria@example.com");
        novoAssociado.setTelefone("(21) 91234-5678");
        
        Associado savedAssociado = new Associado();
        savedAssociado.setId(UUID.randomUUID());
        savedAssociado.setNomeCompleto("Maria Santos");
        savedAssociado.setCpf("987.654.321-00");
        savedAssociado.setEmail("maria@example.com");
        savedAssociado.setTelefone("(21) 91234-5678");
        savedAssociado.setAtivo(true);
        
        AssociadoDTO savedDTO = new AssociadoDTO();
        savedDTO.setId(savedAssociado.getId());
        savedDTO.setNomeCompleto("Maria Santos");
        savedDTO.setCpf("987.654.321-00");
        savedDTO.setEmail("maria@example.com");
        savedDTO.setTelefone("(21) 91234-5678");
        
        when(associadoRepository.findByCpf("987.654.321-00")).thenReturn(Optional.empty());
        when(associadoRepository.findByEmail("maria@example.com")).thenReturn(Optional.empty());
        when(mapper.toDomain(novoDTO)).thenReturn(novoAssociado);
        when(associadoRepository.save(any(Associado.class))).thenReturn(savedAssociado);
        when(mapper.toDTO(savedAssociado)).thenReturn(savedDTO);
        
        AssociadoDTO resultado = associadoUseCase.create(novoDTO);
        
        assertNotNull(resultado);
        assertEquals(savedDTO, resultado);
        assertTrue(novoAssociado.isAtivo());
        
        verify(associadoRepository).findByCpf("987.654.321-00");
        verify(associadoRepository).findByEmail("maria@example.com");
        verify(mapper).toDomain(novoDTO);
        verify(associadoRepository).save(novoAssociado);
        verify(mapper).toDTO(savedAssociado);
    }

    @Test(expected = WebApplicationException.class)
    public void testCreateCpfDuplicado() {
        AssociadoDTO novoDTO = new AssociadoDTO();
        novoDTO.setCpf("123.456.789-00");
        novoDTO.setEmail("novo@example.com");
        
        when(associadoRepository.findByCpf("123.456.789-00")).thenReturn(Optional.of(associado));
        
        associadoUseCase.create(novoDTO);
    }

    @Test(expected = WebApplicationException.class)
    public void testCreateEmailDuplicado() {
        AssociadoDTO novoDTO = new AssociadoDTO();
        novoDTO.setCpf("999.999.999-99");
        novoDTO.setEmail("joao@example.com");
        
        when(associadoRepository.findByCpf("999.999.999-99")).thenReturn(Optional.empty());
        when(associadoRepository.findByEmail("joao@example.com")).thenReturn(Optional.of(associado));
        
        associadoUseCase.create(novoDTO);
    }

    @Test
    public void testUpdateSucesso() {
        AssociadoDTO updateDTO = new AssociadoDTO();
        updateDTO.setNomeCompleto("João Silva Atualizado");
        updateDTO.setTelefone("(11) 99999-9999");
        updateDTO.setEmail("joao.novo@example.com");
        
        Associado updatedAssociado = new Associado();
        updatedAssociado.setId(id);
        updatedAssociado.setNomeCompleto("João Silva Atualizado");
        updatedAssociado.setCpf("123.456.789-00");
        updatedAssociado.setEmail("joao.novo@example.com");
        updatedAssociado.setTelefone("(11) 99999-9999");
        updatedAssociado.setAtivo(true);
        
        AssociadoDTO updatedDTO = new AssociadoDTO();
        updatedDTO.setId(id);
        updatedDTO.setNomeCompleto("João Silva Atualizado");
        updatedDTO.setCpf("123.456.789-00");
        updatedDTO.setEmail("joao.novo@example.com");
        updatedDTO.setTelefone("(11) 99999-9999");
        
        when(associadoRepository.findById(id)).thenReturn(Optional.of(associado));
        when(associadoRepository.findByEmail("joao.novo@example.com")).thenReturn(Optional.empty());
        when(associadoRepository.save(any(Associado.class))).thenReturn(updatedAssociado);
        when(mapper.toDTO(updatedAssociado)).thenReturn(updatedDTO);
        
        AssociadoDTO resultado = associadoUseCase.update(id, updateDTO);
        
        assertNotNull(resultado);
        assertEquals("João Silva Atualizado", associado.getNomeCompleto());
        assertEquals("(11) 99999-9999", associado.getTelefone());
        assertEquals("joao.novo@example.com", associado.getEmail());
        
        verify(associadoRepository).findById(id);
        verify(associadoRepository).findByEmail("joao.novo@example.com");
        verify(associadoRepository).save(associado);
        verify(mapper).toDTO(updatedAssociado);
    }

    @Test
    public void testUpdateSemAlterarEmail() {
        AssociadoDTO updateDTO = new AssociadoDTO();
        updateDTO.setNomeCompleto("João Silva Atualizado");
        updateDTO.setTelefone("(11) 99999-9999");
        updateDTO.setEmail(null);
        
        Associado updatedAssociado = new Associado();
        updatedAssociado.setId(id);
        updatedAssociado.setNomeCompleto("João Silva Atualizado");
        updatedAssociado.setCpf("123.456.789-00");
        updatedAssociado.setEmail("joao@example.com");
        updatedAssociado.setTelefone("(11) 99999-9999");
        updatedAssociado.setAtivo(true);
        
        AssociadoDTO updatedDTO = new AssociadoDTO();
        updatedDTO.setId(id);
        updatedDTO.setNomeCompleto("João Silva Atualizado");
        updatedDTO.setCpf("123.456.789-00");
        updatedDTO.setEmail("joao@example.com");
        updatedDTO.setTelefone("(11) 99999-9999");
        
        when(associadoRepository.findById(id)).thenReturn(Optional.of(associado));
        when(associadoRepository.save(any(Associado.class))).thenReturn(updatedAssociado);
        when(mapper.toDTO(updatedAssociado)).thenReturn(updatedDTO);
        
        AssociadoDTO resultado = associadoUseCase.update(id, updateDTO);
        
        assertNotNull(resultado);
        assertEquals("João Silva Atualizado", associado.getNomeCompleto());
        assertEquals("(11) 99999-9999", associado.getTelefone());
        assertEquals("joao@example.com", associado.getEmail()); // Email não alterado
        
        verify(associadoRepository).findById(id);
        verify(associadoRepository, never()).findByEmail(anyString());
        verify(associadoRepository).save(associado);
        verify(mapper).toDTO(updatedAssociado);
    }

    @Test
    public void testUpdateManterMesmoEmail() {
        AssociadoDTO updateDTO = new AssociadoDTO();
        updateDTO.setNomeCompleto("João Silva Atualizado");
        updateDTO.setTelefone("(11) 99999-9999");
        updateDTO.setEmail("joao@example.com"); // Mesmo email
        
        when(associadoRepository.findById(id)).thenReturn(Optional.of(associado));
        when(associadoRepository.findByEmail("joao@example.com")).thenReturn(Optional.of(associado));
        when(associadoRepository.save(any(Associado.class))).thenReturn(associado);
        when(mapper.toDTO(associado)).thenReturn(associadoDTO);
        
        AssociadoDTO resultado = associadoUseCase.update(id, updateDTO);
        
        assertNotNull(resultado);
        
        verify(associadoRepository).findById(id);
        verify(associadoRepository).findByEmail("joao@example.com");
        verify(associadoRepository).save(associado);
    }

    @Test(expected = WebApplicationException.class)
    public void testUpdateEmailDuplicado() {
        AssociadoDTO updateDTO = new AssociadoDTO();
        updateDTO.setEmail("maria@example.com");
        
        Associado outroAssociado = new Associado();
        outroAssociado.setId(UUID.randomUUID());
        outroAssociado.setEmail("maria@example.com");
        
        when(associadoRepository.findById(id)).thenReturn(Optional.of(associado));
        when(associadoRepository.findByEmail("maria@example.com")).thenReturn(Optional.of(outroAssociado));
        
        associadoUseCase.update(id, updateDTO);
    }

    @Test(expected = NotFoundException.class)
    public void testUpdateAssociadoInexistente() {
        AssociadoDTO updateDTO = new AssociadoDTO();
        updateDTO.setNomeCompleto("Nome Atualizado");
        
        when(associadoRepository.findById(id)).thenReturn(Optional.empty());
        
        associadoUseCase.update(id, updateDTO);
    }

    @Test
    public void testDeleteSucesso() {
        when(associadoRepository.findById(id)).thenReturn(Optional.of(associado));
        doNothing().when(associadoRepository).deleteById(id);
        
        associadoUseCase.delete(id);
        
        verify(associadoRepository).findById(id);
        verify(associadoRepository).deleteById(id);
    }

    @Test(expected = NotFoundException.class)
    public void testDeleteAssociadoInexistente() {
        when(associadoRepository.findById(id)).thenReturn(Optional.empty());
        
        associadoUseCase.delete(id);
    }
}