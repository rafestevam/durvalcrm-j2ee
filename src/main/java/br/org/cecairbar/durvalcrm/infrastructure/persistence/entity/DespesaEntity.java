package br.org.cecairbar.durvalcrm.infrastructure.persistence.entity;

import br.org.cecairbar.durvalcrm.domain.model.TipoDespesa;
import br.org.cecairbar.durvalcrm.domain.model.StatusPagamentoDespesa;
import br.org.cecairbar.durvalcrm.domain.model.FormaPagamento;
import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "despesas",
       indexes = {
           @Index(name = "idx_despesa_data", columnList = "data_despesa"),
           @Index(name = "idx_despesa_vencimento", columnList = "data_vencimento"),
           @Index(name = "idx_despesa_pagamento", columnList = "data_pagamento"),
           @Index(name = "idx_despesa_tipo", columnList = "tipo_despesa"),
           @Index(name = "idx_despesa_categoria", columnList = "categoria_id"),
           @Index(name = "idx_despesa_status", columnList = "status_pagamento"),
           @Index(name = "idx_despesa_fornecedor", columnList = "fornecedor")
       })
public class DespesaEntity {

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
    @Column(name = "data_despesa", nullable = false)
    public LocalDate dataDespesa;

    @NotNull
    @Column(name = "data_vencimento", nullable = false)
    public LocalDate dataVencimento;

    @Column(name = "data_pagamento")
    public LocalDate dataPagamento;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_despesa", nullable = false, length = 20)
    public TipoDespesa tipoDespesa;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    public CategoriaFinanceiraEntity categoria;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status_pagamento", nullable = false, length = 20)
    public StatusPagamentoDespesa statusPagamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pagamento", length = 20)
    public FormaPagamento formaPagamento;

    @Column(length = 200)
    public String fornecedor;

    @Column(name = "numero_documento", length = 100)
    public String numeroDocumento;

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
        if (dataDespesa == null) {
            dataDespesa = LocalDate.now();
        }
        if (statusPagamento == null) {
            statusPagamento = StatusPagamentoDespesa.PENDENTE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        // Atualizar status automaticamente se pago
        if (dataPagamento != null && statusPagamento == StatusPagamentoDespesa.PENDENTE) {
            statusPagamento = StatusPagamentoDespesa.PAGO;
        }
        // Marcar como vencido se passou da data de vencimento e ainda está pendente
        if (dataVencimento != null && dataVencimento.isBefore(LocalDate.now())
            && statusPagamento == StatusPagamentoDespesa.PENDENTE) {
            statusPagamento = StatusPagamentoDespesa.VENCIDO;
        }
    }
}
