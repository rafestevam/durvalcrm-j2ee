package br.org.cecairbar.durvalcrm.examples;

import br.org.cecairbar.durvalcrm.application.dto.VendaDTO;
import br.org.cecairbar.durvalcrm.application.usecase.VendaUseCase;
import br.org.cecairbar.durvalcrm.domain.model.FormaPagamento;
import br.org.cecairbar.durvalcrm.domain.model.OrigemVenda;
import br.org.cecairbar.durvalcrm.domain.model.Venda;
import br.org.cecairbar.durvalcrm.infrastructure.web.resource.VendaResource;
import br.org.cecairbar.durvalcrm.test.base.BaseResourceTest;
import br.org.cecairbar.durvalcrm.test.util.TestDataBuilder;
import jakarta.ws.rs.core.Response;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Example test for VendaResource demonstrating REST endpoint testing.
 *
 * This test demonstrates:
 * - Using BaseResourceTest for common REST testing setup
 * - Testing HTTP status codes
 * - Testing request/response payloads
 * - Mocking use case layer
 * - Testing error scenarios
 */
public class VendaResourceExampleTest extends BaseResourceTest {

    private VendaResource resource;
    private VendaUseCase mockUseCase;

    @Before
    public void setUp() {
        super.setUp();
        mockUseCase = mock(VendaUseCase.class);
        resource = new VendaResource(mockUseCase);
    }

    @Test
    public void deveListarTodasVendas() {
        // Arrange
        Venda venda1 = TestDataBuilder.umaVenda()
            .comDescricao("Venda 1")
            .comValor(new BigDecimal("25.00"))
            .build();

        Venda venda2 = TestDataBuilder.umaVenda()
            .comDescricao("Venda 2")
            .comValor(new BigDecimal("50.00"))
            .build();

        List<Venda> vendas = Arrays.asList(venda1, venda2);
        when(mockUseCase.listarTodas()).thenReturn(vendas);

        // Act
        Response response = resource.listarTodas();

        // Assert
        assertOk(response);
        assertNotNull("Response entity não deve ser nula", response.getEntity());
        verify(mockUseCase).listarTodas();
    }

    @Test
    public void deveCriarNovaVenda() {
        // Arrange
        VendaDTO dto = new VendaDTO();
        dto.setDescricao("Nova venda");
        dto.setValor(new BigDecimal("30.00"));
        dto.setOrigem(OrigemVenda.CANTINA);
        dto.setFormaPagamento(FormaPagamento.DINHEIRO);

        Venda vendaCriada = TestDataBuilder.umaVenda()
            .comDescricao(dto.getDescricao())
            .comValor(dto.getValor())
            .build();

        when(mockUseCase.criar(any(VendaDTO.class))).thenReturn(vendaCriada);

        // Act
        Response response = resource.criar(dto);

        // Assert
        assertCreated(response);
        assertNotNull("Response entity não deve ser nula", response.getEntity());
        verify(mockUseCase).criar(any(VendaDTO.class));
    }

    @Test
    public void deveBuscarVendaPorId() {
        // Arrange
        UUID id = UUID.randomUUID();
        Venda venda = TestDataBuilder.umaVenda().build();

        when(mockUseCase.buscarPorId(id)).thenReturn(Optional.of(venda));

        // Act
        Response response = resource.buscarPorId(id);

        // Assert
        assertOk(response);
        assertNotNull("Response entity não deve ser nula", response.getEntity());
        verify(mockUseCase).buscarPorId(id);
    }

    @Test
    public void deveRetornar404QuandoVendaNaoExiste() {
        // Arrange
        UUID idInexistente = UUID.randomUUID();
        when(mockUseCase.buscarPorId(idInexistente)).thenReturn(Optional.empty());

        // Act
        Response response = resource.buscarPorId(idInexistente);

        // Assert
        assertNotFound(response);
        verify(mockUseCase).buscarPorId(idInexistente);
    }

    @Test
    public void deveAtualizarVenda() {
        // Arrange
        UUID id = UUID.randomUUID();
        VendaDTO dto = new VendaDTO();
        dto.setDescricao("Venda atualizada");
        dto.setValor(new BigDecimal("40.00"));

        Venda vendaAtualizada = TestDataBuilder.umaVenda()
            .comDescricao(dto.getDescricao())
            .comValor(dto.getValor())
            .build();

        when(mockUseCase.atualizar(eq(id), any(VendaDTO.class))).thenReturn(vendaAtualizada);

        // Act
        Response response = resource.atualizar(id, dto);

        // Assert
        assertOk(response);
        assertNotNull("Response entity não deve ser nula", response.getEntity());
        verify(mockUseCase).atualizar(eq(id), any(VendaDTO.class));
    }

    @Test
    public void deveDeletarVenda() {
        // Arrange
        UUID id = UUID.randomUUID();
        doNothing().when(mockUseCase).deletar(id);

        // Act
        Response response = resource.deletar(id);

        // Assert
        assertNoContent(response);
        verify(mockUseCase).deletar(id);
    }

    @Test
    public void deveRetornar400QuandoDadosInvalidos() {
        // Arrange
        VendaDTO dtoInvalido = new VendaDTO();
        // DTO sem campos obrigatórios

        when(mockUseCase.criar(any(VendaDTO.class)))
            .thenThrow(new IllegalArgumentException("Dados inválidos"));

        // Act & Assert
        try {
            resource.criar(dtoInvalido);
            fail("Deveria ter lançado exceção");
        } catch (IllegalArgumentException e) {
            assertEquals("Dados inválidos", e.getMessage());
        }

        verify(mockUseCase).criar(any(VendaDTO.class));
    }

    @Test
    public void deveFiltrarVendasPorOrigem() {
        // Arrange
        OrigemVenda origem = OrigemVenda.CANTINA;
        List<Venda> vendas = Arrays.asList(
            TestDataBuilder.umaVenda().comOrigem(origem).build(),
            TestDataBuilder.umaVenda().comOrigem(origem).build()
        );

        when(mockUseCase.buscarPorOrigem(origem)).thenReturn(vendas);

        // Act
        Response response = resource.buscarPorOrigem(origem);

        // Assert
        assertOk(response);
        assertNotNull("Response entity não deve ser nula", response.getEntity());
        verify(mockUseCase).buscarPorOrigem(origem);
    }
}
