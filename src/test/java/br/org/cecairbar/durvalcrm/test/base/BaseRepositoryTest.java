package br.org.cecairbar.durvalcrm.test.base;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * Base class for repository integration tests.
 * Provides EntityManager setup with H2 in-memory database for testing.
 *
 * Usage:
 * <pre>
 * public class MyRepositoryTest extends BaseRepositoryTest {
 *
 *     private MyRepositoryImpl repository;
 *
 *     {@literal @}Before
 *     public void setUp() {
 *         super.setUp();
 *         repository = new MyRepositoryImpl(entityManager);
 *     }
 * }
 * </pre>
 */
public abstract class BaseRepositoryTest {

    protected static EntityManagerFactory entityManagerFactory;
    protected EntityManager entityManager;

    @BeforeClass
    public static void setUpClass() {
        // Create EntityManagerFactory with test persistence unit
        entityManagerFactory = Persistence.createEntityManagerFactory("durvalcrm-test-pu");
    }

    @AfterClass
    public static void tearDownClass() {
        if (entityManagerFactory != null) {
            entityManagerFactory.close();
        }
    }

    @Before
    public void setUp() {
        // Create new EntityManager for each test
        entityManager = entityManagerFactory.createEntityManager();
    }

    @After
    public void tearDown() {
        if (entityManager != null) {
            // Rollback any pending transaction
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            entityManager.close();
        }
    }

    /**
     * Begins a transaction for the current test
     */
    protected void beginTransaction() {
        entityManager.getTransaction().begin();
    }

    /**
     * Commits the current transaction
     */
    protected void commitTransaction() {
        entityManager.getTransaction().commit();
    }

    /**
     * Rolls back the current transaction
     */
    protected void rollbackTransaction() {
        if (entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().rollback();
        }
    }

    /**
     * Flushes and clears the EntityManager to force database sync
     */
    protected void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
