package br.org.cecairbar.durvalcrm.domain.repository;

import br.org.cecairbar.durvalcrm.domain.model.Receita;
import br.org.cecairbar.durvalcrm.domain.model.TipoReceita;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReceitaRepository {
    Receita save(Receita receita);

    Optional<Receita> findById(UUID id);

    List<Receita> findAll();

    List<Receita> findByTipoReceita(TipoReceita tipo);

    List<Receita> findByCategoria(UUID categoriaId);

    List<Receita> findByAssociado(UUID associadoId);

    List<Receita> findByPeriodo(LocalDate inicio, LocalDate fim);

    List<Receita> findByDataRecebimento(LocalDate inicio, LocalDate fim);

    List<Receita> findByTipoAndPeriodo(TipoReceita tipo, LocalDate inicio, LocalDate fim);

    List<Receita> findByCategoriaAndPeriodo(UUID categoriaId, LocalDate inicio, LocalDate fim);

    Optional<Receita> findByOrigemId(UUID origemId);

    BigDecimal sumByPeriodo(LocalDate inicio, LocalDate fim);

    BigDecimal sumByCategoria(UUID categoriaId, LocalDate inicio, LocalDate fim);

    BigDecimal sumByTipo(TipoReceita tipo, LocalDate inicio, LocalDate fim);

    BigDecimal sumByAssociado(UUID associadoId, LocalDate inicio, LocalDate fim);

    long countByTipo(TipoReceita tipo);

    long countByPeriodo(LocalDate inicio, LocalDate fim);

    void delete(Receita receita);

    boolean existsById(UUID id);

    boolean existsByOrigemId(UUID origemId);
}
