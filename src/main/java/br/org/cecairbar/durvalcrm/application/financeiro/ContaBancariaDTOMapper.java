package br.org.cecairbar.durvalcrm.application.financeiro;

import br.org.cecairbar.durvalcrm.domain.model.ContaBancaria;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Mapper entre ContaBancaria (domínio) e ContaBancariaDTO (API).
 *
 * US-060: Cadastro de Contas Bancárias e Caixa
 */
@Mapper(componentModel = "cdi")
public interface ContaBancariaDTOMapper {
    ContaBancariaDTO toDTO(ContaBancaria domain);
    ContaBancaria toDomain(ContaBancariaDTO dto);
    List<ContaBancariaDTO> toDTOList(List<ContaBancaria> domains);
}
