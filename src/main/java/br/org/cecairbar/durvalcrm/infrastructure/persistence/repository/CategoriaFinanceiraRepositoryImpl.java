package br.org.cecairbar.durvalcrm.infrastructure.persistence.repository;

import br.org.cecairbar.durvalcrm.application.financeiro.CategoriaFinanceiraEntityMapper;
import br.org.cecairbar.durvalcrm.domain.model.CategoriaFinanceira;
import br.org.cecairbar.durvalcrm.domain.model.TipoCategoriaFinanceira;
import br.org.cecairbar.durvalcrm.domain.repository.CategoriaFinanceiraRepository;
import br.org.cecairbar.durvalcrm.infrastructure.persistence.entity.CategoriaFinanceiraEntity;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class CategoriaFinanceiraRepositoryImpl implements CategoriaFinanceiraRepository {

    @Inject
    EntityManager entityManager;

    @Inject
    CategoriaFinanceiraEntityMapper mapper;

    @Override
    @Transactional
    public CategoriaFinanceira save(CategoriaFinanceira categoria) {
        if (categoria.getId() == null) {
            CategoriaFinanceiraEntity entity = mapper.toEntity(categoria);
            entityManager.persist(entity);
            entityManager.flush();
            return mapper.toDomain(entity);
        } else {
            CategoriaFinanceiraEntity entity = entityManager.find(CategoriaFinanceiraEntity.class, categoria.getId());
            if (entity != null) {
                mapper.updateEntityFromDomain(categoria, entity);
                entity = entityManager.merge(entity);
                return mapper.toDomain(entity);
            } else {
                throw new IllegalArgumentException("Categoria com ID " + categoria.getId() + " não encontrada para atualização.");
            }
        }
    }

    @Override
    public Optional<CategoriaFinanceira> findById(UUID id) {
        try {
            CategoriaFinanceiraEntity entity = entityManager.find(CategoriaFinanceiraEntity.class, id);
            return entity != null ? Optional.of(mapper.toDomain(entity)) : Optional.empty();
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<CategoriaFinanceira> findAll() {
        TypedQuery<CategoriaFinanceiraEntity> query = entityManager.createQuery(
            "SELECT c FROM CategoriaFinanceiraEntity c ORDER BY c.nome",
            CategoriaFinanceiraEntity.class
        );
        List<CategoriaFinanceiraEntity> entities = query.getResultList();
        return mapper.toDomainList(entities);
    }

    @Override
    public List<CategoriaFinanceira> findByTipo(TipoCategoriaFinanceira tipo) {
        TypedQuery<CategoriaFinanceiraEntity> query = entityManager.createQuery(
            "SELECT c FROM CategoriaFinanceiraEntity c WHERE c.tipo = :tipo OR c.tipo = :ambos ORDER BY c.nome",
            CategoriaFinanceiraEntity.class
        );
        query.setParameter("tipo", tipo);
        query.setParameter("ambos", TipoCategoriaFinanceira.AMBOS);
        List<CategoriaFinanceiraEntity> entities = query.getResultList();
        return mapper.toDomainList(entities);
    }

    @Override
    public List<CategoriaFinanceira> findByAtiva(Boolean ativa) {
        TypedQuery<CategoriaFinanceiraEntity> query = entityManager.createQuery(
            "SELECT c FROM CategoriaFinanceiraEntity c WHERE c.ativa = :ativa ORDER BY c.nome",
            CategoriaFinanceiraEntity.class
        );
        query.setParameter("ativa", ativa);
        List<CategoriaFinanceiraEntity> entities = query.getResultList();
        return mapper.toDomainList(entities);
    }

    @Override
    public List<CategoriaFinanceira> findByTipoAndAtiva(TipoCategoriaFinanceira tipo, Boolean ativa) {
        TypedQuery<CategoriaFinanceiraEntity> query = entityManager.createQuery(
            "SELECT c FROM CategoriaFinanceiraEntity c WHERE (c.tipo = :tipo OR c.tipo = :ambos) AND c.ativa = :ativa ORDER BY c.nome",
            CategoriaFinanceiraEntity.class
        );
        query.setParameter("tipo", tipo);
        query.setParameter("ambos", TipoCategoriaFinanceira.AMBOS);
        query.setParameter("ativa", ativa);
        List<CategoriaFinanceiraEntity> entities = query.getResultList();
        return mapper.toDomainList(entities);
    }

    @Override
    public Optional<CategoriaFinanceira> findByNome(String nome) {
        TypedQuery<CategoriaFinanceiraEntity> query = entityManager.createQuery(
            "SELECT c FROM CategoriaFinanceiraEntity c WHERE c.nome = :nome",
            CategoriaFinanceiraEntity.class
        );
        query.setParameter("nome", nome);
        try {
            CategoriaFinanceiraEntity entity = query.getSingleResult();
            return Optional.of(mapper.toDomain(entity));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public void delete(CategoriaFinanceira categoria) {
        CategoriaFinanceiraEntity entity = entityManager.find(CategoriaFinanceiraEntity.class, categoria.getId());
        if (entity != null) {
            // Soft delete - apenas marca como inativa
            entity.ativa = false;
            entityManager.merge(entity);
        }
    }

    @Override
    public boolean existsById(UUID id) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(c) FROM CategoriaFinanceiraEntity c WHERE c.id = :id",
            Long.class
        );
        query.setParameter("id", id);
        return query.getSingleResult() > 0;
    }

    @Override
    public boolean existsByNome(String nome) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(c) FROM CategoriaFinanceiraEntity c WHERE c.nome = :nome",
            Long.class
        );
        query.setParameter("nome", nome);
        return query.getSingleResult() > 0;
    }

    @Override
    public long count() {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(c) FROM CategoriaFinanceiraEntity c",
            Long.class
        );
        return query.getSingleResult();
    }

    @Override
    public long countByTipo(TipoCategoriaFinanceira tipo) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(c) FROM CategoriaFinanceiraEntity c WHERE c.tipo = :tipo OR c.tipo = :ambos",
            Long.class
        );
        query.setParameter("tipo", tipo);
        query.setParameter("ambos", TipoCategoriaFinanceira.AMBOS);
        return query.getSingleResult();
    }
}
