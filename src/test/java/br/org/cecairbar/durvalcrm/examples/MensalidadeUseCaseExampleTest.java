package br.org.cecairbar.durvalcrm.examples;

import br.org.cecairbar.durvalcrm.application.usecase.impl.MensalidadeUseCaseImpl;
import br.org.cecairbar.durvalcrm.domain.model.Mensalidade;
import br.org.cecairbar.durvalcrm.domain.model.StatusMensalidade;
import br.org.cecairbar.durvalcrm.domain.repository.MensalidadeRepository;
import br.org.cecairbar.durvalcrm.test.base.BaseUseCaseTest;
import br.org.cecairbar.durvalcrm.test.util.MockFactory;
import br.org.cecairbar.durvalcrm.test.util.TestDataBuilder;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static br.org.cecairbar.durvalcrm.test.util.TestAssertions.assertMensalidade;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Example test for MensalidadeUseCase demonstrating the testing framework usage.
 *
 * This test demonstrates:
 * - Using BaseUseCaseTest for common setup
 * - Using TestDataBuilder for creating test data
 * - Using MockFactory for creating mocks
 * - Using TestAssertions for fluent assertions
 * - Testing business logic in use cases
 */
public class MensalidadeUseCaseExampleTest extends BaseUseCaseTest {

    private MensalidadeUseCaseImpl useCase;
    private MensalidadeRepository mockRepository;

    @Before
    public void setUp() {
        super.setUp();
        mockRepository = MockFactory.mockMensalidadeRepository();
        useCase = new MensalidadeUseCaseImpl(mockRepository);
    }

    @Test
    public void deveCriarNovaMensalidade() {
        // Arrange
        UUID associadoId = UUID.randomUUID();
        int mes = 5;
        int ano = 2024;
        BigDecimal valor = new BigDecimal("10.90");

        Mensalidade mensalidadeEsperada = TestDataBuilder.umaMensalidade()
            .paraAssociado(associadoId)
            .referencia(mes, ano)
            .comValor(valor)
            .build();

        when(mockRepository.save(any(Mensalidade.class))).thenReturn(mensalidadeEsperada);

        // Act
        Mensalidade resultado = useCase.criar(associadoId, mes, ano, valor);

        // Assert
        assertNotNull("Mensalidade não deve ser nula", resultado);
        assertMensalidade(resultado)
            .pertenceAoAssociado(associadoId)
            .hasReferencia(mes, ano)
            .hasValor(valor)
            .isPendente();

        verify(mockRepository).save(any(Mensalidade.class));
    }

    @Test
    public void deveBuscarMensalidadePorId() {
        // Arrange
        UUID id = UUID.randomUUID();
        Mensalidade mensalidadeEsperada = TestDataBuilder.umaMensalidade().build();

        when(mockRepository.findById(id)).thenReturn(Optional.of(mensalidadeEsperada));

        // Act
        Optional<Mensalidade> resultado = useCase.buscarPorId(id);

        // Assert
        assertTrue("Mensalidade deve estar presente", resultado.isPresent());
        assertEquals(mensalidadeEsperada, resultado.get());
        verify(mockRepository).findById(id);
    }

    @Test
    public void deveRetornarVazioQuandoMensalidadeNaoExiste() {
        // Arrange
        UUID idInexistente = UUID.randomUUID();
        when(mockRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // Act
        Optional<Mensalidade> resultado = useCase.buscarPorId(idInexistente);

        // Assert
        assertFalse("Mensalidade não deve estar presente", resultado.isPresent());
        verify(mockRepository).findById(idInexistente);
    }

    @Test
    public void deveMarcarMensalidadeComoPaga() {
        // Arrange
        UUID id = UUID.randomUUID();
        Mensalidade mensalidade = TestDataBuilder.umaMensalidade().build();

        when(mockRepository.findById(id)).thenReturn(Optional.of(mensalidade));
        when(mockRepository.save(any(Mensalidade.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Mensalidade resultado = useCase.marcarComoPaga(id);

        // Assert
        assertNotNull("Mensalidade não deve ser nula", resultado);
        assertEquals(StatusMensalidade.PAGA, resultado.getStatus());
        assertNotNull("Data de pagamento deve estar preenchida", resultado.getDataPagamento());
        verify(mockRepository).findById(id);
        verify(mockRepository).save(mensalidade);
    }

    @Test
    public void deveLancarExcecaoAoMarcarMensalidadeInexistenteComoPaga() {
        // Arrange
        UUID idInexistente = UUID.randomUUID();
        when(mockRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        assertBusinessRuleViolation(
            () -> useCase.marcarComoPaga(idInexistente),
            "Mensalidade não encontrada"
        );

        verify(mockRepository).findById(idInexistente);
        verify(mockRepository, never()).save(any(Mensalidade.class));
    }

    @Test
    public void deveAtualizarStatusDeMensalidadesVencidas() {
        // Arrange
        Mensalidade mensalidadePendente = TestDataBuilder.umaMensalidade().build();

        when(mockRepository.findAll()).thenReturn(java.util.Arrays.asList(mensalidadePendente));
        when(mockRepository.save(any(Mensalidade.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        useCase.atualizarStatusVencidas();

        // Assert - verify that status update was called for overdue payments
        verify(mockRepository).findAll();
        verify(mockRepository, atLeastOnce()).save(any(Mensalidade.class));
    }

    @Test
    public void naoDevePermitirValorNegativo() {
        // Arrange
        UUID associadoId = UUID.randomUUID();
        BigDecimal valorNegativo = new BigDecimal("-10.00");

        // Act & Assert
        assertBusinessRuleViolation(
            () -> useCase.criar(associadoId, 5, 2024, valorNegativo),
            "Valor deve ser positivo"
        );

        verify(mockRepository, never()).save(any(Mensalidade.class));
    }
}
