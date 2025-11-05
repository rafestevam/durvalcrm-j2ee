package br.org.cecairbar.durvalcrm.domain.repository;

import br.org.cecairbar.durvalcrm.domain.model.ContaBancaria;
import br.org.cecairbar.durvalcrm.domain.model.StatusConta;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Interface de repositório para Conta Bancária.
 *
 * US-060: Cadastro de Contas Bancárias e Caixa
 */
public interface ContaBancariaRepository {
    ContaBancaria save(ContaBancaria conta);
    Optional<ContaBancaria> findById(UUID id);
    List<ContaBancaria> findAll();
    List<ContaBancaria> findByStatus(StatusConta status);
    Optional<ContaBancaria> findByNome(String nome);
    void delete(UUID id);
    boolean existsByNome(String nome);
}
