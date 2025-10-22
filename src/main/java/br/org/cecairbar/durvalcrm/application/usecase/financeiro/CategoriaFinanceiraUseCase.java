package br.org.cecairbar.durvalcrm.application.usecase.financeiro;

import br.org.cecairbar.durvalcrm.application.dto.CategoriaFinanceiraDTO;
import br.org.cecairbar.durvalcrm.domain.model.TipoCategoriaFinanceira;

import java.util.List;
import java.util.UUID;

public interface CategoriaFinanceiraUseCase {

    /**
     * Criar nova categoria financeira
     */
    CategoriaFinanceiraDTO criar(CategoriaFinanceiraDTO categoriaDTO);

    /**
     * Buscar categoria por ID
     */
    CategoriaFinanceiraDTO buscarPorId(UUID id);

    /**
     * Atualizar categoria
     */
    CategoriaFinanceiraDTO atualizar(UUID id, CategoriaFinanceiraDTO categoriaDTO);

    /**
     * Desativar categoria (soft delete)
     */
    void desativar(UUID id);

    /**
     * Reativar categoria
     */
    CategoriaFinanceiraDTO reativar(UUID id);

    /**
     * Listar todas as categorias
     */
    List<CategoriaFinanceiraDTO> listarTodas();

    /**
     * Listar categorias ativas
     */
    List<CategoriaFinanceiraDTO> listarAtivas();

    /**
     * Listar categorias por tipo
     */
    List<CategoriaFinanceiraDTO> listarPorTipo(TipoCategoriaFinanceira tipo);

    /**
     * Listar categorias ativas por tipo
     */
    List<CategoriaFinanceiraDTO> listarAtivasPorTipo(TipoCategoriaFinanceira tipo);

    /**
     * Verificar se nome já existe
     */
    boolean existeNome(String nome);
}
