package br.org.cecairbar.durvalcrm.infrastructure.persistence.entity;

import br.org.cecairbar.durvalcrm.domain.model.FinalidadeConta;
import br.org.cecairbar.durvalcrm.domain.model.StatusConta;
import br.org.cecairbar.durvalcrm.domain.model.TipoConta;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade JPA para persistência de contas bancárias e caixa físico.
 *
 * US-060: Cadastro de Contas Bancárias e Caixa
 */
@Entity
@Table(name = "contas_bancarias", uniqueConstraints = {
    @UniqueConstraint(columnNames = "nome")
})
@Data
public class ContaBancariaEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @org.hibernate.annotations.GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    public UUID id;

    @Column(nullable = false, unique = true, length = 100)
    public String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    public TipoConta tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    public FinalidadeConta finalidade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    public StatusConta status;

    // Dados bancários (opcionais para caixa físico)
    @Column(length = 100)
    public String banco;

    @Column(length = 10)
    public String agencia;

    @Column(name = "numero_conta", length = 20)
    public String numeroConta;

    // Saldo inicial
    @Column(name = "saldo_inicial", nullable = false, precision = 10, scale = 2)
    public BigDecimal saldoInicial;

    @Column(name = "data_saldo_inicial", nullable = false)
    public LocalDate dataSaldoInicial;

    // Saldo atual (calculado)
    @Column(name = "saldo_atual", nullable = false, precision = 10, scale = 2)
    public BigDecimal saldoAtual;

    // Auditoria
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    public LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    public LocalDateTime updatedAt;
}
