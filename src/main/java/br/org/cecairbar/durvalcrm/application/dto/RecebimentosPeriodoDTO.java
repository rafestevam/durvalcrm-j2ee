package br.org.cecairbar.durvalcrm.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for period receipts data (US-062)
 */
public class RecebimentosPeriodoDTO {

    private LocalDate dataInicio;
    private LocalDate dataFim;
    private BigDecimal totalPeriodo;
    private List<RecebimentoPorFormaPagamentoDTO> porFormaPagamento;
    private List<RecebimentoPorOrigemDTO> porOrigem;

    public RecebimentosPeriodoDTO() {
    }

    public RecebimentosPeriodoDTO(LocalDate dataInicio, LocalDate dataFim, BigDecimal totalPeriodo,
                                  List<RecebimentoPorFormaPagamentoDTO> porFormaPagamento,
                                  List<RecebimentoPorOrigemDTO> porOrigem) {
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.totalPeriodo = totalPeriodo;
        this.porFormaPagamento = porFormaPagamento;
        this.porOrigem = porOrigem;
    }

    // Getters and Setters
    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public BigDecimal getTotalPeriodo() {
        return totalPeriodo;
    }

    public void setTotalPeriodo(BigDecimal totalPeriodo) {
        this.totalPeriodo = totalPeriodo;
    }

    public List<RecebimentoPorFormaPagamentoDTO> getPorFormaPagamento() {
        return porFormaPagamento;
    }

    public void setPorFormaPagamento(List<RecebimentoPorFormaPagamentoDTO> porFormaPagamento) {
        this.porFormaPagamento = porFormaPagamento;
    }

    public List<RecebimentoPorOrigemDTO> getPorOrigem() {
        return porOrigem;
    }

    public void setPorOrigem(List<RecebimentoPorOrigemDTO> porOrigem) {
        this.porOrigem = porOrigem;
    }
}
