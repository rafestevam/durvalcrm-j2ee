package br.org.cecairbar.durvalcrm.application.usecase.financeiro;

import br.org.cecairbar.durvalcrm.application.dto.ReceitaDTO;
import br.org.cecairbar.durvalcrm.application.financeiro.ReceitaMapper;
import br.org.cecairbar.durvalcrm.domain.model.Receita;
import br.org.cecairbar.durvalcrm.domain.model.TipoReceita;
import br.org.cecairbar.durvalcrm.domain.repository.ReceitaRepository;
import br.org.cecairbar.durvalcrm.domain.repository.CategoriaFinanceiraRepository;
import br.org.cecairbar.durvalcrm.domain.repository.AssociadoRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ReceitaUseCaseImpl implements ReceitaUseCase {

    @Inject
    ReceitaRepository receitaRepository;

    @Inject
    CategoriaFinanceiraRepository categoriaRepository;

    @Inject
    AssociadoRepository associadoRepository;

    @Inject
    ReceitaMapper mapper;

    @Override
    @Transactional
    public ReceitaDTO criar(ReceitaDTO receitaDTO) {
        // Validar categoria existe
        if (receitaDTO.getCategoriaId() != null) {
            categoriaRepository.findById(receitaDTO.getCategoriaId())
                    .orElseThrow(() -> new BadRequestException("Categoria financeira não encontrada"));
        }

        // Validar associado existe (se informado)
        if (receitaDTO.getAssociadoId() != null) {
            associadoRepository.findById(receitaDTO.getAssociadoId())
                    .orElseThrow(() -> new BadRequestException("Associado não encontrado"));
        }

        // Validar origem única (se informada)
        if (receitaDTO.getOrigemId() != null && receitaRepository.existsByOrigemId(receitaDTO.getOrigemId())) {
            throw new BadRequestException("Já existe uma receita vinculada a esta origem");
        }

        // Converter DTO para domain
        Receita receita = mapper.toDomain(receitaDTO);

        // Salvar
        receitaRepository.save(receita);

        // Retornar DTO
        return mapper.toDTO(receita);
    }

    @Override
    public ReceitaDTO buscarPorId(UUID id) {
        Receita receita = receitaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Receita não encontrada"));

        return mapper.toDTO(receita);
    }

    @Override
    @Transactional
    public ReceitaDTO atualizar(UUID id, ReceitaDTO receitaDTO) {
        Receita receita = receitaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Receita não encontrada"));

        // Validar categoria existe
        if (receitaDTO.getCategoriaId() != null) {
            categoriaRepository.findById(receitaDTO.getCategoriaId())
                    .orElseThrow(() -> new BadRequestException("Categoria financeira não encontrada"));
        }

        // Validar associado existe (se informado)
        if (receitaDTO.getAssociadoId() != null) {
            associadoRepository.findById(receitaDTO.getAssociadoId())
                    .orElseThrow(() -> new BadRequestException("Associado não encontrado"));
        }

        // Atualizar
        mapper.updateDomainFromDTO(receitaDTO, receita);
        receitaRepository.save(receita);

        return mapper.toDTO(receita);
    }

    @Override
    @Transactional
    public void deletar(UUID id) {
        Receita receita = receitaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Receita não encontrada"));
        receitaRepository.delete(receita);
    }

    @Override
    public List<ReceitaDTO> listarTodas() {
        List<Receita> receitas = receitaRepository.findAll();
        return mapper.toDTOList(receitas);
    }

    @Override
    public List<ReceitaDTO> listarPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        validarPeriodo(dataInicio, dataFim);

        List<Receita> receitas = receitaRepository.findByPeriodo(dataInicio, dataFim);
        return mapper.toDTOList(receitas);
    }

    @Override
    public List<ReceitaDTO> listarPorDataRecebimento(LocalDate dataInicio, LocalDate dataFim) {
        validarPeriodo(dataInicio, dataFim);

        List<Receita> receitas = receitaRepository.findByDataRecebimento(dataInicio, dataFim);
        return mapper.toDTOList(receitas);
    }

    @Override
    public List<ReceitaDTO> listarPorTipo(TipoReceita tipo) {
        List<Receita> receitas = receitaRepository.findByTipoReceita(tipo);
        return mapper.toDTOList(receitas);
    }

    @Override
    public List<ReceitaDTO> listarPorTipoEPeriodo(TipoReceita tipo, LocalDate dataInicio, LocalDate dataFim) {
        validarPeriodo(dataInicio, dataFim);

        List<Receita> receitas = receitaRepository.findByTipoAndPeriodo(tipo, dataInicio, dataFim);
        return mapper.toDTOList(receitas);
    }

    @Override
    public List<ReceitaDTO> listarPorCategoria(UUID categoriaId) {
        // Validar categoria existe
        categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new NotFoundException("Categoria financeira não encontrada"));

        List<Receita> receitas = receitaRepository.findByCategoria(categoriaId);
        return mapper.toDTOList(receitas);
    }

    @Override
    public List<ReceitaDTO> listarPorCategoriaEPeriodo(UUID categoriaId, LocalDate dataInicio, LocalDate dataFim) {
        validarPeriodo(dataInicio, dataFim);

        // Validar categoria existe
        categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new NotFoundException("Categoria financeira não encontrada"));

        List<Receita> receitas = receitaRepository.findByCategoriaAndPeriodo(categoriaId, dataInicio, dataFim);
        return mapper.toDTOList(receitas);
    }

    @Override
    public List<ReceitaDTO> listarPorAssociado(UUID associadoId) {
        // Validar associado existe
        associadoRepository.findById(associadoId)
                .orElseThrow(() -> new NotFoundException("Associado não encontrado"));

        List<Receita> receitas = receitaRepository.findByAssociado(associadoId);
        return mapper.toDTOList(receitas);
    }

    @Override
    public BigDecimal somarPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        validarPeriodo(dataInicio, dataFim);
        return receitaRepository.sumByPeriodo(dataInicio, dataFim);
    }

    @Override
    public BigDecimal somarPorCategoriaEPeriodo(UUID categoriaId, LocalDate dataInicio, LocalDate dataFim) {
        validarPeriodo(dataInicio, dataFim);

        // Validar categoria existe
        categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new NotFoundException("Categoria financeira não encontrada"));

        return receitaRepository.sumByCategoria(categoriaId, dataInicio, dataFim);
    }

    @Override
    public BigDecimal somarPorTipoEPeriodo(TipoReceita tipo, LocalDate dataInicio, LocalDate dataFim) {
        validarPeriodo(dataInicio, dataFim);
        return receitaRepository.sumByTipo(tipo, dataInicio, dataFim);
    }

    @Override
    public BigDecimal somarPorAssociadoEPeriodo(UUID associadoId, LocalDate dataInicio, LocalDate dataFim) {
        validarPeriodo(dataInicio, dataFim);

        // Validar associado existe
        associadoRepository.findById(associadoId)
                .orElseThrow(() -> new NotFoundException("Associado não encontrado"));

        return receitaRepository.sumByAssociado(associadoId, dataInicio, dataFim);
    }

    @Override
    public boolean existePorOrigem(UUID origemId) {
        return receitaRepository.existsByOrigemId(origemId);
    }

    // Métodos auxiliares
    private void validarPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio.isAfter(dataFim)) {
            throw new BadRequestException("Data de início deve ser anterior à data de fim");
        }
    }
}
