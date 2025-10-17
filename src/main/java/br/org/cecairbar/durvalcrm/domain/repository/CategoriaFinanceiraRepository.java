package br.org.cecairbar.durvalcrm.domain.repository;

import br.org.cecairbar.durvalcrm.domain.model.CategoriaFinanceira;
import br.org.cecairbar.durvalcrm.domain.model.TipoCategoriaFinanceira;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoriaFinanceiraRepository {
    CategoriaFinanceira save(CategoriaFinanceira categoria);

    Optional<CategoriaFinanceira> findById(UUID id);

    List<CategoriaFinanceira> findAll();

    List<CategoriaFinanceira> findByTipo(TipoCategoriaFinanceira tipo);

    List<CategoriaFinanceira> findByAtiva(Boolean ativa);

    List<CategoriaFinanceira> findByTipoAndAtiva(TipoCategoriaFinanceira tipo, Boolean ativa);

    Optional<CategoriaFinanceira> findByNome(String nome);

    void delete(CategoriaFinanceira categoria);

    boolean existsById(UUID id);

    boolean existsByNome(String nome);

    long count();

    long countByTipo(TipoCategoriaFinanceira tipo);
}
