package br.org.cecairbar.durvalcrm.application.financeiro;

import br.org.cecairbar.durvalcrm.application.dto.DespesaDTO;
import br.org.cecairbar.durvalcrm.domain.model.Despesa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "cdi",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DespesaMapper {

    @Mapping(source = "categoria.id", target = "categoriaId")
    @Mapping(source = "categoria.nome", target = "categoriaNome")
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "atualizadoEm", ignore = true)
    DespesaDTO toDTO(Despesa despesa);

    @Mapping(source = "categoriaId", target = "categoria.id")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "categoria.nome", ignore = true)
    @Mapping(target = "categoria.descricao", ignore = true)
    @Mapping(target = "categoria.tipo", ignore = true)
    @Mapping(target = "categoria.ativa", ignore = true)
    @Mapping(target = "categoria.cor", ignore = true)
    @Mapping(target = "categoria.createdAt", ignore = true)
    @Mapping(target = "categoria.updatedAt", ignore = true)
    Despesa toDomain(DespesaDTO dto);

    List<DespesaDTO> toDTOList(List<Despesa> despesas);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categoria", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateDomainFromDTO(DespesaDTO dto, @MappingTarget Despesa despesa);
}
