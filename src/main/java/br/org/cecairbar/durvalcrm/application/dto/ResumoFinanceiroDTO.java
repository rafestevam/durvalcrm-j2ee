package br.org.cecairbar.durvalcrm.application.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumoFinanceiroDTO {

    private LocalDate periodoInicio;
    private LocalDate periodoFim;

    @Builder.Default
    private BigDecimal totalReceitas = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal totalDespesas = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal saldo = BigDecimal.ZERO;

    private Long quantidadeReceitas;
    private Long quantidadeDespesas;
    private Long quantidadeDespesasPendentes;
    private Long quantidadeDespesasVencidas;

    @Builder.Default
    private BigDecimal totalDespesasPendentes = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal totalDespesasVencidas = BigDecimal.ZERO;

    // Método auxiliar para calcular o saldo
    public void calcularSaldo() {
        this.saldo = this.totalReceitas.subtract(this.totalDespesas);
    }

    // Factory method
    public static ResumoFinanceiroDTO criar(LocalDate periodoInicio, LocalDate periodoFim) {
        ResumoFinanceiroDTO resumo = ResumoFinanceiroDTO.builder()
                .periodoInicio(periodoInicio)
                .periodoFim(periodoFim)
                .build();
        resumo.calcularSaldo();
        return resumo;
    }
}
