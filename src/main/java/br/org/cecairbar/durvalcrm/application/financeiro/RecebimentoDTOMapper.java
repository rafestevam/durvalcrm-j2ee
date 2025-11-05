package br.org.cecairbar.durvalcrm.application.financeiro;

import br.org.cecairbar.durvalcrm.domain.model.Recebimento;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Mapper entre Recebimento (domínio) e RecebimentoDTO (API).
 *
 * US-061: Registro de Recebimentos por Forma de Pagamento
 */
@Mapper(componentModel = "cdi")
public interface RecebimentoDTOMapper {
    RecebimentoDTO toDTO(Recebimento domain);
    Recebimento toDomain(RecebimentoDTO dto);
    List<RecebimentoDTO> toDTOList(List<Recebimento> domains);
}
