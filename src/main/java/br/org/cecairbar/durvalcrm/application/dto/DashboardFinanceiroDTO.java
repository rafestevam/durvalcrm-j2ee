package br.org.cecairbar.durvalcrm.application.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for consolidated financial dashboard (US-062)
 */
public class DashboardFinanceiroDTO {

    private BigDecimal saldoTotalConsolidado;
    private List<SaldoContaDTO> contasBancarias;
    private List<SaldoContaDTO> caixasFisicos;
    private RecebimentosPeriodoDTO recebimentosPeriodo;

    public DashboardFinanceiroDTO() {
    }

    public DashboardFinanceiroDTO(BigDecimal saldoTotalConsolidado,
                                  List<SaldoContaDTO> contasBancarias,
                                  List<SaldoContaDTO> caixasFisicos,
                                  RecebimentosPeriodoDTO recebimentosPeriodo) {
        this.saldoTotalConsolidado = saldoTotalConsolidado;
        this.contasBancarias = contasBancarias;
        this.caixasFisicos = caixasFisicos;
        this.recebimentosPeriodo = recebimentosPeriodo;
    }

    // Getters and Setters
    public BigDecimal getSaldoTotalConsolidado() {
        return saldoTotalConsolidado;
    }

    public void setSaldoTotalConsolidado(BigDecimal saldoTotalConsolidado) {
        this.saldoTotalConsolidado = saldoTotalConsolidado;
    }

    public List<SaldoContaDTO> getContasBancarias() {
        return contasBancarias;
    }

    public void setContasBancarias(List<SaldoContaDTO> contasBancarias) {
        this.contasBancarias = contasBancarias;
    }

    public List<SaldoContaDTO> getCaixasFisicos() {
        return caixasFisicos;
    }

    public void setCaixasFisicos(List<SaldoContaDTO> caixasFisicos) {
        this.caixasFisicos = caixasFisicos;
    }

    public RecebimentosPeriodoDTO getRecebimentosPeriodo() {
        return recebimentosPeriodo;
    }

    public void setRecebimentosPeriodo(RecebimentosPeriodoDTO recebimentosPeriodo) {
        this.recebimentosPeriodo = recebimentosPeriodo;
    }
}
