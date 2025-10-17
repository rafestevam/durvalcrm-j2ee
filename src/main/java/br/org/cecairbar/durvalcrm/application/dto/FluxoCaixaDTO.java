package br.org.cecairbar.durvalcrm.application.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FluxoCaixaDTO {

    private LocalDate periodoInicio;
    private LocalDate periodoFim;

    private List<MovimentacaoMensalDTO> movimentacoes;

    @Builder.Default
    private BigDecimal totalReceitasPeriodo = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal totalDespesasPeriodo = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal saldoPeriodo = BigDecimal.ZERO;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MovimentacaoMensalDTO {
        private YearMonth mesAno; // Ex: 2025-01
        private String mesAnoFormatado; // Ex: "Janeiro/2025"

        @Builder.Default
        private BigDecimal receitas = BigDecimal.ZERO;

        @Builder.Default
        private BigDecimal despesas = BigDecimal.ZERO;

        @Builder.Default
        private BigDecimal saldo = BigDecimal.ZERO;

        @Builder.Default
        private BigDecimal saldoAcumulado = BigDecimal.ZERO;

        // Método auxiliar para calcular o saldo
        public void calcularSaldo() {
            this.saldo = this.receitas.subtract(this.despesas);
        }
    }
}
