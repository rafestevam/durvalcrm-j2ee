package br.org.cecairbar.durvalcrm.infrastructure.persistence.entity;

import br.org.cecairbar.durvalcrm.domain.model.Mensalidade;
import br.org.cecairbar.durvalcrm.domain.model.StatusMensalidade;
import br.org.cecairbar.durvalcrm.domain.model.FormaPagamento;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "mensalidades", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"associado_id", "mes_referencia", "ano_referencia"}))
public class MensalidadeEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id")
    public UUID id;

    @Column(name = "associado_id", nullable = false)
    public UUID associadoId;

    @Column(name = "mes_referencia", nullable = false)
    public Integer mesReferencia;

    @Column(name = "ano_referencia", nullable = false)
    public Integer anoReferencia;

    @Column(name = "valor", nullable = false, precision = 10, scale = 2)
    public BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    public StatusMensalidade status;

    @Column(name = "data_vencimento", nullable = false)
    public LocalDate dataVencimento;

    @Column(name = "data_pagamento")
    public Instant dataPagamento;

    @Column(name = "qr_code_pix", length = 1000)
    public String qrCodePix;

    @Column(name = "identificador_pix", nullable = false, unique = true)
    public String identificadorPix;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    public Instant criadoEm;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pagamento")
    public FormaPagamento metodoPagamento;

    public static MensalidadeEntity fromDomain(Mensalidade mensalidade) {
        MensalidadeEntity entity = new MensalidadeEntity();
        entity.id = mensalidade.getId();
        entity.associadoId = mensalidade.getAssociadoId();
        entity.mesReferencia = mensalidade.getMesReferencia();
        entity.anoReferencia = mensalidade.getAnoReferencia();
        entity.valor = mensalidade.getValor();
        entity.status = mensalidade.getStatus();
        entity.dataVencimento = mensalidade.getDataVencimento();
        entity.dataPagamento = mensalidade.getDataPagamento();
        entity.qrCodePix = mensalidade.getQrCodePix();
        entity.identificadorPix = mensalidade.getIdentificadorPix();
        entity.criadoEm = mensalidade.getCriadoEm();
        entity.metodoPagamento = mensalidade.getMetodoPagamento();
        return entity;
    }

    public Mensalidade toDomain() {
        // Usar método fromEntity que criamos na classe Mensalidade
        return Mensalidade.fromEntity(
            this.id,
            this.associadoId, 
            this.mesReferencia, 
            this.anoReferencia, 
            this.valor,
            this.status,
            this.dataVencimento,
            this.dataPagamento,
            this.qrCodePix,
            this.identificadorPix,
            this.criadoEm,
            this.metodoPagamento
        );
    }

    public void updateFromDomain(Mensalidade mensalidade) {
        this.associadoId = mensalidade.getAssociadoId();
        this.mesReferencia = mensalidade.getMesReferencia();
        this.anoReferencia = mensalidade.getAnoReferencia();
        this.valor = mensalidade.getValor();
        this.dataVencimento = mensalidade.getDataVencimento();
        this.dataPagamento = mensalidade.getDataPagamento();
        this.status = mensalidade.getStatus();
        this.identificadorPix = mensalidade.getIdentificadorPix();
        this.qrCodePix = mensalidade.getQrCodePix();
        this.metodoPagamento = mensalidade.getMetodoPagamento();
    }
}