package br.org.cecairbar.durvalcrm.application.financeiro;

import br.org.cecairbar.durvalcrm.domain.model.CategoriaFinanceira;
import br.org.cecairbar.durvalcrm.infrastructure.persistence.entity.CategoriaFinanceiraEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "cdi",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class CategoriaFinanceiraEntityMapper {

    public abstract CategoriaFinanceiraEntity toEntity(CategoriaFinanceira categoria);

    public abstract CategoriaFinanceira toDomain(CategoriaFinanceiraEntity entity);

    public abstract List<CategoriaFinanceira> toDomainList(List<CategoriaFinanceiraEntity> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    public abstract void updateEntityFromDomain(CategoriaFinanceira categoria, @MappingTarget CategoriaFinanceiraEntity entity);
}
