package br.org.cecairbar.durvalcrm.application.financeiro;

import br.org.cecairbar.durvalcrm.domain.model.FinalidadeConta;
import br.org.cecairbar.durvalcrm.domain.model.StatusConta;
import br.org.cecairbar.durvalcrm.domain.model.TipoConta;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO para transferência de dados de Conta Bancária via REST API.
 *
 * US-060: Cadastro de Contas Bancárias e Caixa
 */
@Data
public class ContaBancariaDTO {
    private UUID id;
    private String nome;
    private TipoConta tipo;
    private FinalidadeConta finalidade;
    private StatusConta status;
    private String banco;
    private String agencia;
    private String numeroConta;
    private BigDecimal saldoInicial;
    private LocalDate dataSaldoInicial;
    private BigDecimal saldoAtual;
}
