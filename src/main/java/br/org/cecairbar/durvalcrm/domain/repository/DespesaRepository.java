package br.org.cecairbar.durvalcrm.domain.repository;

import br.org.cecairbar.durvalcrm.domain.model.Despesa;
import br.org.cecairbar.durvalcrm.domain.model.StatusPagamentoDespesa;
import br.org.cecairbar.durvalcrm.domain.model.TipoDespesa;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DespesaRepository {
    Despesa save(Despesa despesa);

    Optional<Despesa> findById(UUID id);

    List<Despesa> findAll();

    List<Despesa> findByTipoDespesa(TipoDespesa tipo);

    List<Despesa> findByCategoria(UUID categoriaId);

    List<Despesa> findByStatus(StatusPagamentoDespesa status);

    List<Despesa> findByPeriodo(LocalDate inicio, LocalDate fim);

    List<Despesa> findByDataVencimento(LocalDate inicio, LocalDate fim);

    List<Despesa> findByDataPagamento(LocalDate inicio, LocalDate fim);

    List<Despesa> findByStatusAndDataVencimento(StatusPagamentoDespesa status, LocalDate inicio, LocalDate fim);

    List<Despesa> findByTipoAndPeriodo(TipoDespesa tipo, LocalDate inicio, LocalDate fim);

    List<Despesa> findByCategoriaAndPeriodo(UUID categoriaId, LocalDate inicio, LocalDate fim);

    List<Despesa> findByFornecedor(String fornecedor);

    List<Despesa> findVencidas(LocalDate dataReferencia);

    BigDecimal sumByPeriodo(LocalDate inicio, LocalDate fim);

    BigDecimal sumByCategoria(UUID categoriaId, LocalDate inicio, LocalDate fim);

    BigDecimal sumByTipo(TipoDespesa tipo, LocalDate inicio, LocalDate fim);

    BigDecimal sumByStatus(StatusPagamentoDespesa status, LocalDate inicio, LocalDate fim);

    BigDecimal sumByFornecedor(String fornecedor, LocalDate inicio, LocalDate fim);

    long countByStatus(StatusPagamentoDespesa status);

    long countByTipo(TipoDespesa tipo);

    long countVencidas(LocalDate dataReferencia);

    void delete(Despesa despesa);

    boolean existsById(UUID id);

    boolean existsByNumeroDocumento(String numeroDocumento);
}
