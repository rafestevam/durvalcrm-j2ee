package br.org.cecairbar.durvalcrm.domain.model;

/**
 * Enum que representa os tipos de conta disponíveis no sistema.
 *
 * US-060: Cadastro de Contas Bancárias e Caixa
 */
public enum TipoConta {
    BANCARIA("Conta Bancária"),
    CAIXA_FISICO("Caixa Físico");

    private final String descricao;

    TipoConta(String descricao) {
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
