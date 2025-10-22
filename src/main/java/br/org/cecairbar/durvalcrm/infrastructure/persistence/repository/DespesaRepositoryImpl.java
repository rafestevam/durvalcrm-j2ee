package br.org.cecairbar.durvalcrm.infrastructure.persistence.repository;

import br.org.cecairbar.durvalcrm.application.financeiro.DespesaEntityMapper;
import br.org.cecairbar.durvalcrm.domain.model.Despesa;
import br.org.cecairbar.durvalcrm.domain.model.StatusPagamentoDespesa;
import br.org.cecairbar.durvalcrm.domain.model.TipoDespesa;
import br.org.cecairbar.durvalcrm.domain.repository.DespesaRepository;
import br.org.cecairbar.durvalcrm.infrastructure.persistence.entity.DespesaEntity;

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
public class DespesaRepositoryImpl implements DespesaRepository {

    @Inject
    EntityManager entityManager;

    @Inject
    DespesaEntityMapper mapper;

    @Override
    @Transactional
    public Despesa save(Despesa despesa) {
        if (despesa.getId() == null) {
            DespesaEntity entity = mapper.toEntity(despesa);
            entityManager.persist(entity);
            entityManager.flush();
            return mapper.toDomain(entity);
        } else {
            DespesaEntity entity = entityManager.find(DespesaEntity.class, despesa.getId());
            if (entity != null) {
                mapper.updateEntityFromDomain(despesa, entity);
                entity = entityManager.merge(entity);
                return mapper.toDomain(entity);
            } else {
                throw new IllegalArgumentException("Despesa com ID " + despesa.getId() + " não encontrada para atualização.");
            }
        }
    }

    @Override
    public Optional<Despesa> findById(UUID id) {
        try {
            DespesaEntity entity = entityManager.find(DespesaEntity.class, id);
            return entity != null ? Optional.of(mapper.toDomain(entity)) : Optional.empty();
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Despesa> findAll() {
        TypedQuery<DespesaEntity> query = entityManager.createQuery(
            "SELECT d FROM DespesaEntity d ORDER BY d.dataVencimento DESC",
            DespesaEntity.class
        );
        List<DespesaEntity> entities = query.getResultList();
        return mapper.toDomainList(entities);
    }

    @Override
    public List<Despesa> findByTipoDespesa(TipoDespesa tipo) {
        TypedQuery<DespesaEntity> query = entityManager.createQuery(
            "SELECT d FROM DespesaEntity d WHERE d.tipoDespesa = :tipo ORDER BY d.dataVencimento DESC",
            DespesaEntity.class
        );
        query.setParameter("tipo", tipo);
        List<DespesaEntity> entities = query.getResultList();
        return mapper.toDomainList(entities);
    }

    @Override
    public List<Despesa> findByCategoria(UUID categoriaId) {
        TypedQuery<DespesaEntity> query = entityManager.createQuery(
            "SELECT d FROM DespesaEntity d WHERE d.categoria.id = :categoriaId ORDER BY d.dataVencimento DESC",
            DespesaEntity.class
        );
        query.setParameter("categoriaId", categoriaId);
        List<DespesaEntity> entities = query.getResultList();
        return mapper.toDomainList(entities);
    }

    @Override
    public List<Despesa> findByStatus(StatusPagamentoDespesa status) {
        TypedQuery<DespesaEntity> query = entityManager.createQuery(
            "SELECT d FROM DespesaEntity d WHERE d.statusPagamento = :status ORDER BY d.dataVencimento DESC",
            DespesaEntity.class
        );
        query.setParameter("status", status);
        List<DespesaEntity> entities = query.getResultList();
        return mapper.toDomainList(entities);
    }

    @Override
    public List<Despesa> findByPeriodo(LocalDate inicio, LocalDate fim) {
        TypedQuery<DespesaEntity> query = entityManager.createQuery(
            "SELECT d FROM DespesaEntity d WHERE d.dataDespesa >= :inicio AND d.dataDespesa <= :fim ORDER BY d.dataDespesa DESC",
            DespesaEntity.class
        );
        query.setParameter("inicio", inicio);
        query.setParameter("fim", fim);
        List<DespesaEntity> entities = query.getResultList();
        return mapper.toDomainList(entities);
    }

    @Override
    public List<Despesa> findByDataVencimento(LocalDate inicio, LocalDate fim) {
        TypedQuery<DespesaEntity> query = entityManager.createQuery(
            "SELECT d FROM DespesaEntity d WHERE d.dataVencimento >= :inicio AND d.dataVencimento <= :fim ORDER BY d.dataVencimento",
            DespesaEntity.class
        );
        query.setParameter("inicio", inicio);
        query.setParameter("fim", fim);
        List<DespesaEntity> entities = query.getResultList();
        return mapper.toDomainList(entities);
    }

    @Override
    public List<Despesa> findByDataPagamento(LocalDate inicio, LocalDate fim) {
        TypedQuery<DespesaEntity> query = entityManager.createQuery(
            "SELECT d FROM DespesaEntity d WHERE d.dataPagamento >= :inicio AND d.dataPagamento <= :fim ORDER BY d.dataPagamento DESC",
            DespesaEntity.class
        );
        query.setParameter("inicio", inicio);
        query.setParameter("fim", fim);
        List<DespesaEntity> entities = query.getResultList();
        return mapper.toDomainList(entities);
    }

    @Override
    public List<Despesa> findByStatusAndDataVencimento(StatusPagamentoDespesa status, LocalDate inicio, LocalDate fim) {
        TypedQuery<DespesaEntity> query = entityManager.createQuery(
            "SELECT d FROM DespesaEntity d WHERE d.statusPagamento = :status AND d.dataVencimento >= :inicio AND d.dataVencimento <= :fim ORDER BY d.dataVencimento",
            DespesaEntity.class
        );
        query.setParameter("status", status);
        query.setParameter("inicio", inicio);
        query.setParameter("fim", fim);
        List<DespesaEntity> entities = query.getResultList();
        return mapper.toDomainList(entities);
    }

    @Override
    public List<Despesa> findByTipoAndPeriodo(TipoDespesa tipo, LocalDate inicio, LocalDate fim) {
        TypedQuery<DespesaEntity> query = entityManager.createQuery(
            "SELECT d FROM DespesaEntity d WHERE d.tipoDespesa = :tipo AND d.dataDespesa >= :inicio AND d.dataDespesa <= :fim ORDER BY d.dataDespesa DESC",
            DespesaEntity.class
        );
        query.setParameter("tipo", tipo);
        query.setParameter("inicio", inicio);
        query.setParameter("fim", fim);
        List<DespesaEntity> entities = query.getResultList();
        return mapper.toDomainList(entities);
    }

    @Override
    public List<Despesa> findByCategoriaAndPeriodo(UUID categoriaId, LocalDate inicio, LocalDate fim) {
        TypedQuery<DespesaEntity> query = entityManager.createQuery(
            "SELECT d FROM DespesaEntity d WHERE d.categoria.id = :categoriaId AND d.dataDespesa >= :inicio AND d.dataDespesa <= :fim ORDER BY d.dataDespesa DESC",
            DespesaEntity.class
        );
        query.setParameter("categoriaId", categoriaId);
        query.setParameter("inicio", inicio);
        query.setParameter("fim", fim);
        List<DespesaEntity> entities = query.getResultList();
        return mapper.toDomainList(entities);
    }

    @Override
    public List<Despesa> findByFornecedor(String fornecedor) {
        TypedQuery<DespesaEntity> query = entityManager.createQuery(
            "SELECT d FROM DespesaEntity d WHERE LOWER(d.fornecedor) LIKE LOWER(:fornecedor) ORDER BY d.dataVencimento DESC",
            DespesaEntity.class
        );
        query.setParameter("fornecedor", "%" + fornecedor + "%");
        List<DespesaEntity> entities = query.getResultList();
        return mapper.toDomainList(entities);
    }

    @Override
    public List<Despesa> findVencidas(LocalDate dataReferencia) {
        TypedQuery<DespesaEntity> query = entityManager.createQuery(
            "SELECT d FROM DespesaEntity d WHERE d.dataVencimento < :dataReferencia AND d.statusPagamento = :status ORDER BY d.dataVencimento",
            DespesaEntity.class
        );
        query.setParameter("dataReferencia", dataReferencia);
        query.setParameter("status", StatusPagamentoDespesa.PENDENTE);
        List<DespesaEntity> entities = query.getResultList();
        return mapper.toDomainList(entities);
    }

    @Override
    public BigDecimal sumByPeriodo(LocalDate inicio, LocalDate fim) {
        TypedQuery<BigDecimal> query = entityManager.createQuery(
            "SELECT SUM(d.valor) FROM DespesaEntity d WHERE d.dataDespesa >= :inicio AND d.dataDespesa <= :fim",
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
            "SELECT SUM(d.valor) FROM DespesaEntity d WHERE d.categoria.id = :categoriaId AND d.dataDespesa >= :inicio AND d.dataDespesa <= :fim",
            BigDecimal.class
        );
        query.setParameter("categoriaId", categoriaId);
        query.setParameter("inicio", inicio);
        query.setParameter("fim", fim);
        BigDecimal result = query.getSingleResult();
        return result != null ? result : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal sumByTipo(TipoDespesa tipo, LocalDate inicio, LocalDate fim) {
        TypedQuery<BigDecimal> query = entityManager.createQuery(
            "SELECT SUM(d.valor) FROM DespesaEntity d WHERE d.tipoDespesa = :tipo AND d.dataDespesa >= :inicio AND d.dataDespesa <= :fim",
            BigDecimal.class
        );
        query.setParameter("tipo", tipo);
        query.setParameter("inicio", inicio);
        query.setParameter("fim", fim);
        BigDecimal result = query.getSingleResult();
        return result != null ? result : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal sumByStatus(StatusPagamentoDespesa status, LocalDate inicio, LocalDate fim) {
        TypedQuery<BigDecimal> query = entityManager.createQuery(
            "SELECT SUM(d.valor) FROM DespesaEntity d WHERE d.statusPagamento = :status AND d.dataDespesa >= :inicio AND d.dataDespesa <= :fim",
            BigDecimal.class
        );
        query.setParameter("status", status);
        query.setParameter("inicio", inicio);
        query.setParameter("fim", fim);
        BigDecimal result = query.getSingleResult();
        return result != null ? result : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal sumByFornecedor(String fornecedor, LocalDate inicio, LocalDate fim) {
        TypedQuery<BigDecimal> query = entityManager.createQuery(
            "SELECT SUM(d.valor) FROM DespesaEntity d WHERE LOWER(d.fornecedor) LIKE LOWER(:fornecedor) AND d.dataDespesa >= :inicio AND d.dataDespesa <= :fim",
            BigDecimal.class
        );
        query.setParameter("fornecedor", "%" + fornecedor + "%");
        query.setParameter("inicio", inicio);
        query.setParameter("fim", fim);
        BigDecimal result = query.getSingleResult();
        return result != null ? result : BigDecimal.ZERO;
    }

    @Override
    public long countByStatus(StatusPagamentoDespesa status) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(d) FROM DespesaEntity d WHERE d.statusPagamento = :status",
            Long.class
        );
        query.setParameter("status", status);
        return query.getSingleResult();
    }

    @Override
    public long countByTipo(TipoDespesa tipo) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(d) FROM DespesaEntity d WHERE d.tipoDespesa = :tipo",
            Long.class
        );
        query.setParameter("tipo", tipo);
        return query.getSingleResult();
    }

    @Override
    public long countVencidas(LocalDate dataReferencia) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(d) FROM DespesaEntity d WHERE d.dataVencimento < :dataReferencia AND d.statusPagamento = :status",
            Long.class
        );
        query.setParameter("dataReferencia", dataReferencia);
        query.setParameter("status", StatusPagamentoDespesa.PENDENTE);
        return query.getSingleResult();
    }

    @Override
    @Transactional
    public void delete(Despesa despesa) {
        DespesaEntity entity = entityManager.find(DespesaEntity.class, despesa.getId());
        if (entity != null) {
            entityManager.remove(entity);
        }
    }

    @Override
    public boolean existsById(UUID id) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(d) FROM DespesaEntity d WHERE d.id = :id",
            Long.class
        );
        query.setParameter("id", id);
        return query.getSingleResult() > 0;
    }

    @Override
    public boolean existsByNumeroDocumento(String numeroDocumento) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(d) FROM DespesaEntity d WHERE d.numeroDocumento = :numeroDocumento",
            Long.class
        );
        query.setParameter("numeroDocumento", numeroDocumento);
        return query.getSingleResult() > 0;
    }
}
