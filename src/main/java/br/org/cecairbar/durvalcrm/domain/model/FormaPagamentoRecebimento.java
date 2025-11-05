package br.org.cecairbar.durvalcrm.domain.model;

/**
 * Enum que representa as formas de pagamento para recebimentos.
 *
 * US-061: Registro de Recebimentos por Forma de Pagamento
 */
public enum FormaPagamentoRecebimento {
    PIX("PIX"),
    CARTAO_CREDITO("Cartão de Crédito"),
    CARTAO_DEBITO("Cartão de Débito"),
    DINHEIRO("Dinheiro");

    private final String descricao;

    FormaPagamentoRecebimento(String descricao) {
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
