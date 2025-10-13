package br.org.cecairbar.durvalcrm.test.util;

import br.org.cecairbar.durvalcrm.domain.model.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Builder class for creating test data objects.
 * Provides fluent API for building domain entities with sensible defaults.
 *
 * Usage:
 * <pre>
 * Associado associado = TestDataBuilder.umAssociado()
 *     .comNome("João Silva")
 *     .comCpf("123.456.789-00")
 *     .ativo()
 *     .build();
 * </pre>
 */
public class TestDataBuilder {

    // ========== ASSOCIADO BUILDER ==========

    public static AssociadoBuilder umAssociado() {
        return new AssociadoBuilder();
    }

    public static class AssociadoBuilder {
        private UUID id = UUID.randomUUID();
        private String nomeCompleto = "João da Silva";
        private String cpf = "123.456.789-00";
        private String email = "joao@example.com";
        private String telefone = "(11) 98765-4321";
        private boolean ativo = true;

        public AssociadoBuilder comId(UUID id) {
            this.id = id;
            return this;
        }

        public AssociadoBuilder comNome(String nomeCompleto) {
            this.nomeCompleto = nomeCompleto;
            return this;
        }

        public AssociadoBuilder comCpf(String cpf) {
            this.cpf = cpf;
            return this;
        }

        public AssociadoBuilder comEmail(String email) {
            this.email = email;
            return this;
        }

        public AssociadoBuilder comTelefone(String telefone) {
            this.telefone = telefone;
            return this;
        }

        public AssociadoBuilder ativo() {
            this.ativo = true;
            return this;
        }

        public AssociadoBuilder inativo() {
            this.ativo = false;
            return this;
        }

        public Associado build() {
            return new Associado(id, nomeCompleto, cpf, email, telefone, ativo);
        }
    }

    // ========== MENSALIDADE BUILDER ==========

    public static MensalidadeBuilder umaMensalidade() {
        return new MensalidadeBuilder();
    }

    public static class MensalidadeBuilder {
        private UUID associadoId = UUID.randomUUID();
        private Integer mesReferencia = LocalDate.now().getMonthValue();
        private Integer anoReferencia = LocalDate.now().getYear();
        private BigDecimal valor = new BigDecimal("10.90");

        public MensalidadeBuilder paraAssociado(UUID associadoId) {
            this.associadoId = associadoId;
            return this;
        }

        public MensalidadeBuilder referencia(Integer mes, Integer ano) {
            this.mesReferencia = mes;
            this.anoReferencia = ano;
            return this;
        }

        public MensalidadeBuilder comValor(BigDecimal valor) {
            this.valor = valor;
            return this;
        }

        public Mensalidade build() {
            return Mensalidade.criar(associadoId, mesReferencia, anoReferencia, valor);
        }
    }

    // ========== DOACAO BUILDER ==========

    public static DoacaoBuilder umaDoacao() {
        return new DoacaoBuilder();
    }

    public static class DoacaoBuilder {
        private UUID id = UUID.randomUUID();
        private Associado associado = umAssociado().build();
        private BigDecimal valor = new BigDecimal("50.00");
        private TipoDoacao tipoDoacao = TipoDoacao.UNICA;
        private StatusDoacao status = StatusDoacao.CONFIRMADA;
        private String descricao = "Doação de teste";
        private LocalDateTime dataDoacao = LocalDateTime.now();

        public DoacaoBuilder comId(UUID id) {
            this.id = id;
            return this;
        }

        public DoacaoBuilder deAssociado(Associado associado) {
            this.associado = associado;
            return this;
        }

        public DoacaoBuilder comTipo(TipoDoacao tipoDoacao) {
            this.tipoDoacao = tipoDoacao;
            return this;
        }

        public DoacaoBuilder comValor(BigDecimal valor) {
            this.valor = valor;
            return this;
        }

        public DoacaoBuilder emData(LocalDateTime dataDoacao) {
            this.dataDoacao = dataDoacao;
            return this;
        }

        public DoacaoBuilder comStatus(StatusDoacao status) {
            this.status = status;
            return this;
        }

        public DoacaoBuilder confirmada() {
            this.status = StatusDoacao.CONFIRMADA;
            return this;
        }

        public DoacaoBuilder pendente() {
            this.status = StatusDoacao.PENDENTE;
            return this;
        }

        public DoacaoBuilder cancelada() {
            this.status = StatusDoacao.CANCELADA;
            return this;
        }

        public DoacaoBuilder comDescricao(String descricao) {
            this.descricao = descricao;
            return this;
        }

        public Doacao build() {
            return new Doacao(id, associado, valor, tipoDoacao, status, descricao, dataDoacao);
        }
    }

    // ========== VENDA BUILDER ==========

    public static VendaBuilder umaVenda() {
        return new VendaBuilder();
    }

    public static class VendaBuilder {
        private String descricao = "Venda de produtos";
        private BigDecimal valor = new BigDecimal("25.00");
        private OrigemVenda origemVenda = OrigemVenda.CANTINA;
        private FormaPagamento formaPagamento = FormaPagamento.DINHEIRO;

        public VendaBuilder comOrigem(OrigemVenda origemVenda) {
            this.origemVenda = origemVenda;
            return this;
        }

        public VendaBuilder comDescricao(String descricao) {
            this.descricao = descricao;
            return this;
        }

        public VendaBuilder comValor(BigDecimal valor) {
            this.valor = valor;
            return this;
        }

        public VendaBuilder comFormaPagamento(FormaPagamento formaPagamento) {
            this.formaPagamento = formaPagamento;
            return this;
        }

        public Venda build() {
            return Venda.criar(descricao, valor, origemVenda, formaPagamento);
        }
    }
}
