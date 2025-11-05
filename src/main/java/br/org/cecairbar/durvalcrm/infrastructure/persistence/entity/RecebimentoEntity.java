package br.org.cecairbar.durvalcrm.infrastructure.persistence.entity;

import br.org.cecairbar.durvalcrm.domain.model.FormaPagamentoRecebimento;
import br.org.cecairbar.durvalcrm.domain.model.OrigemRecebimento;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade JPA para persistência de recebimentos financeiros.
 *
 * US-061: Registro de Recebimentos por Forma de Pagamento
 */
@Entity
@Table(name = "recebimentos", indexes = {
    @Index(name = "idx_recebimento_data", columnList = "data_recebimento"),
    @Index(name = "idx_recebimento_conta", columnList = "conta_bancaria_id"),
    @Index(name = "idx_recebimento_associado", columnList = "associado_id")
})
@Data
public class RecebimentoEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @org.hibernate.annotations.GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    public UUID id;

    @Column(name = "data_recebimento", nullable = false)
    public LocalDate dataRecebimento;

    @Column(nullable = false, precision = 10, scale = 2)
    public BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pagamento", nullable = false, length = 20)
    public FormaPagamentoRecebimento formaPagamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    public OrigemRecebimento origem;

    // Relacionamento com ContaBancaria
    @Column(name = "conta_bancaria_id", nullable = false)
    public UUID contaBancariaId;

    // Vinculações opcionais
    @Column(name = "associado_id")
    public UUID associadoId;

    @Column(name = "venda_id")
    public UUID vendaId;

    @Column(name = "mensalidade_id")
    public UUID mensalidadeId;

    // Informações adicionais
    @Column(length = 255)
    public String descricao;

    @Column(columnDefinition = "TEXT")
    public String observacoes;

    // Auditoria
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    public LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    public LocalDateTime updatedAt;
}
