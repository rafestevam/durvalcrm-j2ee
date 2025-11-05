package br.org.cecairbar.durvalcrm.infrastructure.web.resource;

import br.org.cecairbar.durvalcrm.application.financeiro.RecebimentoDTO;
import br.org.cecairbar.durvalcrm.application.financeiro.RecebimentoUseCase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.time.LocalDate;
import java.util.UUID;

/**
 * REST Resource para operações de Recebimento.
 *
 * US-061: Registro de Recebimentos por Forma de Pagamento
 */
@ApplicationScoped
@Path("/recebimentos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RecebimentoResource {

    @Inject
    RecebimentoUseCase useCase;

    @GET
    public Response findAll(
            @QueryParam("contaId") UUID contaId,
            @QueryParam("dataInicio") String dataInicio,
            @QueryParam("dataFim") String dataFim) {

        if (contaId != null) {
            return Response.ok(useCase.findByContaId(contaId)).build();
        }

        if (dataInicio != null && dataFim != null) {
            LocalDate inicio = LocalDate.parse(dataInicio);
            LocalDate fim = LocalDate.parse(dataFim);
            return Response.ok(useCase.findByPeriodo(inicio, fim)).build();
        }

        return Response.ok(useCase.findAll()).build();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") UUID id) {
        return Response.ok(useCase.findById(id)).build();
    }

    @POST
    public Response create(RecebimentoDTO dto) {
        RecebimentoDTO created = useCase.create(dto);
        return Response.created(URI.create("/recebimentos/" + created.getId()))
                .entity(created)
                .build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") UUID id) {
        useCase.delete(id);
        return Response.noContent().build();
    }
}
