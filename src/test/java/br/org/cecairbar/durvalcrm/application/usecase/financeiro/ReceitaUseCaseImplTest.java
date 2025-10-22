package br.org.cecairbar.durvalcrm.application.usecase.financeiro;

import br.org.cecairbar.durvalcrm.application.dto.ReceitaDTO;
import br.org.cecairbar.durvalcrm.application.financeiro.ReceitaMapper;
import br.org.cecairbar.durvalcrm.domain.model.Receita;
import br.org.cecairbar.durvalcrm.domain.model.CategoriaFinanceira;
import br.org.cecairbar.durvalcrm.domain.model.Associado;
import br.org.cecairbar.durvalcrm.domain.model.TipoReceita;
import br.org.cecairbar.durvalcrm.domain.repository.ReceitaRepository;
import br.org.cecairbar.durvalcrm.domain.repository.CategoriaFinanceiraRepository;
import br.org.cecairbar.durvalcrm.domain.repository.AssociadoRepository;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ReceitaUseCaseImplTest {

    @InjectMocks
    private ReceitaUseCaseImpl receitaUseCase;

    @Mock
    private ReceitaRepository receitaRepository;

    @Mock
    private CategoriaFinanceiraRepository categoriaRepository;

    @Mock
    private AssociadoRepository associadoRepository;

    @Mock
    private ReceitaMapper mapper;

    private UUID receitaId;
    private UUID categoriaId;
    private UUID associadoId;
    private Receita receita;
    private ReceitaDTO receitaDTO;
    private CategoriaFinanceira categoria;
    private Associado associado;

    @Before
    public void setUp() {
        receitaId = UUID.randomUUID();
        categoriaId = UUID.randomUUID();
        associadoId = UUID.randomUUID();

        categoria = new CategoriaFinanceira();
        categoria.setId(categoriaId);
        categoria.setNome("Mensalidades");

        associado = new Associado();
        associado.setId(associadoId);
        associado.setNomeCompleto("João Silva");

        receita = new Receita();
        receita.setId(receitaId);
        receita.setDescricao("Mensalidade Janeiro 2025");
        receita.setValor(new BigDecimal("100.00"));
        receita.setDataReceita(LocalDate.of(2025, 1, 15));
        receita.setTipoReceita(TipoReceita.MENSALIDADE);
        receita.setCategoria(categoria);
        receita.setAssociado(associado);

        receitaDTO = new ReceitaDTO();
        receitaDTO.setId(receitaId);
        receitaDTO.setDescricao("Mensalidade Janeiro 2025");
        receitaDTO.setValor(new BigDecimal("100.00"));
        receitaDTO.setDataReceita(LocalDate.of(2025, 1, 15));
        receitaDTO.setTipoReceita(TipoReceita.MENSALIDADE);
        receitaDTO.setCategoriaId(categoriaId);
        receitaDTO.setAssociadoId(associadoId);
    }

    @Test
    public void testCriarSucesso() {
        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoria));
        when(associadoRepository.findById(associadoId)).thenReturn(Optional.of(associado));
        when(mapper.toDomain(receitaDTO)).thenReturn(receita);
        when(receitaRepository.save(receita)).thenReturn(receita);
        when(mapper.toDTO(receita)).thenReturn(receitaDTO);

        ReceitaDTO resultado = receitaUseCase.criar(receitaDTO);

        assertNotNull(resultado);
        assertEquals("Mensalidade Janeiro 2025", resultado.getDescricao());

        verify(categoriaRepository).findById(categoriaId);
        verify(associadoRepository).findById(associadoId);
        verify(mapper).toDomain(receitaDTO);
        verify(receitaRepository).save(receita);
    }

    @Test(expected = BadRequestException.class)
    public void testCriarCategoriaNaoEncontrada() {
        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.empty());

        receitaUseCase.criar(receitaDTO);
    }

    @Test(expected = BadRequestException.class)
    public void testCriarAssociadoNaoEncontrado() {
        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoria));
        when(associadoRepository.findById(associadoId)).thenReturn(Optional.empty());

        receitaUseCase.criar(receitaDTO);
    }

    @Test(expected = BadRequestException.class)
    public void testCriarOrigemDuplicada() {
        UUID origemId = UUID.randomUUID();
        receitaDTO.setOrigemId(origemId);

        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoria));
        when(associadoRepository.findById(associadoId)).thenReturn(Optional.of(associado));
        when(receitaRepository.existsByOrigemId(origemId)).thenReturn(true);

        receitaUseCase.criar(receitaDTO);
    }

    @Test
    public void testBuscarPorIdExistente() {
        when(receitaRepository.findById(receitaId)).thenReturn(Optional.of(receita));
        when(mapper.toDTO(receita)).thenReturn(receitaDTO);

        ReceitaDTO resultado = receitaUseCase.buscarPorId(receitaId);

        assertNotNull(resultado);
        assertEquals(receitaId, resultado.getId());

        verify(receitaRepository).findById(receitaId);
        verify(mapper).toDTO(receita);
    }

    @Test(expected = NotFoundException.class)
    public void testBuscarPorIdInexistente() {
        when(receitaRepository.findById(receitaId)).thenReturn(Optional.empty());

        receitaUseCase.buscarPorId(receitaId);
    }

    @Test
    public void testAtualizarSucesso() {
        when(receitaRepository.findById(receitaId)).thenReturn(Optional.of(receita));
        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoria));
        when(associadoRepository.findById(associadoId)).thenReturn(Optional.of(associado));
        when(receitaRepository.save(receita)).thenReturn(receita);
        when(mapper.toDTO(receita)).thenReturn(receitaDTO);

        ReceitaDTO resultado = receitaUseCase.atualizar(receitaId, receitaDTO);

        assertNotNull(resultado);

        verify(receitaRepository).findById(receitaId);
        verify(mapper).updateDomainFromDTO(receitaDTO, receita);
        verify(receitaRepository).save(receita);
    }

    @Test
    public void testDeletarSucesso() {
        when(receitaRepository.findById(receitaId)).thenReturn(Optional.of(receita));

        receitaUseCase.deletar(receitaId);

        verify(receitaRepository).findById(receitaId);
        verify(receitaRepository).delete(receita);
    }

    @Test(expected = NotFoundException.class)
    public void testDeletarReceitaInexistente() {
        when(receitaRepository.findById(receitaId)).thenReturn(Optional.empty());

        receitaUseCase.deletar(receitaId);
    }

    @Test
    public void testListarTodas() {
        List<Receita> receitas = Arrays.asList(receita);
        List<ReceitaDTO> receitasDTO = Arrays.asList(receitaDTO);

        when(receitaRepository.findAll()).thenReturn(receitas);
        when(mapper.toDTOList(receitas)).thenReturn(receitasDTO);

        List<ReceitaDTO> resultado = receitaUseCase.listarTodas();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());

        verify(receitaRepository).findAll();
        verify(mapper).toDTOList(receitas);
    }

    @Test
    public void testListarPorPeriodo() {
        LocalDate inicio = LocalDate.of(2025, 1, 1);
        LocalDate fim = LocalDate.of(2025, 1, 31);
        List<Receita> receitas = Arrays.asList(receita);
        List<ReceitaDTO> receitasDTO = Arrays.asList(receitaDTO);

        when(receitaRepository.findByPeriodo(inicio, fim)).thenReturn(receitas);
        when(mapper.toDTOList(receitas)).thenReturn(receitasDTO);

        List<ReceitaDTO> resultado = receitaUseCase.listarPorPeriodo(inicio, fim);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());

        verify(receitaRepository).findByPeriodo(inicio, fim);
    }

    @Test(expected = BadRequestException.class)
    public void testListarPorPeriodoDataInvalida() {
        LocalDate inicio = LocalDate.of(2025, 1, 31);
        LocalDate fim = LocalDate.of(2025, 1, 1);

        receitaUseCase.listarPorPeriodo(inicio, fim);
    }

    @Test
    public void testListarPorTipo() {
        List<Receita> receitas = Arrays.asList(receita);
        List<ReceitaDTO> receitasDTO = Arrays.asList(receitaDTO);

        when(receitaRepository.findByTipoReceita(TipoReceita.MENSALIDADE)).thenReturn(receitas);
        when(mapper.toDTOList(receitas)).thenReturn(receitasDTO);

        List<ReceitaDTO> resultado = receitaUseCase.listarPorTipo(TipoReceita.MENSALIDADE);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());

        verify(receitaRepository).findByTipoReceita(TipoReceita.MENSALIDADE);
    }

    @Test
    public void testListarPorCategoria() {
        List<Receita> receitas = Arrays.asList(receita);
        List<ReceitaDTO> receitasDTO = Arrays.asList(receitaDTO);

        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoria));
        when(receitaRepository.findByCategoria(categoriaId)).thenReturn(receitas);
        when(mapper.toDTOList(receitas)).thenReturn(receitasDTO);

        List<ReceitaDTO> resultado = receitaUseCase.listarPorCategoria(categoriaId);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());

        verify(categoriaRepository).findById(categoriaId);
        verify(receitaRepository).findByCategoria(categoriaId);
    }

    @Test
    public void testListarPorAssociado() {
        List<Receita> receitas = Arrays.asList(receita);
        List<ReceitaDTO> receitasDTO = Arrays.asList(receitaDTO);

        when(associadoRepository.findById(associadoId)).thenReturn(Optional.of(associado));
        when(receitaRepository.findByAssociado(associadoId)).thenReturn(receitas);
        when(mapper.toDTOList(receitas)).thenReturn(receitasDTO);

        List<ReceitaDTO> resultado = receitaUseCase.listarPorAssociado(associadoId);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());

        verify(associadoRepository).findById(associadoId);
        verify(receitaRepository).findByAssociado(associadoId);
    }

    @Test
    public void testSomarPorPeriodo() {
        LocalDate inicio = LocalDate.of(2025, 1, 1);
        LocalDate fim = LocalDate.of(2025, 1, 31);
        BigDecimal total = new BigDecimal("1000.00");

        when(receitaRepository.sumByPeriodo(inicio, fim)).thenReturn(total);

        BigDecimal resultado = receitaUseCase.somarPorPeriodo(inicio, fim);

        assertNotNull(resultado);
        assertEquals(total, resultado);

        verify(receitaRepository).sumByPeriodo(inicio, fim);
    }

    @Test
    public void testSomarPorCategoriaEPeriodo() {
        LocalDate inicio = LocalDate.of(2025, 1, 1);
        LocalDate fim = LocalDate.of(2025, 1, 31);
        BigDecimal total = new BigDecimal("500.00");

        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoria));
        when(receitaRepository.sumByCategoria(categoriaId, inicio, fim)).thenReturn(total);

        BigDecimal resultado = receitaUseCase.somarPorCategoriaEPeriodo(categoriaId, inicio, fim);

        assertNotNull(resultado);
        assertEquals(total, resultado);

        verify(categoriaRepository).findById(categoriaId);
        verify(receitaRepository).sumByCategoria(categoriaId, inicio, fim);
    }

    @Test
    public void testExistePorOrigem() {
        UUID origemId = UUID.randomUUID();

        when(receitaRepository.existsByOrigemId(origemId)).thenReturn(true);

        boolean resultado = receitaUseCase.existePorOrigem(origemId);

        assertTrue(resultado);

        verify(receitaRepository).existsByOrigemId(origemId);
    }
}
