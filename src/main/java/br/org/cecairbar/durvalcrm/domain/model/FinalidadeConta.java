package br.org.cecairbar.durvalcrm.domain.model;

/**
 * Enum que representa as finalidades de uma conta bancária.
 *
 * US-060: Cadastro de Contas Bancárias e Caixa
 */
public enum FinalidadeConta {
    PIX("Recebimentos via PIX"),
    CARTAO_CREDITO("Recebimentos via Cartão de Crédito"),
    CARTAO_DEBITO("Recebimentos via Cartão de Débito"),
    DINHEIRO_DEPOSITOS("Depósitos em Dinheiro"),
    OPERACIONAL("Conta Operacional"),
    OUTROS("Outros");

    private final String descricao;

    FinalidadeConta(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
