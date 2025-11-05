package br.org.cecairbar.durvalcrm.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * DTO for detailed account statement (US-064)
 */
public class ExtratoContaDTO {

    private UUID contaId;
    private String nomeConta;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private BigDecimal saldoInicial;
    private BigDecimal saldoFinal;
    private BigDecimal totalEntradas;
    private BigDecimal totalSaidas;
    private List<MovimentacaoExtratoDTO> movimentacoes;

    public ExtratoContaDTO() {
    }

    public ExtratoContaDTO(UUID contaId, String nomeConta, LocalDate dataInicio, LocalDate dataFim,
                           BigDecimal saldoInicial, BigDecimal saldoFinal,
                           BigDecimal totalEntradas, BigDecimal totalSaidas,
                           List<MovimentacaoExtratoDTO> movimentacoes) {
        this.contaId = contaId;
        this.nomeConta = nomeConta;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.saldoInicial = saldoInicial;
        this.saldoFinal = saldoFinal;
        this.totalEntradas = totalEntradas;
        this.totalSaidas = totalSaidas;
        this.movimentacoes = movimentacoes;
    }

    // Getters and Setters
    public UUID getContaId() {
        return contaId;
    }

    public void setContaId(UUID contaId) {
        this.contaId = contaId;
    }

    public String getNomeConta() {
        return nomeConta;
    }

    public void setNomeConta(String nomeConta) {
        this.nomeConta = nomeConta;
    }

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

    public BigDecimal getSaldoInicial() {
        return saldoInicial;
    }

    public void setSaldoInicial(BigDecimal saldoInicial) {
        this.saldoInicial = saldoInicial;
    }

    public BigDecimal getSaldoFinal() {
        return saldoFinal;
    }

    public void setSaldoFinal(BigDecimal saldoFinal) {
        this.saldoFinal = saldoFinal;
    }

    public BigDecimal getTotalEntradas() {
        return totalEntradas;
    }

    public void setTotalEntradas(BigDecimal totalEntradas) {
        this.totalEntradas = totalEntradas;
    }

    public BigDecimal getTotalSaidas() {
        return totalSaidas;
    }

    public void setTotalSaidas(BigDecimal totalSaidas) {
        this.totalSaidas = totalSaidas;
    }

    public List<MovimentacaoExtratoDTO> getMovimentacoes() {
        return movimentacoes;
    }

    public void setMovimentacoes(List<MovimentacaoExtratoDTO> movimentacoes) {
        this.movimentacoes = movimentacoes;
    }
}
