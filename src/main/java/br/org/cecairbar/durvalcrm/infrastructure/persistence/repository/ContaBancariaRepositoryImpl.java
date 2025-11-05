package br.org.cecairbar.durvalcrm.infrastructure.persistence.repository;

import br.org.cecairbar.durvalcrm.application.financeiro.ContaBancariaMapper;
import br.org.cecairbar.durvalcrm.domain.model.ContaBancaria;
import br.org.cecairbar.durvalcrm.domain.model.StatusConta;
import br.org.cecairbar.durvalcrm.domain.repository.ContaBancariaRepository;
import br.org.cecairbar.durvalcrm.infrastructure.persistence.entity.ContaBancariaEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementação do repositório de Conta Bancária.
 *
 * US-060: Cadastro de Contas Bancárias e Caixa
 */
@ApplicationScoped
public class ContaBancariaRepositoryImpl implements ContaBancariaRepository {

    @Inject
    EntityManager entityManager;

    @Inject
    ContaBancariaMapper mapper;

    @Override
    @Transactional
    public ContaBancaria save(ContaBancaria conta) {
        if (conta.getId() == null) {
            ContaBancariaEntity entity = mapper.toEntity(conta);
            entityManager.persist(entity);
            entityManager.flush();
            return mapper.toDomain(entity);
        } else {
            ContaBancariaEntity entity = entityManager.find(ContaBancariaEntity.class, conta.getId());
            if (entity != null) {
                mapper.updateEntityFromDomain(conta, entity);
                entity = entityManager.merge(entity);
                return mapper.toDomain(entity);
            }
            throw new jakarta.ws.rs.NotFoundException("Conta não encontrada: " + conta.getId());
        }
    }

    @Override
    public Optional<ContaBancaria> findById(UUID id) {
        try {
            ContaBancariaEntity entity = entityManager.find(ContaBancariaEntity.class, id);
            return entity != null ? Optional.of(mapper.toDomain(entity)) : Optional.empty();
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<ContaBancaria> findAll() {
        TypedQuery<ContaBancariaEntity> query = entityManager.createQuery(
            "SELECT c FROM ContaBancariaEntity c ORDER BY c.nome",
            ContaBancariaEntity.class
        );
        return mapper.toDomainList(query.getResultList());
    }

    @Override
    public List<ContaBancaria> findByStatus(StatusConta status) {
        TypedQuery<ContaBancariaEntity> query = entityManager.createQuery(
            "SELECT c FROM ContaBancariaEntity c WHERE c.status = :status ORDER BY c.nome",
            ContaBancariaEntity.class
        );
        query.setParameter("status", status);
        return mapper.toDomainList(query.getResultList());
    }

    @Override
    public Optional<ContaBancaria> findByNome(String nome) {
        try {
            TypedQuery<ContaBancariaEntity> query = entityManager.createQuery(
                "SELECT c FROM ContaBancariaEntity c WHERE c.nome = :nome",
                ContaBancariaEntity.class
            );
            query.setParameter("nome", nome);
            return Optional.of(mapper.toDomain(query.getSingleResult()));
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        ContaBancariaEntity entity = entityManager.find(ContaBancariaEntity.class, id);
        if (entity != null) {
            entityManager.remove(entity);
        }
    }

    @Override
    public boolean existsByNome(String nome) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(c) FROM ContaBancariaEntity c WHERE c.nome = :nome",
            Long.class
        );
        query.setParameter("nome", nome);
        return query.getSingleResult() > 0;
    }
}
