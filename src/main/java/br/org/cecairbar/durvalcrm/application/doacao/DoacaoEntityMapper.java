package br.org.cecairbar.durvalcrm.application.doacao;

import br.org.cecairbar.durvalcrm.domain.model.Associado;
import br.org.cecairbar.durvalcrm.domain.model.Doacao;
import br.org.cecairbar.durvalcrm.infrastructure.persistence.entity.AssociadoEntity;
import br.org.cecairbar.durvalcrm.infrastructure.persistence.entity.DoacaoEntity;
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
public abstract class DoacaoEntityMapper {
    
    @Inject
    protected EntityManager entityManager;
    
    @Mapping(source = "associado", target = "associado", qualifiedByName = "associadoToEntity")
    public abstract DoacaoEntity toEntity(Doacao doacao);
    
    @Mapping(source = "associado", target = "associado", qualifiedByName = "entityToAssociado")
    public abstract Doacao toDomain(DoacaoEntity entity);
    
    public abstract List<Doacao> toDomainList(List<DoacaoEntity> entities);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "associado", ignore = true)
    public abstract void updateEntityFromDomain(Doacao doacao, @MappingTarget DoacaoEntity entity);
    
    @Named("associadoToEntity")
    protected AssociadoEntity associadoToEntity(Associado associado) {
        if (associado == null || associado.getId() == null) {
            return null;
        }
        // Get reference to existing entity instead of creating a new one
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