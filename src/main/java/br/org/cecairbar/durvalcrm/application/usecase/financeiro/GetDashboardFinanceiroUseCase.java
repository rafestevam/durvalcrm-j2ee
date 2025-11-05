package br.org.cecairbar.durvalcrm.application.usecase.financeiro;

import br.org.cecairbar.durvalcrm.application.dto.*;
import br.org.cecairbar.durvalcrm.domain.model.*;
import br.org.cecairbar.durvalcrm.domain.repository.ContaBancariaRepository;
import br.org.cecairbar.durvalcrm.domain.repository.RecebimentoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Use case for getting consolidated financial dashboard (US-062)
 */
@ApplicationScoped
public class GetDashboardFinanceiroUseCase {

    @Inject
    private ContaBancariaRepository contaBancariaRepository;

    @Inject
    private RecebimentoRepository recebimentoRepository;

    public DashboardFinanceiroDTO execute(LocalDate dataInicio, LocalDate dataFim) {
        // Default to current month if dates not provided
        if (dataInicio == null) {
            dataInicio = LocalDate.now().withDayOfMonth(1);
        }
        if (dataFim == null) {
            dataFim = LocalDate.now();
        }

        // Get all active accounts
        List<ContaBancaria> todasContas = contaBancariaRepository.findByStatus(StatusConta.ATIVA);

        // Separate bank accounts and physical cash
        List<SaldoContaDTO> contasBancarias = todasContas.stream()
                .filter(c -> c.getTipo() == TipoConta.BANCARIA)
                .map(this::toSaldoContaDTO)
                .collect(Collectors.toList());

        List<SaldoContaDTO> caixasFisicos = todasContas.stream()
                .filter(c -> c.getTipo() == TipoConta.CAIXA_FISICO)
                .map(this::toSaldoContaDTO)
                .collect(Collectors.toList());

        // Calculate consolidated balance
        BigDecimal saldoTotal = todasContas.stream()
                .map(ContaBancaria::getSaldoAtual)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Get receipts for period
        RecebimentosPeriodoDTO recebimentosPeriodo = getRecebimentosPeriodo(dataInicio, dataFim);

        return new DashboardFinanceiroDTO(saldoTotal, contasBancarias, caixasFisicos, recebimentosPeriodo);
    }

    private SaldoContaDTO toSaldoContaDTO(ContaBancaria conta) {
        return new SaldoContaDTO(
                conta.getId(),
                conta.getNome(),
                conta.getFinalidade(),
                conta.getSaldoAtual()
        );
    }

    private RecebimentosPeriodoDTO getRecebimentosPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        List<Recebimento> recebimentos = recebimentoRepository.findByPeriodo(dataInicio, dataFim);

        // Calculate total
        BigDecimal totalPeriodo = recebimentos.stream()
                .map(Recebimento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Group by payment method
        Map<FormaPagamentoRecebimento, List<Recebimento>> porFormaPagamento =
                recebimentos.stream().collect(Collectors.groupingBy(Recebimento::getFormaPagamento));

        List<RecebimentoPorFormaPagamentoDTO> recebimentosPorForma = porFormaPagamento.entrySet().stream()
                .map(entry -> {
                    BigDecimal total = entry.getValue().stream()
                            .map(Recebimento::getValor)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    Long quantidade = (long) entry.getValue().size();

                    RecebimentoPorFormaPagamentoDTO dto = new RecebimentoPorFormaPagamentoDTO(
                            entry.getKey(), total, quantidade
                    );

                    // Calculate percentage
                    if (totalPeriodo.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal percentual = total.multiply(BigDecimal.valueOf(100))
                                .divide(totalPeriodo, 2, RoundingMode.HALF_UP);
                        dto.setPercentual(percentual);
                    } else {
                        dto.setPercentual(BigDecimal.ZERO);
                    }

                    return dto;
                })
                .collect(Collectors.toList());

        // Group by origin
        Map<OrigemRecebimento, List<Recebimento>> porOrigem =
                recebimentos.stream().collect(Collectors.groupingBy(Recebimento::getOrigem));

        List<RecebimentoPorOrigemDTO> recebimentosPorOrigem = porOrigem.entrySet().stream()
                .map(entry -> {
                    BigDecimal total = entry.getValue().stream()
                            .map(Recebimento::getValor)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    Long quantidade = (long) entry.getValue().size();

                    RecebimentoPorOrigemDTO dto = new RecebimentoPorOrigemDTO(
                            entry.getKey(), total, quantidade
                    );

                    // Calculate percentage
                    if (totalPeriodo.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal percentual = total.multiply(BigDecimal.valueOf(100))
                                .divide(totalPeriodo, 2, RoundingMode.HALF_UP);
                        dto.setPercentual(percentual);
                    } else {
                        dto.setPercentual(BigDecimal.ZERO);
                    }

                    return dto;
                })
                .collect(Collectors.toList());

        return new RecebimentosPeriodoDTO(
                dataInicio,
                dataFim,
                totalPeriodo,
                recebimentosPorForma,
                recebimentosPorOrigem
        );
    }
}
