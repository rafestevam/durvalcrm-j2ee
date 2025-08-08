package br.org.cecairbar.durvalcrm.infrastructure.persistence.repository;

import br.org.cecairbar.durvalcrm.application.mapper.AssociadoMapper;
import br.org.cecairbar.durvalcrm.domain.model.Associado;
import br.org.cecairbar.durvalcrm.domain.repository.AssociadoRepository;
import br.org.cecairbar.durvalcrm.infrastructure.persistence.entity.AssociadoEntity;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class AssociadoRepositoryImpl implements AssociadoRepository {

    @Inject
    EntityManager entityManager;

    @Inject
    AssociadoMapper mapper;

    @Override
    @Transactional
    public Associado save(Associado associado) {
        if (associado.getId() == null) {
            // Lógica de Criação
            AssociadoEntity entity = mapper.toEntity(associado);
            entityManager.persist(entity);
            entityManager.flush();
            return mapper.toDomain(entity);
        } else {
            // Lógica de Atualização
            AssociadoEntity entity = entityManager.find(AssociadoEntity.class, associado.getId());
            if (entity != null) {
                // Atualiza os campos da entidade existente
                entity.nomeCompleto = associado.getNomeCompleto();
                entity.cpf = associado.getCpf();
                entity.email = associado.getEmail();
                entity.telefone = associado.getTelefone();
                entity.ativo = associado.isAtivo();
                entity = entityManager.merge(entity);
                return mapper.toDomain(entity);
            } else {
                throw new NotFoundException("Associado com ID " + associado.getId() + " não encontrado para atualização.");
            }
        }
    }

    @Override
    public Optional<Associado> findById(UUID id) {
        try {
            TypedQuery<AssociadoEntity> query = entityManager.createQuery(
                "SELECT a FROM AssociadoEntity a WHERE a.id = :id AND a.ativo = true", 
                AssociadoEntity.class
            );
            query.setParameter("id", id);
            AssociadoEntity entity = query.getSingleResult();
            return Optional.of(mapper.toDomain(entity));
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Associado> findAll(String query) {
        String searchPattern = "%" + (query == null ? "" : query).toLowerCase() + "%";
        
        TypedQuery<AssociadoEntity> jpqlQuery = entityManager.createQuery(
            "SELECT a FROM AssociadoEntity a WHERE a.ativo = true AND " +
            "(LOWER(a.nomeCompleto) LIKE :query OR a.cpf LIKE :query)", 
            AssociadoEntity.class
        );
        jpqlQuery.setParameter("query", searchPattern);
        
        List<AssociadoEntity> entities = jpqlQuery.getResultList();
        return entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        // Soft delete: marca como inativo ao invés de deletar fisicamente
        entityManager.createQuery("UPDATE AssociadoEntity a SET a.ativo = false WHERE a.id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }

    @Override
    public Optional<Associado> findByCpf(String cpf) {
        try {
            TypedQuery<AssociadoEntity> query = entityManager.createQuery(
                "SELECT a FROM AssociadoEntity a WHERE a.cpf = :cpf", 
                AssociadoEntity.class
            );
            query.setParameter("cpf", cpf);
            AssociadoEntity entity = query.getSingleResult();
            return Optional.of(mapper.toDomain(entity));
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Associado> findByEmail(String email) {
        try {
            TypedQuery<AssociadoEntity> query = entityManager.createQuery(
                "SELECT a FROM AssociadoEntity a WHERE a.email = :email AND a.ativo = true", 
                AssociadoEntity.class
            );
            query.setParameter("email", email);
            AssociadoEntity entity = query.getSingleResult();
            return Optional.of(mapper.toDomain(entity));
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Associado> findByAtivo(Boolean ativo) {
        TypedQuery<AssociadoEntity> query = entityManager.createQuery(
            "SELECT a FROM AssociadoEntity a WHERE a.ativo = :ativo", 
            AssociadoEntity.class
        );
        query.setParameter("ativo", ativo);
        
        List<AssociadoEntity> entities = query.getResultList();
        return entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Associado> findAll() {
        TypedQuery<AssociadoEntity> query = entityManager.createQuery(
            "SELECT a FROM AssociadoEntity a", 
            AssociadoEntity.class
        );
        
        List<AssociadoEntity> entities = query.getResultList();
        return entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public long count() {
        return entityManager.createQuery("SELECT COUNT(a) FROM AssociadoEntity a", Long.class)
                .getSingleResult();
    }
}