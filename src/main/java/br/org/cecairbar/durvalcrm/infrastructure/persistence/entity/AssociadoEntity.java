package br.org.cecairbar.durvalcrm.infrastructure.persistence.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "associados")
@Data
public class AssociadoEntity {
    
    @Id
    @GeneratedValue(generator = "UUID")
    @org.hibernate.annotations.GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    public UUID id;

    @Column(name = "nome_completo", nullable = false)
    public String nomeCompleto;

    @Column(unique = true, nullable = false, length = 14)
    public String cpf;

    @Column(unique = true, nullable = false)
    public String email;

    @Column(length = 20)
    public String telefone;

    @Column(nullable = false)
    public boolean ativo = true;

    @CreationTimestamp
    @Column(name = "criado_em", updatable = false)
    public Instant criadoEm;
}