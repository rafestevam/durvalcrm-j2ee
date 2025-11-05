package br.org.cecairbar.durvalcrm.application.dto;

import br.org.cecairbar.durvalcrm.domain.model.OrigemRecebimento;
import java.math.BigDecimal;

/**
 * DTO for receipts grouped by origin (US-062)
 */
public class RecebimentoPorOrigemDTO {

    private OrigemRecebimento origem;
    private BigDecimal total;
    private Long quantidade;
    private BigDecimal percentual;

    public RecebimentoPorOrigemDTO() {
    }

    public RecebimentoPorOrigemDTO(OrigemRecebimento origem, BigDecimal total, Long quantidade) {
        this.origem = origem;
        this.total = total;
        this.quantidade = quantidade;
    }

    // Getters and Setters
    public OrigemRecebimento getOrigem() {
        return origem;
    }

    public void setOrigem(OrigemRecebimento origem) {
        this.origem = origem;
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
