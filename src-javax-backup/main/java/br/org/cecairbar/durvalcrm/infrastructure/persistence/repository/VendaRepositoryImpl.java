package br.org.cecairbar.durvalcrm.infrastructure.persistence.repository;

import br.org.cecairbar.durvalcrm.domain.model.Venda;
import br.org.cecairbar.durvalcrm.domain.model.OrigemVenda;
import br.org.cecairbar.durvalcrm.domain.repository.VendaRepository;
import br.org.cecairbar.durvalcrm.infrastructure.persistence.entity.VendaEntity;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.math.BigDecimal;

@ApplicationScoped
public class VendaRepositoryImpl implements VendaRepository {
    
    @Inject
    VendaPanacheRepository panacheRepository;
    
    @Inject
    EntityManager entityManager;
    
    @Override
    @Transactional
    public void save(Venda venda) {
        VendaEntity entity = toEntity(venda);
        panacheRepository.persist(entity);
    }
    
    @Override
    public Venda findById(UUID id) {
        VendaEntity entity = panacheRepository.find("id", id).getResultList().stream().findFirst().orElse(null);
        return entity != null ? toDomain(entity) : null;
    }
    
    @Override
    public List<Venda> findAll() {
        return panacheRepository.listAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Venda> findByPeriodo(Instant dataInicio, Instant dataFim) {
        return panacheRepository.find("dataVenda >= ?1 and dataVenda <= ?2", dataInicio, dataFim)
                .getResultList().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Venda> findByOrigem(OrigemVenda origem) {
        return panacheRepository.find("origem", origem)
                .getResultList().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Venda> findByOrigemAndPeriodo(OrigemVenda origem, Instant dataInicio, Instant dataFim) {
        return panacheRepository.find("origem = ?1 and dataVenda >= ?2 and dataVenda <= ?3", 
                   origem, dataInicio, dataFim)
                .getResultList().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public long countByPeriodo(Instant dataInicio, Instant dataFim) {
        return panacheRepository.count("dataVenda >= ?1 and dataVenda <= ?2", dataInicio, dataFim);
    }
    
    @Override
    public long countByOrigem(OrigemVenda origem) {
        return panacheRepository.count("origem", origem);
    }
    
    @Override
    public BigDecimal sumValorByPeriodo(Instant dataInicio, Instant dataFim) {
        TypedQuery<BigDecimal> query = entityManager.createQuery(
            "SELECT SUM(v.valor) FROM VendaEntity v WHERE v.dataVenda >= :inicio AND v.dataVenda <= :fim", 
            BigDecimal.class
        );
        query.setParameter("inicio", dataInicio);
        query.setParameter("fim", dataFim);
        
        BigDecimal result = query.getSingleResult();
        return result != null ? result : BigDecimal.ZERO;
    }
    
    @Override
    public BigDecimal sumValorByOrigem(OrigemVenda origem) {
        TypedQuery<BigDecimal> query = entityManager.createQuery(
            "SELECT SUM(v.valor) FROM VendaEntity v WHERE v.origem = :origem", 
            BigDecimal.class
        );
        query.setParameter("origem", origem);
        
        BigDecimal result = query.getSingleResult();
        return result != null ? result : BigDecimal.ZERO;
    }
    
    @Override
    public BigDecimal sumValorByOrigemAndPeriodo(OrigemVenda origem, Instant dataInicio, Instant dataFim) {
        TypedQuery<BigDecimal> query = entityManager.createQuery(
            "SELECT SUM(v.valor) FROM VendaEntity v WHERE v.origem = :origem AND v.dataVenda >= :inicio AND v.dataVenda <= :fim", 
            BigDecimal.class
        );
        query.setParameter("origem", origem);
        query.setParameter("inicio", dataInicio);
        query.setParameter("fim", dataFim);
        
        BigDecimal result = query.getSingleResult();
        return result != null ? result : BigDecimal.ZERO;
    }
    
    @Override
    @Transactional
    public void update(Venda venda) {
        VendaEntity entity = toEntity(venda);
        entity.atualizadoEm = Instant.now();
        panacheRepository.persist(entity);
    }
    
    @Override
    @Transactional
    public void delete(UUID id) {
        panacheRepository.delete("id", id);
    }
    
    @Override
    public boolean existsById(UUID id) {
        VendaEntity entity = panacheRepository.find("id", id).getResultList().stream().findFirst().orElse(null);
        return entity != null;
    }
    
    @Override
    public List<Venda> findRecentes() {
        Instant trintaDiasAtras = Instant.now().minus(30, ChronoUnit.DAYS);
        return findByPeriodo(trintaDiasAtras, Instant.now());
    }
    
    // Métodos de conversão
    private VendaEntity toEntity(Venda venda) {
        return VendaEntity.builder()
                .id(venda.getId())
                .descricao(venda.getDescricao())
                .valor(venda.getValor())
                .origem(venda.getOrigem())
                .formaPagamento(venda.getFormaPagamento())
                .dataVenda(venda.getDataVenda())
                .criadoEm(venda.getCriadoEm())
                .atualizadoEm(venda.getAtualizadoEm())
                .build();
    }
    
    private Venda toDomain(VendaEntity entity) {
        return Venda.builder()
                .id(entity.id)
                .descricao(entity.descricao)
                .valor(entity.valor)
                .origem(entity.origem)
                .formaPagamento(entity.formaPagamento)
                .dataVenda(entity.dataVenda)
                .criadoEm(entity.criadoEm)
                .atualizadoEm(entity.atualizadoEm)
                .build();
    }
}