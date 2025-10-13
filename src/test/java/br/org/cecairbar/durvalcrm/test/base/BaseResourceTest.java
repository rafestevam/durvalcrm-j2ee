package br.org.cecairbar.durvalcrm.test.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.ws.rs.core.Response;
import org.junit.After;
import org.junit.Before;

import static org.junit.Assert.assertEquals;

/**
 * Base class for REST resource (endpoint) tests.
 * Provides common utilities for testing JAX-RS resources.
 *
 * Usage:
 * <pre>
 * public class MyResourceTest extends BaseResourceTest {
 *
 *     private MyResource resource;
 *     private MyUseCase mockUseCase;
 *
 *     {@literal @}Before
 *     public void setUp() {
 *         super.setUp();
 *         mockUseCase = mock(MyUseCase.class);
 *         resource = new MyResource(mockUseCase);
 *     }
 * }
 * </pre>
 */
public abstract class BaseResourceTest {

    protected ObjectMapper objectMapper;

    @Before
    public void setUp() {
        // Setup ObjectMapper for JSON serialization/deserialization in tests
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @After
    public void tearDown() {
        // Common cleanup
    }

    /**
     * Asserts that a Response has the expected HTTP status
     */
    protected void assertResponseStatus(Response response, Response.Status expectedStatus) {
        assertEquals(
            "Expected status " + expectedStatus + " but got " + response.getStatusInfo(),
            expectedStatus.getStatusCode(),
            response.getStatus()
        );
    }

    /**
     * Asserts that a Response is 200 OK
     */
    protected void assertOk(Response response) {
        assertResponseStatus(response, Response.Status.OK);
    }

    /**
     * Asserts that a Response is 201 Created
     */
    protected void assertCreated(Response response) {
        assertResponseStatus(response, Response.Status.CREATED);
    }

    /**
     * Asserts that a Response is 204 No Content
     */
    protected void assertNoContent(Response response) {
        assertResponseStatus(response, Response.Status.NO_CONTENT);
    }

    /**
     * Asserts that a Response is 400 Bad Request
     */
    protected void assertBadRequest(Response response) {
        assertResponseStatus(response, Response.Status.BAD_REQUEST);
    }

    /**
     * Asserts that a Response is 404 Not Found
     */
    protected void assertNotFound(Response response) {
        assertResponseStatus(response, Response.Status.NOT_FOUND);
    }

    /**
     * Asserts that a Response is 500 Internal Server Error
     */
    protected void assertInternalServerError(Response response) {
        assertResponseStatus(response, Response.Status.INTERNAL_SERVER_ERROR);
    }

    /**
     * Extracts entity from Response and converts to specified type
     */
    protected <T> T extractEntity(Response response, Class<T> entityClass) {
        Object entity = response.getEntity();
        return objectMapper.convertValue(entity, entityClass);
    }
}
