package br.org.cecairbar.durvalcrm.domain.model;

/**
 * Enum que representa as formas de pagamento para vendas.
 * Alinhado com FormaPagamentoRecebimento para integração automática com contas bancárias.
 *
 * US-067: Integração Automática de Vendas com Contas Bancárias
 */
public enum FormaPagamento {
    PIX("PIX"),
    CARTAO_CREDITO("Cartão de Crédito"),
    CARTAO_DEBITO("Cartão de Débito"),
    DINHEIRO("Dinheiro");

    private final String descricao;

    FormaPagamento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    /**
     * Converte FormaPagamento para FormaPagamentoRecebimento.
     * Usado na integração automática de vendas com recebimentos.
     */
    public FormaPagamentoRecebimento toFormaPagamentoRecebimento() {
        return switch (this) {
            case PIX -> FormaPagamentoRecebimento.PIX;
            case CARTAO_CREDITO -> FormaPagamentoRecebimento.CARTAO_CREDITO;
            case CARTAO_DEBITO -> FormaPagamentoRecebimento.CARTAO_DEBITO;
            case DINHEIRO -> FormaPagamentoRecebimento.DINHEIRO;
        };
    }

    /**
     * Mapeia FormaPagamento para FinalidadeConta correspondente.
     * Usado para buscar automaticamente a conta bancária correta.
     */
    public FinalidadeConta toFinalidadeConta() {
        return switch (this) {
            case PIX -> FinalidadeConta.PIX;
            case CARTAO_CREDITO -> FinalidadeConta.CARTAO_CREDITO;
            case CARTAO_DEBITO -> FinalidadeConta.CARTAO_DEBITO;
            case DINHEIRO -> FinalidadeConta.DINHEIRO_DEPOSITOS;
        };
    }
}