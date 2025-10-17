package br.org.cecairbar.durvalcrm.infrastructure.persistence.entity;

import br.org.cecairbar.durvalcrm.domain.model.TipoCategoriaFinanceira;
import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "categorias_financeiras",
       indexes = {
           @Index(name = "idx_categoria_nome", columnList = "nome"),
           @Index(name = "idx_categoria_tipo", columnList = "tipo"),
           @Index(name = "idx_categoria_ativa", columnList = "ativa")
       },
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_categoria_nome", columnNames = {"nome"})
       })
public class CategoriaFinanceiraEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    public UUID id;

    @NotNull
    @Column(nullable = false, length = 100)
    public String nome;

    @Column(length = 500)
    public String descricao;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    public TipoCategoriaFinanceira tipo;

    @NotNull
    @Column(nullable = false)
    public Boolean ativa;

    @Column(length = 7) // #RRGGBB
    public String cor;

    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt;

    @Column(name = "updated_at")
    public LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (ativa == null) {
            ativa = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
