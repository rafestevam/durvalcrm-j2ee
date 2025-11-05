package br.org.cecairbar.durvalcrm.application.dto;

import br.org.cecairbar.durvalcrm.domain.model.FormaPagamentoRecebimento;
import java.math.BigDecimal;

/**
 * DTO for receipts grouped by payment method (US-062)
 */
public class RecebimentoPorFormaPagamentoDTO {

    private FormaPagamentoRecebimento formaPagamento;
    private BigDecimal total;
    private Long quantidade;
    private BigDecimal percentual;

    public RecebimentoPorFormaPagamentoDTO() {
    }

    public RecebimentoPorFormaPagamentoDTO(FormaPagamentoRecebimento formaPagamento, BigDecimal total, Long quantidade) {
        this.formaPagamento = formaPagamento;
        this.total = total;
        this.quantidade = quantidade;
    }

    // Getters and Setters
    public FormaPagamentoRecebimento getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(FormaPagamentoRecebimento formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Long getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Long quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getPercentual() {
        return percentual;
    }

    public void setPercentual(BigDecimal percentual) {
        this.percentual = percentual;
    }
}
