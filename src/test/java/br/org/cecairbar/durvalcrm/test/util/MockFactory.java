package br.org.cecairbar.durvalcrm.test.util;

import br.org.cecairbar.durvalcrm.domain.repository.*;
import org.mockito.Mockito;

/**
 * Factory for creating mock objects commonly used in tests.
 * Centralizes mock creation to ensure consistency across tests.
 *
 * Usage:
 * <pre>
 * AssociadoRepository mockRepository = MockFactory.mockAssociadoRepository();
 * </pre>
 */
public class MockFactory {

    /**
     * Creates a mock AssociadoRepository
     */
    public static AssociadoRepository mockAssociadoRepository() {
        return Mockito.mock(AssociadoRepository.class);
    }

    /**
     * Creates a mock MensalidadeRepository
     */
    public static MensalidadeRepository mockMensalidadeRepository() {
        return Mockito.mock(MensalidadeRepository.class);
    }

    /**
     * Creates a mock DoacaoRepository
     */
    public static DoacaoRepository mockDoacaoRepository() {
        return Mockito.mock(DoacaoRepository.class);
    }

    /**
     * Creates a mock VendaRepository
     */
    public static VendaRepository mockVendaRepository() {
        return Mockito.mock(VendaRepository.class);
    }

    /**
     * Resets all mocks (useful for cleanup between tests)
     */
    public static void resetMocks(Object... mocks) {
        for (Object mock : mocks) {
            Mockito.reset(mock);
        }
    }
}
