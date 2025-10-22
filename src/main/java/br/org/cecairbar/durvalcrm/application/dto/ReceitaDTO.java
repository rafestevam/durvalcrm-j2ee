package br.org.cecairbar.durvalcrm.application.dto;

import br.org.cecairbar.durvalcrm.domain.model.TipoReceita;
import br.org.cecairbar.durvalcrm.domain.model.MetodoPagamento;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceitaDTO {

    private UUID id;

    @NotEmpty(message = "Descrição é obrigatória")
    @Size(max = 255, message = "Descrição deve ter no máximo 255 caracteres")
    private String descricao;

    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal valor;

    @NotNull(message = "Data da receita é obrigatória")
    private LocalDate dataReceita;

    private LocalDate dataRecebimento;

    @NotNull(message = "Tipo de receita é obrigatório")
    private TipoReceita tipoReceita;

    @NotNull(message = "Categoria é obrigatória")
    private UUID categoriaId;

    private String categoriaNome; // Para exibição

    private UUID origemId; // Referência opcional para Mensalidade, Doacao ou Venda

    private UUID associadoId; // Opcional - para rastreabilidade

    private String associadoNome; // Para exibição

    private MetodoPagamento metodoPagamento;

    @Size(max = 100, message = "Código da transação deve ter no máximo 100 caracteres")
    private String codigoTransacao;

    @Size(max = 1000, message = "Observações devem ter no máximo 1000 caracteres")
    private String observacoes;

    private Instant criadoEm;
    private Instant atualizadoEm;

    // Factory method para criar nova receita
    public static ReceitaDTO criar(String descricao, BigDecimal valor, LocalDate dataReceita,
                                    TipoReceita tipoReceita, UUID categoriaId) {
        return ReceitaDTO.builder()
                .descricao(descricao)
                .valor(valor)
                .dataReceita(dataReceita)
                .tipoReceita(tipoReceita)
                .categoriaId(categoriaId)
                .build();
    }
}
