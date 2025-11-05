package br.org.cecairbar.durvalcrm.domain.model;

/**
 * Enum que representa as origens de recebimento no sistema.
 *
 * US-061: Registro de Recebimentos por Forma de Pagamento
 */
public enum OrigemRecebimento {
    MENSALIDADE("Mensalidade de Associado"),
    VENDA_PRODUTOS("Venda de Produtos"),
    TRANSFERENCIA_BAZAR("Transferência de Bazar"),
    VENDA_CANTINA("Venda de Cantina"),
    OUTROS("Outros");

    private final String descricao;

    OrigemRecebimento(String descricao) {
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
