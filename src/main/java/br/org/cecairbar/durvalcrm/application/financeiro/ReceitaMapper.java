package br.org.cecairbar.durvalcrm.application.financeiro;

import br.org.cecairbar.durvalcrm.application.dto.ReceitaDTO;
import br.org.cecairbar.durvalcrm.domain.model.Receita;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "cdi",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ReceitaMapper {

    @Mapping(source = "categoria.id", target = "categoriaId")
    @Mapping(source = "categoria.nome", target = "categoriaNome")
    @Mapping(source = "associado.id", target = "associadoId")
    @Mapping(source = "associado.nomeCompleto", target = "associadoNome")
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "atualizadoEm", ignore = true)
    ReceitaDTO toDTO(Receita receita);

    @Mapping(source = "categoriaId", target = "categoria.id")
    @Mapping(source = "associadoId", target = "associado.id")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "categoria.nome", ignore = true)
    @Mapping(target = "categoria.descricao", ignore = true)
    @Mapping(target = "categoria.tipo", ignore = true)
    @Mapping(target = "categoria.ativa", ignore = true)
    @Mapping(target = "categoria.cor", ignore = true)
    @Mapping(target = "categoria.createdAt", ignore = true)
    @Mapping(target = "categoria.updatedAt", ignore = true)
    @Mapping(target = "associado.nomeCompleto", ignore = true)
    @Mapping(target = "associado.cpf", ignore = true)
    @Mapping(target = "associado.email", ignore = true)
    @Mapping(target = "associado.telefone", ignore = true)
    @Mapping(target = "associado.ativo", ignore = true)
    Receita toDomain(ReceitaDTO dto);

    List<ReceitaDTO> toDTOList(List<Receita> receitas);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categoria", ignore = true)
    @Mapping(target = "associado", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateDomainFromDTO(ReceitaDTO dto, @MappingTarget Receita receita);
}
