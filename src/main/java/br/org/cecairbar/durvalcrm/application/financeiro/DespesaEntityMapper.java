package br.org.cecairbar.durvalcrm.application.financeiro;

import br.org.cecairbar.durvalcrm.domain.model.CategoriaFinanceira;
import br.org.cecairbar.durvalcrm.domain.model.Despesa;
import br.org.cecairbar.durvalcrm.infrastructure.persistence.entity.CategoriaFinanceiraEntity;
import br.org.cecairbar.durvalcrm.infrastructure.persistence.entity.DespesaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import jakarta.persistence.EntityManager;
import jakarta.inject.Inject;
import java.util.List;

@Mapper(componentModel = "cdi",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class DespesaEntityMapper {

    @Inject
    protected EntityManager entityManager;

    @Mapping(source = "categoria", target = "categoria", qualifiedByName = "categoriaToEntity")
    public abstract DespesaEntity toEntity(Despesa despesa);

    @Mapping(source = "categoria", target = "categoria", qualifiedByName = "entityToCategoria")
    public abstract Despesa toDomain(DespesaEntity entity);

    public abstract List<Despesa> toDomainList(List<DespesaEntity> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "categoria", ignore = true)
    public abstract void updateEntityFromDomain(Despesa despesa, @MappingTarget DespesaEntity entity);

    @Named("categoriaToEntity")
    protected CategoriaFinanceiraEntity categoriaToEntity(CategoriaFinanceira categoria) {
        if (categoria == null || categoria.getId() == null) {
            return null;
        }
        return entityManager.getReference(CategoriaFinanceiraEntity.class, categoria.getId());
    }

    @Named("entityToCategoria")
    protected CategoriaFinanceira entityToCategoria(CategoriaFinanceiraEntity entity) {
        if (entity == null) {
            return null;
        }
        CategoriaFinanceira categoria = new CategoriaFinanceira();
        categoria.setId(entity.id);
        categoria.setNome(entity.nome);
        categoria.setDescricao(entity.descricao);
        categoria.setTipo(entity.tipo);
        categoria.setAtiva(entity.ativa);
        categoria.setCor(entity.cor);
        categoria.setCreatedAt(entity.createdAt);
        categoria.setUpdatedAt(entity.updatedAt);
        return categoria;
    }
}
