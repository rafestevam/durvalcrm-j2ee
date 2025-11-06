package br.org.cecairbar.durvalcrm.domain.repository;

import br.org.cecairbar.durvalcrm.domain.model.ContaBancaria;
import br.org.cecairbar.durvalcrm.domain.model.FinalidadeConta;
import br.org.cecairbar.durvalcrm.domain.model.StatusConta;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Interface de repositório para Conta Bancária.
 *
 * US-060: Cadastro de Contas Bancárias e Caixa
 * US-067: Integração Automática de Vendas com Contas Bancárias
 */
public interface ContaBancariaRepository {
    ContaBancaria save(ContaBancaria conta);
    Optional<ContaBancaria> findById(UUID id);
    List<ContaBancaria> findAll();
    List<ContaBancaria> findByStatus(StatusConta status);
    Optional<ContaBancaria> findByNome(String nome);

    /**
     * Busca contas bancárias ativas por finalidade.
     * Retorna apenas contas ativas para garantir que vendas sejam lançadas apenas em contas válidas.
     *
     * US-067: Integração Automática de Vendas com Contas Bancárias
     */
    List<ContaBancaria> findByFinalidadeAndStatus(FinalidadeConta finalidade, StatusConta status);

    void delete(UUID id);
    boolean existsByNome(String nome);
}
