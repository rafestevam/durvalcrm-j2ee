package br.org.cecairbar.durvalcrm.test.util;

import br.org.cecairbar.durvalcrm.domain.model.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Custom assertions for domain entities.
 * Provides convenient methods for asserting properties of domain objects.
 *
 * Usage:
 * <pre>
 * TestAssertions.assertAssociado(associado)
 *     .hasNome("João Silva")
 *     .hasCpf("123.456.789-00")
 *     .isAtivo();
 * </pre>
 */
public class TestAssertions {

    // ========== ASSOCIADO ASSERTIONS ==========

    public static AssociadoAssertion assertAssociado(Associado associado) {
        return new AssociadoAssertion(associado);
    }

    public static class AssociadoAssertion {
        private final Associado associado;

        private AssociadoAssertion(Associado associado) {
            assertNotNull("Associado should not be null", associado);
            this.associado = associado;
        }

        public AssociadoAssertion hasId(UUID expectedId) {
            assertEquals("Associado ID mismatch", expectedId, associado.getId());
            return this;
        }

        public AssociadoAssertion hasNome(String expectedNome) {
            assertEquals("Nome mismatch", expectedNome, associado.getNomeCompleto());
            return this;
        }

        public AssociadoAssertion hasCpf(String expectedCpf) {
            assertEquals("CPF mismatch", expectedCpf, associado.getCpf());
            return this;
        }

        public AssociadoAssertion hasEmail(String expectedEmail) {
            assertEquals("Email mismatch", expectedEmail, associado.getEmail());
            return this;
        }

        public AssociadoAssertion hasTelefone(String expectedTelefone) {
            assertEquals("Telefone mismatch", expectedTelefone, associado.getTelefone());
            return this;
        }

        public AssociadoAssertion isAtivo() {
            assertTrue("Associado should be ativo", associado.isAtivo());
            return this;
        }

        public AssociadoAssertion isInativo() {
            assertFalse("Associado should be inativo", associado.isAtivo());
            return this;
        }
    }

    // ========== MENSALIDADE ASSERTIONS ==========

    public static MensalidadeAssertion assertMensalidade(Mensalidade mensalidade) {
        return new MensalidadeAssertion(mensalidade);
    }

    public static class MensalidadeAssertion {
        private final Mensalidade mensalidade;

        private MensalidadeAssertion(Mensalidade mensalidade) {
            assertNotNull("Mensalidade should not be null", mensalidade);
            this.mensalidade = mensalidade;
        }

        public MensalidadeAssertion hasId(UUID expectedId) {
            assertEquals("Mensalidade ID mismatch", expectedId, mensalidade.getId());
            return this;
        }

        public MensalidadeAssertion pertenceAoAssociado(UUID expectedAssociadoId) {
            assertEquals("Associado ID mismatch", expectedAssociadoId, mensalidade.getAssociadoId());
            return this;
        }

        public MensalidadeAssertion hasReferencia(int mes, int ano) {
            assertEquals("Mês de referência mismatch", mes, mensalidade.getMesReferencia());
            assertEquals("Ano de referência mismatch", ano, mensalidade.getAnoReferencia());
            return this;
        }

        public MensalidadeAssertion hasValor(BigDecimal expectedValor) {
            assertEquals("Valor mismatch", 0, expectedValor.compareTo(mensalidade.getValor()));
            return this;
        }

        public MensalidadeAssertion hasVencimento(LocalDate expectedVencimento) {
            assertEquals("Data de vencimento mismatch", expectedVencimento, mensalidade.getDataVencimento());
            return this;
        }

        public MensalidadeAssertion hasStatus(StatusMensalidade expectedStatus) {
            assertEquals("Status mismatch", expectedStatus, mensalidade.getStatus());
            return this;
        }

        public MensalidadeAssertion isPendente() {
            return hasStatus(StatusMensalidade.PENDENTE);
        }

        public MensalidadeAssertion isPaga() {
            return hasStatus(StatusMensalidade.PAGA);
        }

        public MensalidadeAssertion isAtrasada() {
            return hasStatus(StatusMensalidade.ATRASADA);
        }

        public MensalidadeAssertion hasPagamento(FormaPagamento metodo, Instant data) {
            assertEquals("Método de pagamento mismatch", metodo, mensalidade.getMetodoPagamento());
            assertEquals("Data de pagamento mismatch", data, mensalidade.getDataPagamento());
            return this;
        }

        public MensalidadeAssertion naoTemPagamento() {
            assertNull("Método de pagamento should be null", mensalidade.getMetodoPagamento());
            assertNull("Data de pagamento should be null", mensalidade.getDataPagamento());
            return this;
        }
    }

    // ========== DOACAO ASSERTIONS ==========

    public static DoacaoAssertion assertDoacao(Doacao doacao) {
        return new DoacaoAssertion(doacao);
    }

    public static class DoacaoAssertion {
        private final Doacao doacao;

        private DoacaoAssertion(Doacao doacao) {
            assertNotNull("Doação should not be null", doacao);
            this.doacao = doacao;
        }

        public DoacaoAssertion hasId(UUID expectedId) {
            assertEquals("Doação ID mismatch", expectedId, doacao.getId());
            return this;
        }

        public DoacaoAssertion deAssociado(Associado expectedAssociado) {
            assertNotNull("Associado should not be null", doacao.getAssociado());
            assertEquals("Associado ID mismatch", expectedAssociado.getId(), doacao.getAssociado().getId());
            return this;
        }

        public DoacaoAssertion hasTipo(TipoDoacao expectedTipo) {
            assertEquals("Tipo de doação mismatch", expectedTipo, doacao.getTipo());
            return this;
        }

        public DoacaoAssertion hasValor(BigDecimal expectedValor) {
            assertEquals("Valor mismatch", 0, expectedValor.compareTo(doacao.getValor()));
            return this;
        }

        public DoacaoAssertion hasStatus(StatusDoacao expectedStatus) {
            assertEquals("Status mismatch", expectedStatus, doacao.getStatus());
            return this;
        }

        public DoacaoAssertion isConfirmada() {
            return hasStatus(StatusDoacao.CONFIRMADA);
        }

        public DoacaoAssertion isPendente() {
            return hasStatus(StatusDoacao.PENDENTE);
        }

        public DoacaoAssertion isCancelada() {
            return hasStatus(StatusDoacao.CANCELADA);
        }
    }

    // ========== VENDA ASSERTIONS ==========

    public static VendaAssertion assertVenda(Venda venda) {
        return new VendaAssertion(venda);
    }

    public static class VendaAssertion {
        private final Venda venda;

        private VendaAssertion(Venda venda) {
            assertNotNull("Venda should not be null", venda);
            this.venda = venda;
        }

        public VendaAssertion hasId(UUID expectedId) {
            assertEquals("Venda ID mismatch", expectedId, venda.getId());
            return this;
        }

        public VendaAssertion hasOrigem(OrigemVenda expectedOrigem) {
            assertEquals("Origem da venda mismatch", expectedOrigem, venda.getOrigem());
            return this;
        }

        public VendaAssertion hasDescricao(String expectedDescricao) {
            assertEquals("Descrição mismatch", expectedDescricao, venda.getDescricao());
            return this;
        }

        public VendaAssertion hasValor(BigDecimal expectedValor) {
            assertEquals("Valor mismatch", 0, expectedValor.compareTo(venda.getValor()));
            return this;
        }

        public VendaAssertion hasFormaPagamento(FormaPagamento expectedFormaPagamento) {
            assertEquals("Forma de pagamento mismatch", expectedFormaPagamento, venda.getFormaPagamento());
            return this;
        }
    }
}
