package br.org.cecairbar.durvalcrm.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade de domínio que representa um recebimento financeiro.
 *
 * US-061: Registro de Recebimentos por Forma de Pagamento
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recebimento {
    private UUID id;
    private LocalDate dataRecebimento;
    private BigDecimal valor;
    private FormaPagamentoRecebimento formaPagamento;
    private OrigemRecebimento origem;

    // Conta de destino
    private UUID contaBancariaId;

    // Vinculações opcionais
    private UUID associadoId; // Se origem = MENSALIDADE
    private UUID vendaId; // Se origem = VENDA_PRODUTOS ou VENDA_CANTINA
    private UUID mensalidadeId; // Se origem = MENSALIDADE

    // Informações adicionais
    private String descricao;
    private String observacoes;

    // Auditoria
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
