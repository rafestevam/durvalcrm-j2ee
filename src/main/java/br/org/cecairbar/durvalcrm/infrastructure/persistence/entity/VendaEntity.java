package br.org.cecairbar.durvalcrm.infrastructure.persistence.entity;

import br.org.cecairbar.durvalcrm.domain.model.OrigemVenda;
import br.org.cecairbar.durvalcrm.domain.model.FormaPagamento;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "vendas")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendaEntity {
    
    @Id
    @Column(name = "id", nullable = false)
    public UUID id;
    
    @Column(name = "descricao", nullable = false, length = 255)
    public String descricao;
    
    @Column(name = "valor", nullable = false, precision = 10, scale = 2)
    public BigDecimal valor;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "origem", nullable = false)
    public OrigemVenda origem;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pagamento", nullable = false)
    public FormaPagamento formaPagamento;
    
    @Column(name = "data_venda", nullable = false)
    public Instant dataVenda;
    
    @Column(name = "criado_em", nullable = false)
    public Instant criadoEm;
    
    @Column(name = "atualizado_em")
    public Instant atualizadoEm;
    
    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (criadoEm == null) {
            criadoEm = Instant.now();
        }
        if (dataVenda == null) {
            dataVenda = Instant.now();
        }
    }
    
    @PreUpdate
    public void preUpdate() {
        atualizadoEm = Instant.now();
    }
}