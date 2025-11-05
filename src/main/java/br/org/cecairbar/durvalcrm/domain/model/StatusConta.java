package br.org.cecairbar.durvalcrm.domain.model;

/**
 * Enum que representa o status de uma conta bancária ou caixa.
 *
 * US-060: Cadastro de Contas Bancárias e Caixa
 */
public enum StatusConta {
    ATIVA("Ativa"),
    INATIVA("Inativa");

    private final String descricao;

    StatusConta(String descricao) {
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
