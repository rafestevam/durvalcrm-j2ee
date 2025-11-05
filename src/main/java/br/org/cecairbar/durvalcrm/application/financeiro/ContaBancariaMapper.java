package br.org.cecairbar.durvalcrm.application.financeiro;

import br.org.cecairbar.durvalcrm.domain.model.ContaBancaria;
import br.org.cecairbar.durvalcrm.infrastructure.persistence.entity.ContaBancariaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * Mapper entre ContaBancaria (domínio) e ContaBancariaEntity (persistência).
 *
 * US-060: Cadastro de Contas Bancárias e Caixa
 */
@Mapper(componentModel = "cdi")
public interface ContaBancariaMapper {
    ContaBancaria toDomain(ContaBancariaEntity entity);
    ContaBancariaEntity toEntity(ContaBancaria domain);
    List<ContaBancaria> toDomainList(List<ContaBancariaEntity> entities);
    void updateEntityFromDomain(ContaBancaria domain, @MappingTarget ContaBancariaEntity entity);
}
