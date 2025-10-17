package br.org.cecairbar.durvalcrm.infrastructure.persistence.repository;

import br.org.cecairbar.durvalcrm.application.financeiro.ReceitaEntityMapper;
import br.org.cecairbar.durvalcrm.domain.model.Receita;
import br.org.cecairbar.durvalcrm.domain.model.TipoReceita;
import br.org.cecairbar.durvalcrm.domain.repository.ReceitaRepository;
import br.org.cecairbar.durvalcrm.infrastructure.persistence.entity.ReceitaEntity;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class ReceitaRepositoryImpl implements ReceitaRepository {

    @Inject
    EntityManager entityManager;

    @Inject
    ReceitaEntityMapper mapper;

    @Override
    @Transactional
    public Receita save(Receita receita) {
        if (receita.getId() == null) {
            ReceitaEntity entity = mapper.toEntity(receita);
            entityManager.persist(entity);
            entityManager.flush();
            return mapper.toDomain(entity);
        } else {
            ReceitaEntity entity = entityManager.find(ReceitaEntity.class, receita.getId());
            if (entity != null) {
                mapper.updateEntityFromDomain(receita, entity);
                entity = entityManager.merge(entity);
                return mapper.toDomain(entity);
            } else {
                throw new IllegalArgumentException("Receita com ID " + receita.getId() + " não encontrada para atualização.");
            }
        }
    }

    @Override
    public Optional<Receita> findById(UUID id) {
        try {
            ReceitaEntity entity = entityManager.find(ReceitaEntity.class, id);
            return entity != null ? Optional.of(mapper.toDomain(entity)) : Optional.empty();
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Receita> findAll() {
        TypedQuery<ReceitaEntity> query = entityManager.createQuery(
            "SELECT r FROM ReceitaEntity r ORDER BY r.dataReceita DESC",
            ReceitaEntity.class
        );
        List<ReceitaEntity> entities = query.getResultList();
        return mapper.toDomainList(entities);
    }

    @Override
    public List<Receita> findByTipoReceita(TipoReceita tipo) {
        TypedQuery<ReceitaEntity> query = entityManager.createQuery(
            "SELECT r FROM ReceitaEntity r WHERE r.tipoReceita = :tipo ORDER BY r.dataReceita DESC",
            ReceitaEntity.class
        );
        query.setParameter("tipo", tipo);
        List<ReceitaEntity> entities = query.getResultList();
        return mapper.toDomainList(entities);
    }

    @Override
    public List<Receita> findByCategoria(UUID categoriaId) {
        TypedQuery<ReceitaEntity> query = entityManager.createQuery(
            "SELECT r FROM ReceitaEntity r WHERE r.categoria.id = :categoriaId ORDER BY r.dataReceita DESC",
            ReceitaEntity.class
        );
        query.setParameter("categoriaId", categoriaId);
        List<ReceitaEntity> entities = query.getResultList();
        return mapper.toDomainList(entities);
    }

    @Override
    public List<Receita> findByAssociado(UUID associadoId) {
        TypedQuery<ReceitaEntity> query = entityManager.createQuery(
            "SELECT r FROM ReceitaEntity r WHERE r.associado.id = :associadoId ORDER BY r.dataReceita DESC",
            ReceitaEntity.class
        );
        query.setParameter("associadoId", associadoId);
        List<ReceitaEntity> entities = query.getResultList();
        return mapper.toDomainList(entities);
    }

    @Override
    public List<Receita> findByPeriodo(LocalDate inicio, LocalDate fim) {
        TypedQuery<ReceitaEntity> query = entityManager.createQuery(
            "SELECT r FROM ReceitaEntity r WHERE r.dataReceita >= :inicio AND r.dataReceita <= :fim ORDER BY r.dataReceita DESC",
            ReceitaEntity.class
        );
        query.setParameter("inicio", inicio);
        query.setParameter("fim", fim);
        List<ReceitaEntity> entities = query.getResultList();
        return mapper.toDomainList(entities);
    }

    @Override
    public List<Receita> findByDataRecebimento(LocalDate inicio, LocalDate fim) {
        TypedQuery<ReceitaEntity> query = entityManager.createQuery(
            "SELECT r FROM ReceitaEntity r WHERE r.dataRecebimento >= :inicio AND r.dataRecebimento <= :fim ORDER BY r.dataRecebimento DESC",
            ReceitaEntity.class
        );
        query.setParameter("inicio", inicio);
        query.setParameter("fim", fim);
        List<ReceitaEntity> entities = query.getResultList();
        return mapper.toDomainList(entities);
    }

    @Override
    public List<Receita> findByTipoAndPeriodo(TipoReceita tipo, LocalDate inicio, LocalDate fim) {
        TypedQuery<ReceitaEntity> query = entityManager.createQuery(
            "SELECT r FROM ReceitaEntity r WHERE r.tipoReceita = :tipo AND r.dataReceita >= :inicio AND r.dataReceita <= :fim ORDER BY r.dataReceita DESC",
            ReceitaEntity.class
        );
        query.setParameter("tipo", tipo);
        query.setParameter("inicio", inicio);
        query.setParameter("fim", fim);
        List<ReceitaEntity> entities = query.getResultList();
        return mapper.toDomainList(entities);
    }

    @Override
    public List<Receita> findByCategoriaAndPeriodo(UUID categoriaId, LocalDate inicio, LocalDate fim) {
        TypedQuery<ReceitaEntity> query = entityManager.createQuery(
            "SELECT r FROM ReceitaEntity r WHERE r.categoria.id = :categoriaId AND r.dataReceita >= :inicio AND r.dataReceita <= :fim ORDER BY r.dataReceita DESC",
            ReceitaEntity.class
        );
        query.setParameter("categoriaId", categoriaId);
        query.setParameter("inicio", inicio);
        query.setParameter("fim", fim);
        List<ReceitaEntity> entities = query.getResultList();
        return mapper.toDomainList(entities);
    }

    @Override
    public Optional<Receita> findByOrigemId(UUID origemId) {
        TypedQuery<ReceitaEntity> query = entityManager.createQuery(
            "SELECT r FROM ReceitaEntity r WHERE r.origemId = :origemId",
            ReceitaEntity.class
        );
        query.setParameter("origemId", origemId);
        try {
            ReceitaEntity entity = query.getSingleResult();
            return Optional.of(mapper.toDomain(entity));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public BigDecimal sumByPeriodo(LocalDate inicio, LocalDate fim) {
        TypedQuery<BigDecimal> query = entityManager.createQuery(
            "SELECT SUM(r.valor) FROM ReceitaEntity r WHERE r.dataReceita >= :inicio AND r.dataReceita <= :fim",
            BigDecimal.class
        );
        query.setParameter("inicio", inicio);
        query.setParameter("fim", fim);
        BigDecimal result = query.getSingleResult();
        return result != null ? result : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal sumByCategoria(UUID categoriaId, LocalDate inicio, LocalDate fim) {
        TypedQuery<BigDecimal> query = entityManager.createQuery(
            "SELECT SUM(r.valor) FROM ReceitaEntity r WHERE r.categoria.id = :categoriaId AND r.dataReceita >= :inicio AND r.dataReceita <= :fim",
            BigDecimal.class
        );
        query.setParameter("categoriaId", categoriaId);
        query.setParameter("inicio", inicio);
        query.setParameter("fim", fim);
        BigDecimal result = query.getSingleResult();
        return result != null ? result : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal sumByTipo(TipoReceita tipo, LocalDate inicio, LocalDate fim) {
        TypedQuery<BigDecimal> query = entityManager.createQuery(
            "SELECT SUM(r.valor) FROM ReceitaEntity r WHERE r.tipoReceita = :tipo AND r.dataReceita >= :inicio AND r.dataReceita <= :fim",
            BigDecimal.class
        );
        query.setParameter("tipo", tipo);
        query.setParameter("inicio", inicio);
        query.setParameter("fim", fim);
        BigDecimal result = query.getSingleResult();
        return result != null ? result : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal sumByAssociado(UUID associadoId, LocalDate inicio, LocalDate fim) {
        TypedQuery<BigDecimal> query = entityManager.createQuery(
            "SELECT SUM(r.valor) FROM ReceitaEntity r WHERE r.associado.id = :associadoId AND r.dataReceita >= :inicio AND r.dataReceita <= :fim",
            BigDecimal.class
        );
        query.setParameter("associadoId", associadoId);
        query.setParameter("inicio", inicio);
        query.setParameter("fim", fim);
        BigDecimal result = query.getSingleResult();
        return result != null ? result : BigDecimal.ZERO;
    }

    @Override
    public long countByTipo(TipoReceita tipo) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(r) FROM ReceitaEntity r WHERE r.tipoReceita = :tipo",
            Long.class
        );
        query.setParameter("tipo", tipo);
        return query.getSingleResult();
    }

    @Override
    public long countByPeriodo(LocalDate inicio, LocalDate fim) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(r) FROM ReceitaEntity r WHERE r.dataReceita >= :inicio AND r.dataReceita <= :fim",
            Long.class
        );
        query.setParameter("inicio", inicio);
        query.setParameter("fim", fim);
        return query.getSingleResult();
    }

    @Override
    @Transactional
    public void delete(Receita receita) {
        ReceitaEntity entity = entityManager.find(ReceitaEntity.class, receita.getId());
        if (entity != null) {
            entityManager.remove(entity);
        }
    }

    @Override
    public boolean existsById(UUID id) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(r) FROM ReceitaEntity r WHERE r.id = :id",
            Long.class
        );
        query.setParameter("id", id);
        return query.getSingleResult() > 0;
    }

    @Override
    public boolean existsByOrigemId(UUID origemId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(r) FROM ReceitaEntity r WHERE r.origemId = :origemId",
            Long.class
        );
        query.setParameter("origemId", origemId);
        return query.getSingleResult() > 0;
    }
}
