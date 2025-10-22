package br.org.cecairbar.durvalcrm.application.financeiro;

import br.org.cecairbar.durvalcrm.application.dto.CategoriaFinanceiraDTO;
import br.org.cecairbar.durvalcrm.domain.model.CategoriaFinanceira;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "cdi",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CategoriaFinanceiraMapper {

    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "atualizadoEm", ignore = true)
    CategoriaFinanceiraDTO toDTO(CategoriaFinanceira categoria);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CategoriaFinanceira toDomain(CategoriaFinanceiraDTO dto);

    List<CategoriaFinanceiraDTO> toDTOList(List<CategoriaFinanceira> categorias);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateDomainFromDTO(CategoriaFinanceiraDTO dto, @MappingTarget CategoriaFinanceira categoria);
}
