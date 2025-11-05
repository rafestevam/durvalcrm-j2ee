package br.org.cecairbar.durvalcrm.application.financeiro;

import br.org.cecairbar.durvalcrm.domain.model.FormaPagamentoRecebimento;
import br.org.cecairbar.durvalcrm.domain.model.OrigemRecebimento;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO para transferência de dados de Recebimento via REST API.
 *
 * US-061: Registro de Recebimentos por Forma de Pagamento
 */
@Data
public class RecebimentoDTO {
    private UUID id;
    private LocalDate dataRecebimento;
    private BigDecimal valor;
    private FormaPagamentoRecebimento formaPagamento;
    private OrigemRecebimento origem;
    private UUID contaBancariaId;
    private UUID associadoId;
    private UUID vendaId;
    private UUID mensalidadeId;
    private String descricao;
    private String observacoes;
}
