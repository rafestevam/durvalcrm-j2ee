package br.org.cecairbar.durvalcrm.infrastructure.persistence.repository;

import br.org.cecairbar.durvalcrm.application.financeiro.RecebimentoMapper;
import br.org.cecairbar.durvalcrm.domain.model.OrigemRecebimento;
import br.org.cecairbar.durvalcrm.domain.model.Recebimento;
import br.org.cecairbar.durvalcrm.domain.repository.RecebimentoRepository;
import br.org.cecairbar.durvalcrm.infrastructure.persistence.entity.RecebimentoEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementação do repositório de Recebimento.
 *
 * US-061: Registro de Recebimentos por Forma de Pagamento
 */
@ApplicationScoped
public class RecebimentoRepositoryImpl implements RecebimentoRepository {

    @Inject
    EntityManager entityManager;

    @Inject
    RecebimentoMapper mapper;

    @Override
    @Transactional
    public Recebimento save(Recebimento recebimento) {
        if (recebimento.getId() == null) {
            RecebimentoEntity entity = mapper.toEntity(recebimento);
            entityManager.persist(entity);
            entityManager.flush();
            return mapper.toDomain(entity);
        } else {
            RecebimentoEntity entity = entityManager.find(RecebimentoEntity.class, recebimento.getId());
            if (entity != null) {
                mapper.updateEntityFromDomain(recebimento, entity);
                entity = entityManager.merge(entity);
                return mapper.toDomain(entity);
            }
            throw new jakarta.ws.rs.NotFoundException("Recebimento não encontrado: " + recebimento.getId());
        }
    }

    @Override
    public Optional<Recebimento> findById(UUID id) {
        RecebimentoEntity entity = entityManager.find(RecebimentoEntity.class, id);
        return entity != null ? Optional.of(mapper.toDomain(entity)) : Optional.empty();
    }

    @Override
    public List<Recebimento> findAll() {
        TypedQuery<RecebimentoEntity> query = entityManager.createQuery(
            "SELECT r FROM RecebimentoEntity r ORDER BY r.dataRecebimento DESC",
            RecebimentoEntity.class
        );
        return mapper.toDomainList(query.getResultList());
    }

    @Override
    public List<Recebimento> findByContaBancariaId(UUID contaId) {
        TypedQuery<RecebimentoEntity> query = entityManager.createQuery(
            "SELECT r FROM RecebimentoEntity r WHERE r.contaBancariaId = :contaId ORDER BY r.dataRecebimento DESC",
            RecebimentoEntity.class
        );
        query.setParameter("contaId", contaId);
        return mapper.toDomainList(query.getResultList());
    }

    @Override
    public List<Recebimento> findByPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        TypedQuery<RecebimentoEntity> query = entityManager.createQuery(
            "SELECT r FROM RecebimentoEntity r WHERE r.dataRecebimento BETWEEN :dataInicio AND :dataFim ORDER BY r.dataRecebimento DESC",
            RecebimentoEntity.class
        );
        query.setParameter("dataInicio", dataInicio);
        query.setParameter("dataFim", dataFim);
        return mapper.toDomainList(query.getResultList());
    }

    @Override
    public List<Recebimento> findByOrigem(OrigemRecebimento origem) {
        TypedQuery<RecebimentoEntity> query = entityManager.createQuery(
            "SELECT r FROM RecebimentoEntity r WHERE r.origem = :origem ORDER BY r.dataRecebimento DESC",
            RecebimentoEntity.class
        );
        query.setParameter("origem", origem);
        return mapper.toDomainList(query.getResultList());
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        RecebimentoEntity entity = entityManager.find(RecebimentoEntity.class, id);
        if (entity != null) {
            entityManager.remove(entity);
        }
    }
}
