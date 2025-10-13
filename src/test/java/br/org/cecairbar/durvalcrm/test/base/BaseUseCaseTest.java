package br.org.cecairbar.durvalcrm.test.base;

import org.junit.After;
import org.junit.Before;

/**
 * Base class for use case unit tests.
 * Provides common setup and teardown for testing application use cases.
 *
 * Usage:
 * <pre>
 * public class MyUseCaseTest extends BaseUseCaseTest {
 *
 *     private MyUseCase useCase;
 *     private MyRepository mockRepository;
 *
 *     {@literal @}Before
 *     public void setUp() {
 *         super.setUp();
 *         mockRepository = MockFactory.mockMyRepository();
 *         useCase = new MyUseCaseImpl(mockRepository);
 *     }
 * }
 * </pre>
 */
public abstract class BaseUseCaseTest {

    @Before
    public void setUp() {
        // Common setup for use case tests
    }

    @After
    public void tearDown() {
        // Common cleanup
    }

    /**
     * Helper method to verify business rule violations
     */
    protected void assertBusinessRuleViolation(Runnable action, String expectedMessage) {
        try {
            action.run();
            throw new AssertionError("Expected IllegalArgumentException but none was thrown");
        } catch (IllegalArgumentException e) {
            if (!e.getMessage().contains(expectedMessage)) {
                throw new AssertionError(
                    String.format("Expected message containing '%s' but got: %s",
                        expectedMessage, e.getMessage())
                );
            }
        }
    }
}
