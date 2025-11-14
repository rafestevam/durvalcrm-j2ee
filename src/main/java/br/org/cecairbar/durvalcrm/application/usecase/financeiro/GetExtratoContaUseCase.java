package br.org.cecairbar.durvalcrm.application.usecase.financeiro;

import br.org.cecairbar.durvalcrm.application.dto.ExtratoContaDTO;
import br.org.cecairbar.durvalcrm.application.dto.MovimentacaoExtratoDTO;
import br.org.cecairbar.durvalcrm.domain.model.ContaBancaria;
import br.org.cecairbar.durvalcrm.domain.model.Recebimento;
import br.org.cecairbar.durvalcrm.domain.repository.ContaBancariaRepository;
import br.org.cecairbar.durvalcrm.domain.repository.RecebimentoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Use case for getting detailed account statement (US-064)
 */
@ApplicationScoped
public class GetExtratoContaUseCase {

    @Inject
    private ContaBancariaRepository contaBancariaRepository;

    @Inject
    private RecebimentoRepository recebimentoRepository;

    public ExtratoContaDTO execute(UUID contaId, LocalDate dataInicio, LocalDate dataFim) {
        // Get account
        ContaBancaria conta = contaBancariaRepository.findById(contaId)
                .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada: " + contaId));

        // Default to current month if dates not provided
        final LocalDate periodoInicio = (dataInicio == null) ? LocalDate.now().withDayOfMonth(1) : dataInicio;
        final LocalDate periodoFim = (dataFim == null) ? LocalDate.now() : dataFim;

        // Get all receipts for this account
        List<Recebimento> todosRecebimentos = recebimentoRepository.findByContaBancariaId(contaId);

        // Filter by period and sort by date
        List<Recebimento> recebimentosPeriodo = todosRecebimentos.stream()
                .filter(r -> !r.getDataRecebimento().isBefore(periodoInicio) && !r.getDataRecebimento().isAfter(periodoFim))
                .sorted(Comparator.comparing(Recebimento::getDataRecebimento))
                .collect(Collectors.toList());

        // Debug logging
        System.out.println("[EXTRATO DEBUG] Conta: " + contaId);
        System.out.println("[EXTRATO DEBUG] Período: " + periodoInicio + " até " + periodoFim);
        System.out.println("[EXTRATO DEBUG] Total recebimentos na conta: " + todosRecebimentos.size());
        System.out.println("[EXTRATO DEBUG] Recebimentos no período: " + recebimentosPeriodo.size());
        if (!todosRecebimentos.isEmpty()) {
            System.out.println("[EXTRATO DEBUG] Primeira data de recebimento: " + todosRecebimentos.get(0).getDataRecebimento());
            System.out.println("[EXTRATO DEBUG] Última data de recebimento: " + todosRecebimentos.get(todosRecebimentos.size() - 1).getDataRecebimento());
        }

        // Calculate opening balance (before period)
        BigDecimal saldoInicial = calcularSaldoInicial(todosRecebimentos, periodoInicio, conta.getSaldoInicial());

        // Calculate totals
        BigDecimal totalEntradas = recebimentosPeriodo.stream()
                .map(Recebimento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalSaidas = BigDecimal.ZERO; // For now, no outgoing transactions (will be added in Sprint 3)

        BigDecimal saldoFinal = saldoInicial.add(totalEntradas).subtract(totalSaidas);

        // Build statement lines with running balance
        List<MovimentacaoExtratoDTO> movimentacoes = buildMovimentacoes(recebimentosPeriodo, saldoInicial);

        return new ExtratoContaDTO(
                contaId,
                conta.getNome(),
                periodoInicio,
                periodoFim,
                saldoInicial,
                saldoFinal,
                totalEntradas,
                totalSaidas,
                movimentacoes
        );
    }

    private BigDecimal calcularSaldoInicial(List<Recebimento> todosRecebimentos, LocalDate dataInicio, BigDecimal saldoInicialConta) {
        // Sum all receipts before the period start
        BigDecimal movimentacoesAnteriores = todosRecebimentos.stream()
                .filter(r -> r.getDataRecebimento().isBefore(dataInicio))
                .map(Recebimento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return saldoInicialConta.add(movimentacoesAnteriores);
    }

    private List<MovimentacaoExtratoDTO> buildMovimentacoes(List<Recebimento> recebimentos, BigDecimal saldoInicial) {
        BigDecimal saldoCorrente = saldoInicial;
        List<MovimentacaoExtratoDTO> movimentacoes = new java.util.ArrayList<>();

        for (Recebimento recebimento : recebimentos) {
            saldoCorrente = saldoCorrente.add(recebimento.getValor());

            MovimentacaoExtratoDTO mov = new MovimentacaoExtratoDTO();
            mov.setId(recebimento.getId());
            mov.setData(recebimento.getDataRecebimento());
            mov.setDataHora(recebimento.getCreatedAt());
            mov.setTipo(MovimentacaoExtratoDTO.TipoMovimentacao.ENTRADA);
            mov.setFormaPagamento(recebimento.getFormaPagamento());
            mov.setOrigem(recebimento.getOrigem());
            mov.setDescricao(recebimento.getDescricao());
            mov.setValor(recebimento.getValor());
            mov.setSaldoApos(saldoCorrente);

            // Set link if applicable
            if (recebimento.getMensalidadeId() != null) {
                mov.setVinculoId(recebimento.getMensalidadeId());
                mov.setVinculoTipo("MENSALIDADE");
            } else if (recebimento.getVendaId() != null) {
                mov.setVinculoId(recebimento.getVendaId());
                mov.setVinculoTipo("VENDA");
            }

            movimentacoes.add(mov);
        }

        return movimentacoes;
    }
}
