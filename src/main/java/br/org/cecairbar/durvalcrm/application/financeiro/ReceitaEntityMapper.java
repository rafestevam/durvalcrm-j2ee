package br.org.cecairbar.durvalcrm.application.financeiro;

import br.org.cecairbar.durvalcrm.domain.model.Associado;
import br.org.cecairbar.durvalcrm.domain.model.CategoriaFinanceira;
import br.org.cecairbar.durvalcrm.domain.model.Receita;
import br.org.cecairbar.durvalcrm.infrastructure.persistence.entity.AssociadoEntity;
import br.org.cecairbar.durvalcrm.infrastructure.persistence.entity.CategoriaFinanceiraEntity;
import br.org.cecairbar.durvalcrm.infrastructure.persistence.entity.ReceitaEntity;
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
public abstract class ReceitaEntityMapper {

    @Inject
    protected EntityManager entityManager;

    @Mapping(source = "categoria", target = "categoria", qualifiedByName = "categoriaToEntity")
    @Mapping(source = "associado", target = "associado", qualifiedByName = "associadoToEntity")
    public abstract ReceitaEntity toEntity(Receita receita);

    @Mapping(source = "categoria", target = "categoria", qualifiedByName = "entityToCategoria")
    @Mapping(source = "associado", target = "associado", qualifiedByName = "entityToAssociado")
    public abstract Receita toDomain(ReceitaEntity entity);

    public abstract List<Receita> toDomainList(List<ReceitaEntity> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "categoria", ignore = true)
    @Mapping(target = "associado", ignore = true)
    public abstract void updateEntityFromDomain(Receita receita, @MappingTarget ReceitaEntity entity);

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

    @Named("associadoToEntity")
    protected AssociadoEntity associadoToEntity(Associado associado) {
        if (associado == null || associado.getId() == null) {
            return null;
        }
        return entityManager.getReference(AssociadoEntity.class, associado.getId());
    }

    @Named("entityToAssociado")
    protected Associado entityToAssociado(AssociadoEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Associado(
            entity.id,
            entity.nomeCompleto,
            entity.cpf,
            entity.email,
            entity.telefone,
            entity.ativo
        );
    }
}
