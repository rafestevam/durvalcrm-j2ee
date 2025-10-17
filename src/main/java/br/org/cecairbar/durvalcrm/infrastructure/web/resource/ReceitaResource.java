package br.org.cecairbar.durvalcrm.infrastructure.web.resource;

import br.org.cecairbar.durvalcrm.application.dto.ReceitaDTO;
import br.org.cecairbar.durvalcrm.application.usecase.financeiro.ReceitaUseCase;
import br.org.cecairbar.durvalcrm.domain.model.TipoReceita;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@ApplicationScoped
@Path("/receitas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReceitaResource {

    @Inject
    ReceitaUseCase receitaUseCase;

    @GET
    public Response listarTodas() {
        return Response.ok(receitaUseCase.listarTodas()).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") UUID id) {
        try {
            ReceitaDTO receita = receitaUseCase.buscarPorId(id);
            return Response.ok(receita).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(e.getMessage())).build();
        }
    }

    @GET
    @Path("/periodo")
    public Response listarPorPeriodo(
            @QueryParam("inicio") String inicioStr,
            @QueryParam("fim") String fimStr) {
        try {
            LocalDate inicio = LocalDate.parse(inicioStr);
            LocalDate fim = LocalDate.parse(fimStr);
            return Response.ok(receitaUseCase.listarPorPeriodo(inicio, fim)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Formato de data inválido. Use: YYYY-MM-DD")).build();
        }
    }

    @GET
    @Path("/recebimento")
    public Response listarPorDataRecebimento(
            @QueryParam("inicio") String inicioStr,
            @QueryParam("fim") String fimStr) {
        try {
            LocalDate inicio = LocalDate.parse(inicioStr);
            LocalDate fim = LocalDate.parse(fimStr);
            return Response.ok(receitaUseCase.listarPorDataRecebimento(inicio, fim)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Formato de data inválido. Use: YYYY-MM-DD")).build();
        }
    }

    @GET
    @Path("/tipo/{tipo}")
    public Response listarPorTipo(@PathParam("tipo") String tipoStr) {
        try {
            TipoReceita tipo = TipoReceita.valueOf(tipoStr.toUpperCase());
            return Response.ok(receitaUseCase.listarPorTipo(tipo)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Tipo inválido: " + tipoStr)).build();
        }
    }

    @GET
    @Path("/tipo/{tipo}/periodo")
    public Response listarPorTipoEPeriodo(
            @PathParam("tipo") String tipoStr,
            @QueryParam("inicio") String inicioStr,
            @QueryParam("fim") String fimStr) {
        try {
            TipoReceita tipo = TipoReceita.valueOf(tipoStr.toUpperCase());
            LocalDate inicio = LocalDate.parse(inicioStr);
            LocalDate fim = LocalDate.parse(fimStr);
            return Response.ok(receitaUseCase.listarPorTipoEPeriodo(tipo, inicio, fim)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Tipo inválido: " + tipoStr)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Formato de data inválido. Use: YYYY-MM-DD")).build();
        }
    }

    @GET
    @Path("/categoria/{categoriaId}")
    public Response listarPorCategoria(@PathParam("categoriaId") UUID categoriaId) {
        try {
            return Response.ok(receitaUseCase.listarPorCategoria(categoriaId)).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(e.getMessage())).build();
        }
    }

    @GET
    @Path("/categoria/{categoriaId}/periodo")
    public Response listarPorCategoriaEPeriodo(
            @PathParam("categoriaId") UUID categoriaId,
            @QueryParam("inicio") String inicioStr,
            @QueryParam("fim") String fimStr) {
        try {
            LocalDate inicio = LocalDate.parse(inicioStr);
            LocalDate fim = LocalDate.parse(fimStr);
            return Response.ok(receitaUseCase.listarPorCategoriaEPeriodo(categoriaId, inicio, fim)).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(e.getMessage())).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Formato de data inválido. Use: YYYY-MM-DD")).build();
        }
    }

    @GET
    @Path("/associado/{associadoId}")
    public Response listarPorAssociado(@PathParam("associadoId") UUID associadoId) {
        try {
            return Response.ok(receitaUseCase.listarPorAssociado(associadoId)).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(e.getMessage())).build();
        }
    }

    @GET
    @Path("/soma/periodo")
    public Response somarPorPeriodo(
            @QueryParam("inicio") String inicioStr,
            @QueryParam("fim") String fimStr) {
        try {
            LocalDate inicio = LocalDate.parse(inicioStr);
            LocalDate fim = LocalDate.parse(fimStr);
            BigDecimal total = receitaUseCase.somarPorPeriodo(inicio, fim);
            return Response.ok(new TotalResponse(total)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Formato de data inválido. Use: YYYY-MM-DD")).build();
        }
    }

    @GET
    @Path("/soma/categoria/{categoriaId}")
    public Response somarPorCategoriaEPeriodo(
            @PathParam("categoriaId") UUID categoriaId,
            @QueryParam("inicio") String inicioStr,
            @QueryParam("fim") String fimStr) {
        try {
            LocalDate inicio = LocalDate.parse(inicioStr);
            LocalDate fim = LocalDate.parse(fimStr);
            BigDecimal total = receitaUseCase.somarPorCategoriaEPeriodo(categoriaId, inicio, fim);
            return Response.ok(new TotalResponse(total)).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(e.getMessage())).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Formato de data inválido. Use: YYYY-MM-DD")).build();
        }
    }

    @GET
    @Path("/soma/tipo/{tipo}")
    public Response somarPorTipoEPeriodo(
            @PathParam("tipo") String tipoStr,
            @QueryParam("inicio") String inicioStr,
            @QueryParam("fim") String fimStr) {
        try {
            TipoReceita tipo = TipoReceita.valueOf(tipoStr.toUpperCase());
            LocalDate inicio = LocalDate.parse(inicioStr);
            LocalDate fim = LocalDate.parse(fimStr);
            BigDecimal total = receitaUseCase.somarPorTipoEPeriodo(tipo, inicio, fim);
            return Response.ok(new TotalResponse(total)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Tipo inválido: " + tipoStr)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Formato de data inválido. Use: YYYY-MM-DD")).build();
        }
    }

    @GET
    @Path("/soma/associado/{associadoId}")
    public Response somarPorAssociadoEPeriodo(
            @PathParam("associadoId") UUID associadoId,
            @QueryParam("inicio") String inicioStr,
            @QueryParam("fim") String fimStr) {
        try {
            LocalDate inicio = LocalDate.parse(inicioStr);
            LocalDate fim = LocalDate.parse(fimStr);
            BigDecimal total = receitaUseCase.somarPorAssociadoEPeriodo(associadoId, inicio, fim);
            return Response.ok(new TotalResponse(total)).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(e.getMessage())).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Formato de data inválido. Use: YYYY-MM-DD")).build();
        }
    }

    @POST
    public Response criar(@Valid ReceitaDTO dto) {
        try {
            ReceitaDTO receita = receitaUseCase.criar(dto);
            return Response.status(Response.Status.CREATED).entity(receita).build();
        } catch (BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage())).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response atualizar(@PathParam("id") UUID id, @Valid ReceitaDTO dto) {
        try {
            ReceitaDTO receita = receitaUseCase.atualizar(id, dto);
            return Response.ok(receita).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(e.getMessage())).build();
        } catch (BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage())).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deletar(@PathParam("id") UUID id) {
        try {
            receitaUseCase.deletar(id);
            return Response.noContent().build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(e.getMessage())).build();
        }
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

    public static class TotalResponse {
        private BigDecimal total;

        public TotalResponse(BigDecimal total) {
            this.total = total;
        }

        public BigDecimal getTotal() {
            return total;
        }

        public void setTotal(BigDecimal total) {
            this.total = total;
        }
    }
}
