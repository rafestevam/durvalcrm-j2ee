package br.org.cecairbar.durvalcrm.infrastructure.web.resource;

import br.org.cecairbar.durvalcrm.application.dto.CategoriaFinanceiraDTO;
import br.org.cecairbar.durvalcrm.application.usecase.financeiro.CategoriaFinanceiraUseCase;
import br.org.cecairbar.durvalcrm.domain.model.TipoCategoriaFinanceira;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.UUID;

@ApplicationScoped
@Path("/categorias-financeiras")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoriaFinanceiraResource {

    @Inject
    CategoriaFinanceiraUseCase categoriaUseCase;

    @GET
    public Response listarTodas() {
        return Response.ok(categoriaUseCase.listarTodas()).build();
    }

    @GET
    @Path("/ativas")
    public Response listarAtivas() {
        return Response.ok(categoriaUseCase.listarAtivas()).build();
    }

    @GET
    @Path("/tipo/{tipo}")
    public Response listarPorTipo(@PathParam("tipo") String tipoStr) {
        try {
            TipoCategoriaFinanceira tipo = TipoCategoriaFinanceira.valueOf(tipoStr.toUpperCase());
            return Response.ok(categoriaUseCase.listarPorTipo(tipo)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Tipo inválido: " + tipoStr)).build();
        }
    }

    @GET
    @Path("/tipo/{tipo}/ativas")
    public Response listarAtivasPorTipo(@PathParam("tipo") String tipoStr) {
        try {
            TipoCategoriaFinanceira tipo = TipoCategoriaFinanceira.valueOf(tipoStr.toUpperCase());
            return Response.ok(categoriaUseCase.listarAtivasPorTipo(tipo)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Tipo inválido: " + tipoStr)).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") UUID id) {
        try {
            CategoriaFinanceiraDTO categoria = categoriaUseCase.buscarPorId(id);
            return Response.ok(categoria).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(e.getMessage())).build();
        }
    }

    @POST
    public Response criar(@Valid CategoriaFinanceiraDTO dto) {
        try {
            CategoriaFinanceiraDTO categoria = categoriaUseCase.criar(dto);
            return Response.status(Response.Status.CREATED).entity(categoria).build();
        } catch (BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage())).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response atualizar(@PathParam("id") UUID id, @Valid CategoriaFinanceiraDTO dto) {
        try {
            CategoriaFinanceiraDTO categoria = categoriaUseCase.atualizar(id, dto);
            return Response.ok(categoria).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(e.getMessage())).build();
        } catch (BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage())).build();
        }
    }

    @POST
    @Path("/{id}/desativar")
    public Response desativar(@PathParam("id") UUID id) {
        try {
            categoriaUseCase.desativar(id);
            return Response.noContent().build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(e.getMessage())).build();
        } catch (BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage())).build();
        }
    }

    @POST
    @Path("/{id}/reativar")
    public Response reativar(@PathParam("id") UUID id) {
        try {
            CategoriaFinanceiraDTO categoria = categoriaUseCase.reativar(id);
            return Response.ok(categoria).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(e.getMessage())).build();
        } catch (BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage())).build();
        }
    }

    @GET
    @Path("/validar-nome")
    public Response validarNome(@QueryParam("nome") String nome) {
        boolean existe = categoriaUseCase.existeNome(nome);
        return Response.ok(new ValidacaoNomeResponse(existe)).build();
    }

    // Classes auxiliares

    public static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static class ValidacaoNomeResponse {
        private boolean existe;

        public ValidacaoNomeResponse(boolean existe) {
            this.existe = existe;
        }

        public boolean isExiste() {
            return existe;
        }

        public void setExiste(boolean existe) {
            this.existe = existe;
        }
    }
}
