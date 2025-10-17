package br.org.cecairbar.durvalcrm.application.dto;

import br.org.cecairbar.durvalcrm.domain.model.TipoDespesa;
import br.org.cecairbar.durvalcrm.domain.model.StatusPagamentoDespesa;
import br.org.cecairbar.durvalcrm.domain.model.FormaPagamento;
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
public class DespesaDTO {

    private UUID id;

    @NotEmpty(message = "Descrição é obrigatória")
    @Size(max = 255, message = "Descrição deve ter no máximo 255 caracteres")
    private String descricao;

    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal valor;

    @NotNull(message = "Data da despesa é obrigatória")
    private LocalDate dataDespesa;

    @NotNull(message = "Data de vencimento é obrigatória")
    private LocalDate dataVencimento;

    private LocalDate dataPagamento;

    @NotNull(message = "Tipo de despesa é obrigatório")
    private TipoDespesa tipoDespesa;

    @NotNull(message = "Categoria é obrigatória")
    private UUID categoriaId;

    private String categoriaNome; // Para exibição

    @NotNull(message = "Status de pagamento é obrigatório")
    @Builder.Default
    private StatusPagamentoDespesa statusPagamento = StatusPagamentoDespesa.PENDENTE;

    private FormaPagamento formaPagamento;

    @Size(max = 200, message = "Nome do fornecedor deve ter no máximo 200 caracteres")
    private String fornecedor;

    @Size(max = 100, message = "Número do documento deve ter no máximo 100 caracteres")
    private String numeroDocumento;

    @Size(max = 100, message = "Código da transação deve ter no máximo 100 caracteres")
    private String codigoTransacao;

    @Size(max = 1000, message = "Observações devem ter no máximo 1000 caracteres")
    private String observacoes;

    private Instant criadoEm;
    private Instant atualizadoEm;

    // Factory method para criar nova despesa
    public static DespesaDTO criar(String descricao, BigDecimal valor, LocalDate dataDespesa,
                                    LocalDate dataVencimento, TipoDespesa tipoDespesa,
                                    UUID categoriaId) {
        return DespesaDTO.builder()
                .descricao(descricao)
                .valor(valor)
                .dataDespesa(dataDespesa)
                .dataVencimento(dataVencimento)
                .tipoDespesa(tipoDespesa)
                .categoriaId(categoriaId)
                .statusPagamento(StatusPagamentoDespesa.PENDENTE)
                .build();
    }

    // Factory method para marcar despesa como paga
    public void marcarComoPaga(LocalDate dataPagamento, FormaPagamento formaPagamento) {
        this.dataPagamento = dataPagamento;
        this.formaPagamento = formaPagamento;
        this.statusPagamento = StatusPagamentoDespesa.PAGO;
    }
}
