package br.org.cecairbar.durvalcrm.infrastructure.persistence.repository;

import br.org.cecairbar.durvalcrm.application.doacao.DoacaoEntityMapper;
import br.org.cecairbar.durvalcrm.domain.model.Doacao;
import br.org.cecairbar.durvalcrm.domain.model.StatusDoacao;
import br.org.cecairbar.durvalcrm.domain.repository.DoacaoRepository;
import br.org.cecairbar.durvalcrm.infrastructure.persistence.entity.DoacaoEntity;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class DoacaoRepositoryImpl implements DoacaoRepository {
    
    @Inject
    EntityManager entityManager;
    
    @Inject
    DoacaoEntityMapper mapper;
    
    @Override
    @Transactional
    public Doacao save(Doacao doacao) {
        if (doacao.getId() == null) {
            DoacaoEntity entity = mapper.toEntity(doacao);
            entityManager.persist(entity);
            entityManager.flush();
            return mapper.toDomain(entity);
        } else {
            DoacaoEntity entity = entityManager.find(DoacaoEntity.class, doacao.getId());
            if (entity != null) {
                mapper.updateEntityFromDomain(doacao, entity);
                entity = entityManager.merge(entity);
                return mapper.toDomain(entity);
            } else {
                throw new IllegalArgumentException("Doação com ID " + doacao.getId() + " não encontrada para atualização.");
            }
        }
    }
    
    @Override
    public Optional<Doacao> findById(UUID id) {
        try {
            DoacaoEntity entity = entityManager.find(DoacaoEntity.class, id);
            return entity != null ? Optional.of(mapper.toDomain(entity)) : Optional.empty();
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    @Override
    public List<Doacao> findAll() {
        TypedQuery<DoacaoEntity> query = entityManager.createQuery(
            "SELECT d FROM DoacaoEntity d", 
            DoacaoEntity.class
        );
        List<DoacaoEntity> entities = query.getResultList();
        return mapper.toDomainList(entities);
    }
    
    @Override
    public List<Doacao> findByAssociado(UUID associadoId) {
        TypedQuery<DoacaoEntity> query = entityManager.createQuery(
            "SELECT d FROM DoacaoEntity d WHERE d.associado.id = :associadoId", 
            DoacaoEntity.class
        );
        query.setParameter("associadoId", associadoId);
        List<DoacaoEntity> entities = query.getResultList();
        return mapper.toDomainList(entities);
    }
    
    @Override
    public List<Doacao> findByStatus(StatusDoacao status) {
        TypedQuery<DoacaoEntity> query = entityManager.createQuery(
            "SELECT d FROM DoacaoEntity d WHERE d.status = :status", 
            DoacaoEntity.class
        );
        query.setParameter("status", status);
        List<DoacaoEntity> entities = query.getResultList();
        return mapper.toDomainList(entities);
    }
    
    @Override
    public List<Doacao> findByPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        TypedQuery<DoacaoEntity> query = entityManager.createQuery(
            "SELECT d FROM DoacaoEntity d WHERE d.dataDoacao >= :inicio AND d.dataDoacao <= :fim", 
            DoacaoEntity.class
        );
        query.setParameter("inicio", inicio);
        query.setParameter("fim", fim);
        List<DoacaoEntity> entities = query.getResultList();
        return mapper.toDomainList(entities);
    }
    
    @Override
    public List<Doacao> findByAssociadoAndPeriodo(UUID associadoId, LocalDateTime inicio, LocalDateTime fim) {
        TypedQuery<DoacaoEntity> query = entityManager.createQuery(
            "SELECT d FROM DoacaoEntity d WHERE d.associado.id = :associadoId AND d.dataDoacao >= :inicio AND d.dataDoacao <= :fim", 
            DoacaoEntity.class
        );
        query.setParameter("associadoId", associadoId);
        query.setParameter("inicio", inicio);
        query.setParameter("fim", fim);
        List<DoacaoEntity> entities = query.getResultList();
        return mapper.toDomainList(entities);
    }
    
    @Override
    public BigDecimal sumByPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        TypedQuery<BigDecimal> query = entityManager.createQuery(
            "SELECT SUM(d.valor) FROM DoacaoEntity d WHERE d.dataDoacao >= :inicio " +
            "AND d.dataDoacao <= :fim AND d.status = :status",
            BigDecimal.class
        );
        query.setParameter("inicio", inicio);
        query.setParameter("fim", fim);
        query.setParameter("status", StatusDoacao.CONFIRMADA);
        
        BigDecimal result = query.getSingleResult();
        return result != null ? result : BigDecimal.ZERO;
    }
    
    @Override
    public BigDecimal sumByAssociadoAndPeriodo(UUID associadoId, LocalDateTime inicio, LocalDateTime fim) {
        TypedQuery<BigDecimal> query = entityManager.createQuery(
            "SELECT SUM(d.valor) FROM DoacaoEntity d WHERE d.associado.id = :associadoId " +
            "AND d.dataDoacao >= :inicio AND d.dataDoacao <= :fim AND d.status = :status",
            BigDecimal.class
        );
        query.setParameter("associadoId", associadoId);
        query.setParameter("inicio", inicio);
        query.setParameter("fim", fim);
        query.setParameter("status", StatusDoacao.CONFIRMADA);
        
        BigDecimal result = query.getSingleResult();
        return result != null ? result : BigDecimal.ZERO;
    }
    
    @Override
    public long countByStatus(StatusDoacao status) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(d) FROM DoacaoEntity d WHERE d.status = :status", 
            Long.class
        );
        query.setParameter("status", status);
        return query.getSingleResult();
    }
    
    @Override
    @Transactional
    public void delete(Doacao doacao) {
        DoacaoEntity entity = entityManager.find(DoacaoEntity.class, doacao.getId());
        if (entity != null) {
            entityManager.remove(entity);
        }
    }
    
    @Override
    public boolean existsById(UUID id) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(d) FROM DoacaoEntity d WHERE d.id = :id", 
            Long.class
        );
        query.setParameter("id", id);
        return query.getSingleResult() > 0;
    }
    
    @Override
    public BigDecimal obterTotalDoacoesPorPeriodo(Instant inicio, Instant fim) {
        // Converter Instant para LocalDateTime
        LocalDateTime inicioLDT = LocalDateTime.ofInstant(inicio, java.time.ZoneId.systemDefault());
        LocalDateTime fimLDT = LocalDateTime.ofInstant(fim, java.time.ZoneId.systemDefault());
        
        TypedQuery<DoacaoEntity> query = entityManager.createQuery(
            "SELECT d FROM DoacaoEntity d WHERE d.dataDoacao >= :inicio AND d.dataDoacao <= :fim AND d.status = :status", 
            DoacaoEntity.class
        );
        query.setParameter("inicio", inicioLDT);
        query.setParameter("fim", fimLDT);
        query.setParameter("status", StatusDoacao.CONFIRMADA);
        
        List<DoacaoEntity> doacoes = query.getResultList();
        
        return doacoes.stream()
            .map(doacao -> doacao.valor)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}