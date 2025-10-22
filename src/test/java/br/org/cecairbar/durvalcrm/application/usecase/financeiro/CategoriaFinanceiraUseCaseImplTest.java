package br.org.cecairbar.durvalcrm.application.usecase.financeiro;

import br.org.cecairbar.durvalcrm.application.dto.CategoriaFinanceiraDTO;
import br.org.cecairbar.durvalcrm.application.financeiro.CategoriaFinanceiraMapper;
import br.org.cecairbar.durvalcrm.domain.model.CategoriaFinanceira;
import br.org.cecairbar.durvalcrm.domain.model.TipoCategoriaFinanceira;
import br.org.cecairbar.durvalcrm.domain.repository.CategoriaFinanceiraRepository;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

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
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CategoriaFinanceiraUseCaseImplTest {

    @InjectMocks
    private CategoriaFinanceiraUseCaseImpl categoriaUseCase;

    @Mock
    private CategoriaFinanceiraRepository categoriaRepository;

    @Mock
    private CategoriaFinanceiraMapper mapper;

    private UUID id;
    private CategoriaFinanceira categoria;
    private CategoriaFinanceiraDTO categoriaDTO;

    @Before
    public void setUp() {
        id = UUID.randomUUID();

        categoria = new CategoriaFinanceira();
        categoria.setId(id);
        categoria.setNome("Mensalidades");
        categoria.setDescricao("Categoria para mensalidades de associados");
        categoria.setTipo(TipoCategoriaFinanceira.RECEITA);
        categoria.setAtiva(true);
        categoria.setCor("#00FF00");

        categoriaDTO = new CategoriaFinanceiraDTO();
        categoriaDTO.setId(id);
        categoriaDTO.setNome("Mensalidades");
        categoriaDTO.setDescricao("Categoria para mensalidades de associados");
        categoriaDTO.setTipo(TipoCategoriaFinanceira.RECEITA);
        categoriaDTO.setAtiva(true);
        categoriaDTO.setCor("#00FF00");
    }

    @Test
    public void testCriarSucesso() {
        CategoriaFinanceiraDTO novoDTO = new CategoriaFinanceiraDTO();
        novoDTO.setNome("Doações");
        novoDTO.setTipo(TipoCategoriaFinanceira.RECEITA);

        CategoriaFinanceira novaCategoria = new CategoriaFinanceira();
        novaCategoria.setNome("Doações");
        novaCategoria.setTipo(TipoCategoriaFinanceira.RECEITA);

        when(categoriaRepository.existsByNome("Doações")).thenReturn(false);
        when(mapper.toDomain(novoDTO)).thenReturn(novaCategoria);
        when(categoriaRepository.save(novaCategoria)).thenReturn(novaCategoria);
        when(mapper.toDTO(novaCategoria)).thenReturn(novoDTO);

        CategoriaFinanceiraDTO resultado = categoriaUseCase.criar(novoDTO);

        assertNotNull(resultado);
        assertEquals("Doações", resultado.getNome());

        verify(categoriaRepository).existsByNome("Doações");
        verify(mapper).toDomain(novoDTO);
        verify(categoriaRepository).save(novaCategoria);
        verify(mapper).toDTO(novaCategoria);
    }

    @Test(expected = BadRequestException.class)
    public void testCriarNomeDuplicado() {
        CategoriaFinanceiraDTO novoDTO = new CategoriaFinanceiraDTO();
        novoDTO.setNome("Mensalidades");

        when(categoriaRepository.existsByNome("Mensalidades")).thenReturn(true);

        categoriaUseCase.criar(novoDTO);
    }

    @Test
    public void testBuscarPorIdExistente() {
        when(categoriaRepository.findById(id)).thenReturn(Optional.of(categoria));
        when(mapper.toDTO(categoria)).thenReturn(categoriaDTO);

        CategoriaFinanceiraDTO resultado = categoriaUseCase.buscarPorId(id);

        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        assertEquals("Mensalidades", resultado.getNome());

        verify(categoriaRepository).findById(id);
        verify(mapper).toDTO(categoria);
    }

    @Test(expected = NotFoundException.class)
    public void testBuscarPorIdInexistente() {
        when(categoriaRepository.findById(id)).thenReturn(Optional.empty());

        categoriaUseCase.buscarPorId(id);
    }

    @Test
    public void testAtualizarSucesso() {
        CategoriaFinanceiraDTO updateDTO = new CategoriaFinanceiraDTO();
        updateDTO.setNome("Mensalidades Atualizadas");
        updateDTO.setDescricao("Descrição atualizada");

        when(categoriaRepository.findById(id)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.existsByNome("Mensalidades Atualizadas")).thenReturn(false);
        when(categoriaRepository.save(categoria)).thenReturn(categoria);
        when(mapper.toDTO(categoria)).thenReturn(categoriaDTO);

        CategoriaFinanceiraDTO resultado = categoriaUseCase.atualizar(id, updateDTO);

        assertNotNull(resultado);

        verify(categoriaRepository).findById(id);
        verify(categoriaRepository).existsByNome("Mensalidades Atualizadas");
        verify(mapper).updateDomainFromDTO(updateDTO, categoria);
        verify(categoriaRepository).save(categoria);
    }

    @Test(expected = BadRequestException.class)
    public void testAtualizarNomeDuplicado() {
        CategoriaFinanceiraDTO updateDTO = new CategoriaFinanceiraDTO();
        updateDTO.setNome("Doações");

        when(categoriaRepository.findById(id)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.existsByNome("Doações")).thenReturn(true);

        categoriaUseCase.atualizar(id, updateDTO);
    }

    @Test
    public void testDesativarSucesso() {
        when(categoriaRepository.findById(id)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.save(categoria)).thenReturn(categoria);

        categoriaUseCase.desativar(id);

        assertFalse(categoria.getAtiva());

        verify(categoriaRepository).findById(id);
        verify(categoriaRepository).save(categoria);
    }

    @Test(expected = BadRequestException.class)
    public void testDesativarCategoriaJaDesativada() {
        categoria.setAtiva(false);

        when(categoriaRepository.findById(id)).thenReturn(Optional.of(categoria));

        categoriaUseCase.desativar(id);
    }

    @Test
    public void testReativarSucesso() {
        categoria.setAtiva(false);

        when(categoriaRepository.findById(id)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.save(categoria)).thenReturn(categoria);
        when(mapper.toDTO(categoria)).thenReturn(categoriaDTO);

        CategoriaFinanceiraDTO resultado = categoriaUseCase.reativar(id);

        assertNotNull(resultado);
        assertTrue(categoria.getAtiva());

        verify(categoriaRepository).findById(id);
        verify(categoriaRepository).save(categoria);
        verify(mapper).toDTO(categoria);
    }

    @Test(expected = BadRequestException.class)
    public void testReativarCategoriaJaAtiva() {
        when(categoriaRepository.findById(id)).thenReturn(Optional.of(categoria));

        categoriaUseCase.reativar(id);
    }

    @Test
    public void testListarTodas() {
        List<CategoriaFinanceira> categorias = Arrays.asList(categoria);
        List<CategoriaFinanceiraDTO> categoriasDTO = Arrays.asList(categoriaDTO);

        when(categoriaRepository.findAll()).thenReturn(categorias);
        when(mapper.toDTOList(categorias)).thenReturn(categoriasDTO);

        List<CategoriaFinanceiraDTO> resultado = categoriaUseCase.listarTodas();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());

        verify(categoriaRepository).findAll();
        verify(mapper).toDTOList(categorias);
    }

    @Test
    public void testListarAtivas() {
        List<CategoriaFinanceira> categorias = Arrays.asList(categoria);
        List<CategoriaFinanceiraDTO> categoriasDTO = Arrays.asList(categoriaDTO);

        when(categoriaRepository.findByAtiva(true)).thenReturn(categorias);
        when(mapper.toDTOList(categorias)).thenReturn(categoriasDTO);

        List<CategoriaFinanceiraDTO> resultado = categoriaUseCase.listarAtivas();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());

        verify(categoriaRepository).findByAtiva(true);
        verify(mapper).toDTOList(categorias);
    }

    @Test
    public void testListarPorTipo() {
        List<CategoriaFinanceira> categorias = Arrays.asList(categoria);
        List<CategoriaFinanceiraDTO> categoriasDTO = Arrays.asList(categoriaDTO);

        when(categoriaRepository.findByTipo(TipoCategoriaFinanceira.RECEITA)).thenReturn(categorias);
        when(mapper.toDTOList(categorias)).thenReturn(categoriasDTO);

        List<CategoriaFinanceiraDTO> resultado = categoriaUseCase.listarPorTipo(TipoCategoriaFinanceira.RECEITA);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());

        verify(categoriaRepository).findByTipo(TipoCategoriaFinanceira.RECEITA);
        verify(mapper).toDTOList(categorias);
    }

    @Test
    public void testListarAtivasPorTipo() {
        List<CategoriaFinanceira> categorias = Arrays.asList(categoria);
        List<CategoriaFinanceiraDTO> categoriasDTO = Arrays.asList(categoriaDTO);

        when(categoriaRepository.findByTipoAndAtiva(TipoCategoriaFinanceira.RECEITA, true)).thenReturn(categorias);
        when(mapper.toDTOList(categorias)).thenReturn(categoriasDTO);

        List<CategoriaFinanceiraDTO> resultado = categoriaUseCase.listarAtivasPorTipo(TipoCategoriaFinanceira.RECEITA);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());

        verify(categoriaRepository).findByTipoAndAtiva(TipoCategoriaFinanceira.RECEITA, true);
        verify(mapper).toDTOList(categorias);
    }

    @Test
    public void testExisteNome() {
        when(categoriaRepository.existsByNome("Mensalidades")).thenReturn(true);

        boolean resultado = categoriaUseCase.existeNome("Mensalidades");

        assertTrue(resultado);

        verify(categoriaRepository).existsByNome("Mensalidades");
    }

    @Test
    public void testNaoExisteNome() {
        when(categoriaRepository.existsByNome("Inexistente")).thenReturn(false);

        boolean resultado = categoriaUseCase.existeNome("Inexistente");

        assertFalse(resultado);

        verify(categoriaRepository).existsByNome("Inexistente");
    }
}
