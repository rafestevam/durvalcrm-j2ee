package br.org.cecairbar.durvalcrm.infrastructure.persistence.repository;

import br.org.cecairbar.durvalcrm.infrastructure.persistence.entity.VendaEntity;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class VendaPanacheRepository {
    
    @Inject
    EntityManager entityManager;
    
    @Transactional
    public void persist(VendaEntity entity) {
        if (entity.id == null) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }
    }
    
    public VendaEntity findById(UUID id) {
        return entityManager.find(VendaEntity.class, id);
    }
    
    public List<VendaEntity> listAll() {
        TypedQuery<VendaEntity> query = entityManager.createQuery(
            "SELECT v FROM VendaEntity v", 
            VendaEntity.class
        );
        return query.getResultList();
    }
    
    public TypedQuery<VendaEntity> find(String query, Object... params) {
        TypedQuery<VendaEntity> typedQuery = entityManager.createQuery(
            "SELECT v FROM VendaEntity v WHERE " + query, 
            VendaEntity.class
        );
        
        for (int i = 0; i < params.length; i++) {
            typedQuery.setParameter(i + 1, params[i]);
        }
        
        return typedQuery;
    }
    
    public TypedQuery<VendaEntity> find(String field, Object value) {
        TypedQuery<VendaEntity> query = entityManager.createQuery(
            "SELECT v FROM VendaEntity v WHERE v." + field + " = :value", 
            VendaEntity.class
        );
        query.setParameter("value", value);
        return query;
    }
    
    public long count(String query, Object... params) {
        TypedQuery<Long> typedQuery = entityManager.createQuery(
            "SELECT COUNT(v) FROM VendaEntity v WHERE " + query, 
            Long.class
        );
        
        for (int i = 0; i < params.length; i++) {
            typedQuery.setParameter(i + 1, params[i]);
        }
        
        return typedQuery.getSingleResult();
    }
    
    public long count(String field, Object value) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(v) FROM VendaEntity v WHERE v." + field + " = :value", 
            Long.class
        );
        query.setParameter("value", value);
        return query.getSingleResult();
    }
    
    @Transactional
    public boolean delete(String field, Object value) {
        int deleted = entityManager.createQuery(
            "DELETE FROM VendaEntity v WHERE v." + field + " = :value"
        )
        .setParameter("value", value)
        .executeUpdate();
        
        return deleted > 0;
    }
}