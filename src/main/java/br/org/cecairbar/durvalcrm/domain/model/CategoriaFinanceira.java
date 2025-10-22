package br.org.cecairbar.durvalcrm.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class CategoriaFinanceira {
    private UUID id;
    private String nome;
    private String descricao;
    private TipoCategoriaFinanceira tipo;
    private Boolean ativa;
    private String cor; // Código hexadecimal para UI (ex: "#FF5733")
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CategoriaFinanceira() {
    }

    public CategoriaFinanceira(UUID id, String nome, String descricao,
                               TipoCategoriaFinanceira tipo, Boolean ativa) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.tipo = tipo;
        this.ativa = ativa;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public TipoCategoriaFinanceira getTipo() {
        return tipo;
    }

    public void setTipo(TipoCategoriaFinanceira tipo) {
        this.tipo = tipo;
    }

    public Boolean getAtiva() {
        return ativa;
    }

    public void setAtiva(Boolean ativa) {
        this.ativa = ativa;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
