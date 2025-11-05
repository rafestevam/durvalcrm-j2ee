package br.org.cecairbar.durvalcrm.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade de domínio que representa uma conta bancária ou caixa físico.
 *
 * US-060: Cadastro de Contas Bancárias e Caixa
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContaBancaria {
    private UUID id;
    private String nome;
    private TipoConta tipo;
    private FinalidadeConta finalidade;
    private StatusConta status;

    // Dados bancários (opcionais para caixa físico)
    private String banco;
    private String agencia;
    private String numeroConta;

    // Saldo inicial
    private BigDecimal saldoInicial;
    private LocalDate dataSaldoInicial;

    // Saldo atual (calculado)
    private BigDecimal saldoAtual;

    // Auditoria
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
