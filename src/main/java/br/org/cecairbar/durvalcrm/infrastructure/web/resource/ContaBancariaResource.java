package br.org.cecairbar.durvalcrm.infrastructure.web.resource;

import br.org.cecairbar.durvalcrm.application.financeiro.ContaBancariaDTO;
import br.org.cecairbar.durvalcrm.application.financeiro.ContaBancariaUseCase;
import br.org.cecairbar.durvalcrm.domain.model.StatusConta;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.UUID;

/**
 * REST Resource para operações de Conta Bancária.
 *
 * US-060: Cadastro de Contas Bancárias e Caixa
 */
@ApplicationScoped
@Path("/contas-bancarias")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ContaBancariaResource {

    @Inject
    ContaBancariaUseCase useCase;

    @GET
    public Response findAll(@QueryParam("status") String status) {
        if (status != null && !status.isEmpty()) {
            return Response.ok(useCase.findByStatus(StatusConta.valueOf(status))).build();
        }
        return Response.ok(useCase.findAll()).build();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") UUID id) {
        return Response.ok(useCase.findById(id)).build();
    }

    @POST
    public Response create(ContaBancariaDTO dto) {
        ContaBancariaDTO created = useCase.create(dto);
        return Response.created(URI.create("/contas-bancarias/" + created.getId()))
                .entity(created)
                .build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") UUID id, ContaBancariaDTO dto) {
        return Response.ok(useCase.update(id, dto)).build();
    }

    @PUT
    @Path("/{id}/inativar")
    public Response inativar(@PathParam("id") UUID id) {
        useCase.inativar(id);
        return Response.noContent().build();
    }
}
