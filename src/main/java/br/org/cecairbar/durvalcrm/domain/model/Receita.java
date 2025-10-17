package br.org.cecairbar.durvalcrm.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class Receita {
    private UUID id;
    private String descricao;
    private BigDecimal valor;
    private LocalDate dataReceita;
    private LocalDate dataRecebimento;
    private TipoReceita tipoReceita;
    private CategoriaFinanceira categoria;
    private UUID origemId; // Referência para Mensalidade, Doacao ou Venda (opcional)
    private Associado associado; // Opcional - para rastreabilidade
    private MetodoPagamento metodoPagamento;
    private String codigoTransacao;
    private String observacoes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Receita() {
    }

    public Receita(UUID id, String descricao, BigDecimal valor, LocalDate dataReceita,
                   TipoReceita tipoReceita, CategoriaFinanceira categoria) {
        this.id = id;
        this.descricao = descricao;
        this.valor = valor;
        this.dataReceita = dataReceita;
        this.tipoReceita = tipoReceita;
        this.categoria = categoria;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDate getDataReceita() {
        return dataReceita;
    }

    public void setDataReceita(LocalDate dataReceita) {
        this.dataReceita = dataReceita;
    }

    public LocalDate getDataRecebimento() {
        return dataRecebimento;
    }

    public void setDataRecebimento(LocalDate dataRecebimento) {
        this.dataRecebimento = dataRecebimento;
    }

    public TipoReceita getTipoReceita() {
        return tipoReceita;
    }

    public void setTipoReceita(TipoReceita tipoReceita) {
        this.tipoReceita = tipoReceita;
    }

    public CategoriaFinanceira getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaFinanceira categoria) {
        this.categoria = categoria;
    }

    public UUID getOrigemId() {
        return origemId;
    }

    public void setOrigemId(UUID origemId) {
        this.origemId = origemId;
    }

    public Associado getAssociado() {
        return associado;
    }

    public void setAssociado(Associado associado) {
        this.associado = associado;
    }

    public MetodoPagamento getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(MetodoPagamento metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    public String getCodigoTransacao() {
        return codigoTransacao;
    }

    public void setCodigoTransacao(String codigoTransacao) {
        this.codigoTransacao = codigoTransacao;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
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
