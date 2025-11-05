package br.org.cecairbar.durvalcrm.application.dto;

import br.org.cecairbar.durvalcrm.domain.model.FormaPagamentoRecebimento;
import br.org.cecairbar.durvalcrm.domain.model.OrigemRecebimento;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for statement transaction line (US-064)
 */
public class MovimentacaoExtratoDTO {

    private UUID id;
    private LocalDate data;
    private LocalDateTime dataHora;
    private TipoMovimentacao tipo;
    private FormaPagamentoRecebimento formaPagamento;
    private OrigemRecebimento origem;
    private String descricao;
    private BigDecimal valor;
    private BigDecimal saldoApos;
    private UUID vinculoId; // ID of linked mensalidade/venda if applicable
    private String vinculoTipo; // MENSALIDADE, VENDA, TRANSFERENCIA, etc.

    public enum TipoMovimentacao {
        ENTRADA,
        SAIDA,
        TRANSFERENCIA_ENTRADA,
        TRANSFERENCIA_SAIDA
    }

    public MovimentacaoExtratoDTO() {
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public TipoMovimentacao getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimentacao tipo) {
        this.tipo = tipo;
    }

    public FormaPagamentoRecebimento getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(FormaPagamentoRecebimento formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public OrigemRecebimento getOrigem() {
        return origem;
    }

    public void setOrigem(OrigemRecebimento origem) {
        this.origem = origem;
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

    public BigDecimal getSaldoApos() {
        return saldoApos;
    }

    public void setSaldoApos(BigDecimal saldoApos) {
        this.saldoApos = saldoApos;
    }

    public UUID getVinculoId() {
        return vinculoId;
    }

    public void setVinculoId(UUID vinculoId) {
        this.vinculoId = vinculoId;
    }

    public String getVinculoTipo() {
        return vinculoTipo;
    }

    public void setVinculoTipo(String vinculoTipo) {
        this.vinculoTipo = vinculoTipo;
    }
}
