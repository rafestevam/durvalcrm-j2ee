package br.org.cecairbar.durvalcrm.infrastructure.persistence.entity;

import br.org.cecairbar.durvalcrm.domain.model.TipoReceita;
import br.org.cecairbar.durvalcrm.domain.model.MetodoPagamento;
import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "receitas",
       indexes = {
           @Index(name = "idx_receita_data", columnList = "data_receita"),
           @Index(name = "idx_receita_recebimento", columnList = "data_recebimento"),
           @Index(name = "idx_receita_tipo", columnList = "tipo_receita"),
           @Index(name = "idx_receita_categoria", columnList = "categoria_id"),
           @Index(name = "idx_receita_origem", columnList = "origem_id"),
           @Index(name = "idx_receita_associado", columnList = "associado_id")
       })
public class ReceitaEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    public UUID id;

    @NotNull
    @Column(nullable = false, length = 255)
    public String descricao;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false, precision = 15, scale = 2)
    public BigDecimal valor;

    @NotNull
    @Column(name = "data_receita", nullable = false)
    public LocalDate dataReceita;

    @Column(name = "data_recebimento")
    public LocalDate dataRecebimento;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_receita", nullable = false, length = 20)
    public TipoReceita tipoReceita;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    public CategoriaFinanceiraEntity categoria;

    @Column(name = "origem_id")
    public UUID origemId; // Referência opcional

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "associado_id")
    public AssociadoEntity associado; // Opcional

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pagamento", length = 20)
    public MetodoPagamento metodoPagamento;

    @Column(name = "codigo_transacao", length = 100)
    public String codigoTransacao;

    @Column(length = 1000)
    public String observacoes;

    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt;

    @Column(name = "updated_at")
    public LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (dataReceita == null) {
            dataReceita = LocalDate.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
