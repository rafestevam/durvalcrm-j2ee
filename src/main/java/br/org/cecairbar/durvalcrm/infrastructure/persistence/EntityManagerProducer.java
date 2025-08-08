package br.org.cecairbar.durvalcrm.infrastructure.persistence;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Producer para EntityManager usando CDI
 * Necessário para integração com Jakarta EE e injeção de dependências
 */
@ApplicationScoped
public class EntityManagerProducer {

    @PersistenceContext(unitName = "durvalcrm-pu")
    private EntityManager entityManager;

    @Produces
    public EntityManager getEntityManager() {
        return entityManager;
    }
}