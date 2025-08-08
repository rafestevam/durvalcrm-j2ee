package br.org.cecairbar.durvalcrm.infrastructure.web.resource;

import br.org.cecairbar.durvalcrm.application.dto.AssociadoDTO;
import br.org.cecairbar.durvalcrm.application.usecase.AssociadoUseCase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.UUID;

@ApplicationScoped
@Path("/associados")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AssociadoResource {

    @Inject
    AssociadoUseCase associadoUseCase;

    @GET
    public Response findAll(@QueryParam("search") String search) {
        return Response.ok(associadoUseCase.findAll(search)).build();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") UUID id) {
        return Response.ok(associadoUseCase.findById(id)).build();
    }

    @POST
    public Response create(@Valid AssociadoDTO associadoDTO) {
        AssociadoDTO created = associadoUseCase.create(associadoDTO);
        return Response.created(URI.create("/associados/" + created.getId())).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") UUID id, @Valid AssociadoDTO associadoDTO) {
        return Response.ok(associadoUseCase.update(id, associadoDTO)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") UUID id) {
        associadoUseCase.delete(id);
        return Response.noContent().build();
    }
}