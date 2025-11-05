package br.org.cecairbar.durvalcrm.domain.repository;

import br.org.cecairbar.durvalcrm.domain.model.OrigemRecebimento;
import br.org.cecairbar.durvalcrm.domain.model.Recebimento;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Interface de repositório para Recebimento.
 *
 * US-061: Registro de Recebimentos por Forma de Pagamento
 */
public interface RecebimentoRepository {
    Recebimento save(Recebimento recebimento);
    Optional<Recebimento> findById(UUID id);
    List<Recebimento> findAll();
    List<Recebimento> findByContaBancariaId(UUID contaId);
    List<Recebimento> findByPeriodo(LocalDate dataInicio, LocalDate dataFim);
    List<Recebimento> findByOrigem(OrigemRecebimento origem);
    void delete(UUID id);
}
