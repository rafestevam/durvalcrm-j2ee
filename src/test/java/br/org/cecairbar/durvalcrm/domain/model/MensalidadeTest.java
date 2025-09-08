package br.org.cecairbar.durvalcrm.domain.model;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.Assert.*;

public class MensalidadeTest {

    private UUID associadoId;
    private BigDecimal valor;

    @Before
    public void setUp() {
        associadoId = UUID.randomUUID();
        valor = new BigDecimal("10.90");
    }

    @Test
    public void testCriarMensalidade() {
        int mes = 3;
        int ano = 2024;
        
        Mensalidade mensalidade = Mensalidade.criar(associadoId, mes, ano, valor);

        assertNotNull(mensalidade);
        assertNotNull(mensalidade.getId());
        assertEquals(associadoId, mensalidade.getAssociadoId());
        assertEquals(mes, mensalidade.getMesReferencia());
        assertEquals(ano, mensalidade.getAnoReferencia());
        assertEquals(valor, mensalidade.getValor());
        assertEquals(StatusMensalidade.PENDENTE, mensalidade.getStatus());
        assertEquals(LocalDate.of(2024, 3, 10), mensalidade.getDataVencimento());
        assertNotNull(mensalidade.getIdentificadorPix());
        assertNotNull(mensalidade.getCriadoEm());
        assertNull(mensalidade.getDataPagamento());
    }

    @Test
    public void testIdentificadorPixFormat() {
        int mes = 12;
        int ano = 2024;
        
        Mensalidade mensalidade = Mensalidade.criar(associadoId, mes, ano, valor);
        
        String identificador = mensalidade.getIdentificadorPix();
        assertNotNull(identificador);
        assertTrue(identificador.startsWith("MENS"));
        assertTrue(identificador.contains("12"));
        assertTrue(identificador.contains("2024"));
    }

    @Test
    public void testFromEntity() {
        UUID id = UUID.randomUUID();
        int mesReferencia = 5;
        int anoReferencia = 2024;
        StatusMensalidade status = StatusMensalidade.PAGA;
        LocalDate dataVencimento = LocalDate.of(2024, 5, 10);
        Instant dataPagamento = Instant.now();
        String qrCodePix = "QR_CODE_123";
        String identificadorPix = "MENS12345678052024";
        Instant criadoEm = Instant.now().minus(10, ChronoUnit.DAYS);
        FormaPagamento metodoPagamento = FormaPagamento.PIX;

        Mensalidade mensalidade = Mensalidade.fromEntity(
            id, associadoId, mesReferencia, anoReferencia,
            valor, status, dataVencimento, dataPagamento,
            qrCodePix, identificadorPix, criadoEm, metodoPagamento
        );

        assertEquals(id, mensalidade.getId());
        assertEquals(associadoId, mensalidade.getAssociadoId());
        assertEquals(mesReferencia, mensalidade.getMesReferencia());
        assertEquals(anoReferencia, mensalidade.getAnoReferencia());
        assertEquals(valor, mensalidade.getValor());
        assertEquals(status, mensalidade.getStatus());
        assertEquals(dataVencimento, mensalidade.getDataVencimento());
        assertEquals(dataPagamento, mensalidade.getDataPagamento());
        assertEquals(qrCodePix, mensalidade.getQrCodePix());
        assertEquals(identificadorPix, mensalidade.getIdentificadorPix());
        assertEquals(criadoEm, mensalidade.getCriadoEm());
        assertEquals(metodoPagamento, mensalidade.getMetodoPagamento());
    }

    @Test
    public void testMarcarComoPaga() {
        Mensalidade mensalidade = Mensalidade.criar(associadoId, 4, 2024, valor);
        Instant dataPagamento = Instant.now();

        assertEquals(StatusMensalidade.PENDENTE, mensalidade.getStatus());
        assertNull(mensalidade.getDataPagamento());

        mensalidade.marcarComoPaga(dataPagamento);

        assertEquals(StatusMensalidade.PAGA, mensalidade.getStatus());
        assertEquals(dataPagamento, mensalidade.getDataPagamento());
    }

    @Test
    public void testMarcarComoPagaComMetodo() {
        Mensalidade mensalidade = Mensalidade.criar(associadoId, 6, 2024, valor);
        Instant dataPagamento = Instant.now();
        FormaPagamento metodoPagamento = FormaPagamento.DINHEIRO;

        mensalidade.marcarComoPaga(dataPagamento, metodoPagamento);

        assertEquals(StatusMensalidade.PAGA, mensalidade.getStatus());
        assertEquals(dataPagamento, mensalidade.getDataPagamento());
        assertEquals(metodoPagamento, mensalidade.getMetodoPagamento());
    }

    @Test(expected = IllegalStateException.class)
    public void testMarcarComoPagaQuandoJaPaga() {
        Mensalidade mensalidade = Mensalidade.criar(associadoId, 7, 2024, valor);
        Instant dataPagamento = Instant.now();

        mensalidade.marcarComoPaga(dataPagamento);
        mensalidade.marcarComoPaga(dataPagamento); // Deve lançar exceção
    }

    @Test
    public void testAtualizarStatusParaAtrasada() {
        LocalDate dataPassada = LocalDate.now().minusMonths(2);
        Mensalidade mensalidade = Mensalidade.fromEntity(
            UUID.randomUUID(), associadoId, dataPassada.getMonthValue(), dataPassada.getYear(),
            valor, StatusMensalidade.PENDENTE, dataPassada.withDayOfMonth(10), null,
            null, "PIX123", Instant.now(), null
        );

        mensalidade.atualizarStatus();

        assertEquals(StatusMensalidade.ATRASADA, mensalidade.getStatus());
    }

    @Test
    public void testAtualizarStatusQuandoNaoVencida() {
        LocalDate dataFutura = LocalDate.now().plusMonths(1);
        Mensalidade mensalidade = Mensalidade.fromEntity(
            UUID.randomUUID(), associadoId, dataFutura.getMonthValue(), dataFutura.getYear(),
            valor, StatusMensalidade.PENDENTE, dataFutura.withDayOfMonth(10), null,
            null, "PIX123", Instant.now(), null
        );

        mensalidade.atualizarStatus();

        assertEquals(StatusMensalidade.PENDENTE, mensalidade.getStatus());
    }

    @Test
    public void testAtualizarStatusQuandoJaPaga() {
        LocalDate dataPassada = LocalDate.now().minusMonths(1);
        Mensalidade mensalidade = Mensalidade.fromEntity(
            UUID.randomUUID(), associadoId, dataPassada.getMonthValue(), dataPassada.getYear(),
            valor, StatusMensalidade.PAGA, dataPassada.withDayOfMonth(10), Instant.now(),
            null, "PIX123", Instant.now(), FormaPagamento.PIX
        );

        mensalidade.atualizarStatus();

        assertEquals(StatusMensalidade.PAGA, mensalidade.getStatus());
    }

    @Test
    public void testIsVencida() {
        LocalDate dataPassada = LocalDate.now().minusDays(5);
        Mensalidade mensalidadeVencida = Mensalidade.fromEntity(
            UUID.randomUUID(), associadoId, dataPassada.getMonthValue(), dataPassada.getYear(),
            valor, StatusMensalidade.PENDENTE, dataPassada, null,
            null, "PIX123", Instant.now(), null
        );

        LocalDate dataFutura = LocalDate.now().plusDays(5);
        Mensalidade mensalidadeNaoVencida = Mensalidade.fromEntity(
            UUID.randomUUID(), associadoId, dataFutura.getMonthValue(), dataFutura.getYear(),
            valor, StatusMensalidade.PENDENTE, dataFutura, null,
            null, "PIX456", Instant.now(), null
        );

        assertTrue(mensalidadeVencida.isVencida());
        assertFalse(mensalidadeNaoVencida.isVencida());
    }

    @Test
    public void testGetChaveReferencia() {
        Mensalidade mensalidade1 = Mensalidade.criar(associadoId, 1, 2024, valor);
        Mensalidade mensalidade2 = Mensalidade.criar(associadoId, 12, 2024, valor);

        assertEquals("2024-01", mensalidade1.getChaveReferencia());
        assertEquals("2024-12", mensalidade2.getChaveReferencia());
    }

    @Test
    public void testSetQrCodePix() {
        Mensalidade mensalidade = Mensalidade.criar(associadoId, 8, 2024, valor);
        String qrCode = "00020126580014BR.GOV.BCB.PIX...";

        assertNull(mensalidade.getQrCodePix());
        
        mensalidade.setQrCodePix(qrCode);
        
        assertEquals(qrCode, mensalidade.getQrCodePix());
    }

    @Test
    public void testDataVencimentoDia10() {
        for (int mes = 1; mes <= 12; mes++) {
            Mensalidade mensalidade = Mensalidade.criar(associadoId, mes, 2024, valor);
            assertEquals(10, mensalidade.getDataVencimento().getDayOfMonth());
            assertEquals(mes, mensalidade.getDataVencimento().getMonthValue());
            assertEquals(2024, mensalidade.getDataVencimento().getYear());
        }
    }
}