package br.org.cecairbar.durvalcrm.application.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DespesaPorCategoriaDTO {

    private UUID categoriaId;
    private String categoriaNome;
    private String categoriaCor;

    @Builder.Default
    private BigDecimal total = BigDecimal.ZERO;

    private Long quantidade;

    @Builder.Default
    private BigDecimal percentual = BigDecimal.ZERO;
}
