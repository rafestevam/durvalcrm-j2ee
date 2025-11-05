package br.org.cecairbar.durvalcrm.application.dto;

import br.org.cecairbar.durvalcrm.domain.model.FinalidadeConta;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for account balance summary (US-062)
 */
public class SaldoContaDTO {

    private UUID id;
    private String nome;
    private FinalidadeConta finalidade;
    private BigDecimal saldoAtual;
    private boolean abaixoMinimo;

    public SaldoContaDTO() {
    }

    public SaldoContaDTO(UUID id, String nome, FinalidadeConta finalidade, BigDecimal saldoAtual) {
        this.id = id;
        this.nome = nome;
        this.finalidade = finalidade;
        this.saldoAtual = saldoAtual;
        this.abaixoMinimo = false;
    }

    // Getters and Setters
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

    public FinalidadeConta getFinalidade() {
        return finalidade;
    }

    public void setFinalidade(FinalidadeConta finalidade) {
        this.finalidade = finalidade;
    }

    public BigDecimal getSaldoAtual() {
        return saldoAtual;
    }

    public void setSaldoAtual(BigDecimal saldoAtual) {
        this.saldoAtual = saldoAtual;
    }

    public boolean isAbaixoMinimo() {
        return abaixoMinimo;
    }

    public void setAbaixoMinimo(boolean abaixoMinimo) {
        this.abaixoMinimo = abaixoMinimo;
    }
}
