package br.org.cecairbar.durvalcrm.application.usecase.financeiro;

import br.org.cecairbar.durvalcrm.application.dto.CategoriaFinanceiraDTO;
import br.org.cecairbar.durvalcrm.application.financeiro.CategoriaFinanceiraMapper;
import br.org.cecairbar.durvalcrm.domain.model.CategoriaFinanceira;
import br.org.cecairbar.durvalcrm.domain.model.TipoCategoriaFinanceira;
import br.org.cecairbar.durvalcrm.domain.repository.CategoriaFinanceiraRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class CategoriaFinanceiraUseCaseImpl implements CategoriaFinanceiraUseCase {

    @Inject
    CategoriaFinanceiraRepository categoriaRepository;

    @Inject
    CategoriaFinanceiraMapper mapper;

    @Override
    @Transactional
    public CategoriaFinanceiraDTO criar(CategoriaFinanceiraDTO categoriaDTO) {
        // Validar nome único
        if (categoriaRepository.existsByNome(categoriaDTO.getNome())) {
            throw new BadRequestException("Já existe uma categoria com este nome");
        }

        // Converter DTO para domain
        CategoriaFinanceira categoria = mapper.toDomain(categoriaDTO);

        // Salvar
        categoriaRepository.save(categoria);

        // Retornar DTO
        return mapper.toDTO(categoria);
    }

    @Override
    public CategoriaFinanceiraDTO buscarPorId(UUID id) {
        CategoriaFinanceira categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoria financeira não encontrada"));

        return mapper.toDTO(categoria);
    }

    @Override
    @Transactional
    public CategoriaFinanceiraDTO atualizar(UUID id, CategoriaFinanceiraDTO categoriaDTO) {
        CategoriaFinanceira categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoria financeira não encontrada"));

        // Validar nome único (se mudou)
        if (!categoria.getNome().equals(categoriaDTO.getNome()) &&
            categoriaRepository.existsByNome(categoriaDTO.getNome())) {
            throw new BadRequestException("Já existe uma categoria com este nome");
        }

        // Atualizar
        mapper.updateDomainFromDTO(categoriaDTO, categoria);
        categoriaRepository.save(categoria);

        return mapper.toDTO(categoria);
    }

    @Override
    @Transactional
    public void desativar(UUID id) {
        CategoriaFinanceira categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoria financeira não encontrada"));

        if (!categoria.getAtiva()) {
            throw new BadRequestException("Categoria já está desativada");
        }

        categoria.setAtiva(false);
        categoriaRepository.save(categoria);
    }

    @Override
    @Transactional
    public CategoriaFinanceiraDTO reativar(UUID id) {
        CategoriaFinanceira categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoria financeira não encontrada"));

        if (categoria.getAtiva()) {
            throw new BadRequestException("Categoria já está ativa");
        }

        categoria.setAtiva(true);
        categoriaRepository.save(categoria);

        return mapper.toDTO(categoria);
    }

    @Override
    public List<CategoriaFinanceiraDTO> listarTodas() {
        List<CategoriaFinanceira> categorias = categoriaRepository.findAll();
        return mapper.toDTOList(categorias);
    }

    @Override
    public List<CategoriaFinanceiraDTO> listarAtivas() {
        List<CategoriaFinanceira> categorias = categoriaRepository.findByAtiva(true);
        return mapper.toDTOList(categorias);
    }

    @Override
    public List<CategoriaFinanceiraDTO> listarPorTipo(TipoCategoriaFinanceira tipo) {
        List<CategoriaFinanceira> categorias = categoriaRepository.findByTipo(tipo);
        return mapper.toDTOList(categorias);
    }

    @Override
    public List<CategoriaFinanceiraDTO> listarAtivasPorTipo(TipoCategoriaFinanceira tipo) {
        List<CategoriaFinanceira> categorias = categoriaRepository.findByTipoAndAtiva(tipo, true);
        return mapper.toDTOList(categorias);
    }

    @Override
    public boolean existeNome(String nome) {
        return categoriaRepository.existsByNome(nome);
    }
}
