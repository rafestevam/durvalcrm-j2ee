package br.org.cecairbar.durvalcrm.infrastructure.persistence.repository;

import br.org.cecairbar.durvalcrm.domain.model.Mensalidade;
import br.org.cecairbar.durvalcrm.domain.model.StatusMensalidade;
import br.org.cecairbar.durvalcrm.domain.repository.MensalidadeRepository;
import br.org.cecairbar.durvalcrm.infrastructure.persistence.entity.MensalidadeEntity;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class MensalidadeRepositoryImpl implements MensalidadeRepository {

    @Inject
    EntityManager entityManager;

    @Override
    @Transactional
    public void save(Mensalidade mensalidade) {
        MensalidadeEntity entity = MensalidadeEntity.fromDomain(mensalidade);
        entityManager.persist(entity);
    }

    @Override
    public Mensalidade findById(UUID id) {
        try {
            MensalidadeEntity entity = entityManager.find(MensalidadeEntity.class, id);
            return entity != null ? entity.toDomain() : null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Mensalidade> findByMesEAno(int mes, int ano) {
        TypedQuery<MensalidadeEntity> query = entityManager.createQuery(
            "SELECT m FROM MensalidadeEntity m WHERE m.mesReferencia = :mes AND m.anoReferencia = :ano ORDER BY m.associadoId", 
            MensalidadeEntity.class
        );
        query.setParameter("mes", mes);
        query.setParameter("ano", ano);
        
        return query.getResultList().stream()
                .map(MensalidadeEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Mensalidade> findByAssociadoId(UUID associadoId) {
        TypedQuery<MensalidadeEntity> query = entityManager.createQuery(
            "SELECT m FROM MensalidadeEntity m WHERE m.associadoId = :associadoId ORDER BY m.anoReferencia DESC, m.mesReferencia DESC", 
            MensalidadeEntity.class
        );
        query.setParameter("associadoId", associadoId);
        
        return query.getResultList().stream()
                .map(MensalidadeEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByAssociadoEPeriodo(UUID associadoId, int mes, int ano) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(m) FROM MensalidadeEntity m WHERE m.associadoId = :associadoId AND m.mesReferencia = :mes AND m.anoReferencia = :ano",
            Long.class
        );
        query.setParameter("associadoId", associadoId);
        query.setParameter("mes", mes);
        query.setParameter("ano", ano);
        
        return query.getSingleResult() > 0;
    }

    @Override
    public List<Mensalidade> findByStatus(String status) {
        try {
            StatusMensalidade statusEnum = StatusMensalidade.valueOf(status.toUpperCase());
            TypedQuery<MensalidadeEntity> query = entityManager.createQuery(
                "SELECT m FROM MensalidadeEntity m WHERE m.status = :status", 
                MensalidadeEntity.class
            );
            query.setParameter("status", statusEnum);
            
            return query.getResultList().stream()
                    .map(MensalidadeEntity::toDomain)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            // Se o status não for válido, retorna lista vazia
            return List.of();
        }
    }

    @Override
    public List<Mensalidade> findVencidas() {
        TypedQuery<MensalidadeEntity> query = entityManager.createQuery(
            "SELECT m FROM MensalidadeEntity m WHERE m.status = 'PENDENTE' AND m.dataVencimento < :hoje", 
            MensalidadeEntity.class
        );
        query.setParameter("hoje", LocalDate.now());
        
        return query.getResultList().stream()
                .map(MensalidadeEntity::toDomain)
                .collect(Collectors.toList());
    }

    // Note: These methods are not in the interface, removing @Override annotations
    @Transactional
    public void deleteById(UUID id) {
        entityManager.createQuery("DELETE FROM MensalidadeEntity m WHERE m.id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }

    public long countByMesEAno(int mes, int ano) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(m) FROM MensalidadeEntity m WHERE m.mesReferencia = :mes AND m.anoReferencia = :ano",
            Long.class
        );
        query.setParameter("mes", mes);
        query.setParameter("ano", ano);
        
        return query.getSingleResult();
    }

    public long countByMesEAnoEStatus(int mes, int ano, StatusMensalidade status) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(m) FROM MensalidadeEntity m WHERE m.mesReferencia = :mes AND m.anoReferencia = :ano AND m.status = :status",
            Long.class
        );
        query.setParameter("mes", mes);
        query.setParameter("ano", ano);
        query.setParameter("status", status);
        
        return query.getSingleResult();
    }

    @Override
    public Mensalidade findByIdentificadorPix(String identificadorPix) {
        try {
            TypedQuery<MensalidadeEntity> query = entityManager.createQuery(
                "SELECT m FROM MensalidadeEntity m WHERE m.identificadorPix = :identificadorPix", 
                MensalidadeEntity.class
            );
            query.setParameter("identificadorPix", identificadorPix);
            MensalidadeEntity entity = query.getSingleResult();
            return entity.toDomain();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<Mensalidade> findByAssociadoIdAndMesEAno(UUID associadoId, int mes, int ano) {
        TypedQuery<MensalidadeEntity> query = entityManager.createQuery(
            "SELECT m FROM MensalidadeEntity m WHERE m.associadoId = :associadoId AND m.mesReferencia = :mes AND m.anoReferencia = :ano", 
            MensalidadeEntity.class
        );
        query.setParameter("associadoId", associadoId);
        query.setParameter("mes", mes);
        query.setParameter("ano", ano);
        
        return query.getResultList().stream()
                .map(MensalidadeEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countByStatusAndMesEAno(String status, int mes, int ano) {
        try {
            StatusMensalidade statusEnum = StatusMensalidade.valueOf(status.toUpperCase());
            TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(m) FROM MensalidadeEntity m WHERE m.status = :status AND m.mesReferencia = :mes AND m.anoReferencia = :ano",
                Long.class
            );
            query.setParameter("status", statusEnum);
            query.setParameter("mes", mes);
            query.setParameter("ano", ano);
            
            return query.getSingleResult();
        } catch (IllegalArgumentException e) {
            return 0;
        }
    }

    @Override
    public List<Mensalidade> findAll() {
        TypedQuery<MensalidadeEntity> query = entityManager.createQuery(
            "SELECT m FROM MensalidadeEntity m", 
            MensalidadeEntity.class
        );
        
        return query.getResultList().stream()
                .map(MensalidadeEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Mensalidade> findByAno(int ano) {
        TypedQuery<MensalidadeEntity> query = entityManager.createQuery(
            "SELECT m FROM MensalidadeEntity m WHERE m.anoReferencia = :ano ORDER BY m.mesReferencia", 
            MensalidadeEntity.class
        );
        query.setParameter("ano", ano);
        
        return query.getResultList().stream()
                .map(MensalidadeEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void update(Mensalidade mensalidade) {
        // Usar update direto via JPQL para garantir que seja persistido
        int updated = entityManager.createQuery(
            "UPDATE MensalidadeEntity m SET " +
            "m.associadoId = :associadoId, m.mesReferencia = :mesReferencia, m.anoReferencia = :anoReferencia, " +
            "m.valor = :valor, m.status = :status, m.dataVencimento = :dataVencimento, " +
            "m.dataPagamento = :dataPagamento, m.qrCodePix = :qrCodePix, m.identificadorPix = :identificadorPix " +
            "WHERE m.id = :id"
        )
        .setParameter("associadoId", mensalidade.getAssociadoId())
        .setParameter("mesReferencia", mensalidade.getMesReferencia())
        .setParameter("anoReferencia", mensalidade.getAnoReferencia())
        .setParameter("valor", mensalidade.getValor())
        .setParameter("status", mensalidade.getStatus())
        .setParameter("dataVencimento", mensalidade.getDataVencimento())
        .setParameter("dataPagamento", mensalidade.getDataPagamento())
        .setParameter("qrCodePix", mensalidade.getQrCodePix())
        .setParameter("identificadorPix", mensalidade.getIdentificadorPix())
        .setParameter("id", mensalidade.getId())
        .executeUpdate();
        
        if (updated == 0) {
            throw new RuntimeException("Mensalidade não encontrada para atualização: " + mensalidade.getId());
        }
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        entityManager.createQuery("DELETE FROM MensalidadeEntity m WHERE m.id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }

    @Override
    public List<Mensalidade> findPendentesByMesEAno(int mes, int ano) {
        TypedQuery<MensalidadeEntity> query = entityManager.createQuery(
            "SELECT m FROM MensalidadeEntity m WHERE m.status = :status AND m.mesReferencia = :mes AND m.anoReferencia = :ano",
            MensalidadeEntity.class
        );
        query.setParameter("status", StatusMensalidade.PENDENTE);
        query.setParameter("mes", mes);
        query.setParameter("ano", ano);
        
        return query.getResultList().stream()
                .map(MensalidadeEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Mensalidade> findPagasByMesEAno(int mes, int ano) {
        TypedQuery<MensalidadeEntity> query = entityManager.createQuery(
            "SELECT m FROM MensalidadeEntity m WHERE m.status = :status AND m.mesReferencia = :mes AND m.anoReferencia = :ano",
            MensalidadeEntity.class
        );
        query.setParameter("status", StatusMensalidade.PAGA);
        query.setParameter("mes", mes);
        query.setParameter("ano", ano);
        
        return query.getResultList().stream()
                .map(MensalidadeEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Mensalidade> findAtrasadasByMesEAno(int mes, int ano) {
        TypedQuery<MensalidadeEntity> query = entityManager.createQuery(
            "SELECT m FROM MensalidadeEntity m WHERE m.status = :status AND m.mesReferencia = :mes AND m.anoReferencia = :ano",
            MensalidadeEntity.class
        );
        query.setParameter("status", StatusMensalidade.ATRASADA);
        query.setParameter("mes", mes);
        query.setParameter("ano", ano);
        
        return query.getResultList().stream()
                .map(MensalidadeEntity::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public BigDecimal obterValorArrecadadoPorPeriodo(int mes, int ano) {
        TypedQuery<MensalidadeEntity> query = entityManager.createQuery(
            "SELECT m FROM MensalidadeEntity m WHERE m.status = :status AND m.mesReferencia = :mes AND m.anoReferencia = :ano",
            MensalidadeEntity.class
        );
        query.setParameter("status", StatusMensalidade.PAGA);
        query.setParameter("mes", mes);
        query.setParameter("ano", ano);
        
        List<MensalidadeEntity> mensalidadesPagas = query.getResultList();
        
        return mensalidadesPagas.stream()
            .map(entity -> entity.valor)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    @Override
    public List<String> obterAssociadosComStatusPorPeriodo(int mes, int ano, StatusMensalidade status) {
        TypedQuery<MensalidadeEntity> query = entityManager.createQuery(
            "SELECT m FROM MensalidadeEntity m WHERE m.status = :status AND m.mesReferencia = :mes AND m.anoReferencia = :ano",
            MensalidadeEntity.class
        );
        query.setParameter("status", status);
        query.setParameter("mes", mes);
        query.setParameter("ano", ano);
        
        return query.getResultList().stream()
                .map(entity -> entity.associadoId.toString())
                .collect(Collectors.toList());
    }
    
    @Override
    public List<String> obterAssociadosComMensalidadesVencidas(int mes, int ano) {
        LocalDate hoje = LocalDate.now();
        TypedQuery<MensalidadeEntity> query = entityManager.createQuery(
            "SELECT m FROM MensalidadeEntity m WHERE (m.status = :pendente OR m.status = :atrasada) " +
            "AND m.mesReferencia = :mes AND m.anoReferencia = :ano AND m.dataVencimento <= :hoje",
            MensalidadeEntity.class
        );
        query.setParameter("pendente", StatusMensalidade.PENDENTE);
        query.setParameter("atrasada", StatusMensalidade.ATRASADA);
        query.setParameter("mes", mes);
        query.setParameter("ano", ano);
        query.setParameter("hoje", hoje);
        
        return query.getResultList().stream()
                .map(entity -> entity.associadoId.toString())
                .collect(Collectors.toList());
    }
}