package br.org.cecairbar.durvalcrm.application.financeiro;

import br.org.cecairbar.durvalcrm.domain.model.Recebimento;
import br.org.cecairbar.durvalcrm.infrastructure.persistence.entity.RecebimentoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * Mapper entre Recebimento (domínio) e RecebimentoEntity (persistência).
 *
 * US-061: Registro de Recebimentos por Forma de Pagamento
 */
@Mapper(componentModel = "cdi")
public interface RecebimentoMapper {
    Recebimento toDomain(RecebimentoEntity entity);
    RecebimentoEntity toEntity(Recebimento domain);
    List<Recebimento> toDomainList(List<RecebimentoEntity> entities);
    void updateEntityFromDomain(Recebimento domain, @MappingTarget RecebimentoEntity entity);
}
