package br.org.cecairbar.durvalcrm.application.financeiro;

import br.org.cecairbar.durvalcrm.domain.model.ContaBancaria;
import br.org.cecairbar.durvalcrm.domain.model.StatusConta;
import br.org.cecairbar.durvalcrm.domain.repository.ContaBancariaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Use Case para operações de Conta Bancária.
 *
 * US-060: Cadastro de Contas Bancárias e Caixa
 */
@ApplicationScoped
public class ContaBancariaUseCase {

    @Inject
    ContaBancariaRepository repository;

    @Inject
    ContaBancariaDTOMapper mapper;

    public List<ContaBancariaDTO> findAll() {
        return mapper.toDTOList(repository.findAll());
    }

    public List<ContaBancariaDTO> findByStatus(StatusConta status) {
        return mapper.toDTOList(repository.findByStatus(status));
    }

    public ContaBancariaDTO findById(UUID id) {
        return repository.findById(id)
                .map(mapper::toDTO)
                .orElseThrow(() -> new NotFoundException("Conta não encontrada"));
    }

    public ContaBancariaDTO create(ContaBancariaDTO dto) {
        // Validações
        if (repository.existsByNome(dto.getNome())) {
            throw new BadRequestException("Já existe uma conta com este nome");
        }

        ContaBancaria conta = mapper.toDomain(dto);
        conta.setId(null); // Garantir que é nova
        conta.setStatus(StatusConta.ATIVA);

        // Saldo atual inicial igual ao saldo inicial
        if (conta.getSaldoAtual() == null) {
            conta.setSaldoAtual(conta.getSaldoInicial() != null ? conta.getSaldoInicial() : BigDecimal.ZERO);
        }

        ContaBancaria saved = repository.save(conta);
        return mapper.toDTO(saved);
    }

    public ContaBancariaDTO update(UUID id, ContaBancariaDTO dto) {
        ContaBancaria existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Conta não encontrada"));

        // Validar mudança de nome
        if (!existing.getNome().equals(dto.getNome()) && repository.existsByNome(dto.getNome())) {
            throw new BadRequestException("Já existe uma conta com este nome");
        }

        ContaBancaria conta = mapper.toDomain(dto);
        conta.setId(id);

        // Manter saldo atual (não pode ser alterado diretamente)
        conta.setSaldoAtual(existing.getSaldoAtual());

        ContaBancaria updated = repository.save(conta);
        return mapper.toDTO(updated);
    }

    public void inativar(UUID id) {
        ContaBancaria conta = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Conta não encontrada"));

        conta.setStatus(StatusConta.INATIVA);
        repository.save(conta);
    }
}
