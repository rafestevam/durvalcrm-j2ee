package br.org.cecairbar.durvalcrm.infrastructure.web.resource;

import br.org.cecairbar.durvalcrm.application.dto.ResumoFinanceiroDTO;
import br.org.cecairbar.durvalcrm.application.dto.ReceitaPorCategoriaDTO;
import br.org.cecairbar.durvalcrm.application.dto.DespesaPorCategoriaDTO;
import br.org.cecairbar.durvalcrm.application.dto.FluxoCaixaDTO;
import br.org.cecairbar.durvalcrm.application.usecase.financeiro.RelatorioFinanceiroUseCase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
@Path("/relatorios-financeiros")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RelatorioFinanceiroResource {

    @Inject
    RelatorioFinanceiroUseCase relatorioUseCase;

    @GET
    @Path("/resumo")
    public Response obterResumoFinanceiro(
            @QueryParam("inicio") String inicioStr,
            @QueryParam("fim") String fimStr) {
        try {
            LocalDate inicio = LocalDate.parse(inicioStr);
            LocalDate fim = LocalDate.parse(fimStr);
            ResumoFinanceiroDTO resumo = relatorioUseCase.obterResumoFinanceiro(inicio, fim);
            return Response.ok(resumo).build();
        } catch (BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage())).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Formato de data inválido. Use: YYYY-MM-DD")).build();
        }
    }

    @GET
    @Path("/resumo/mes-atual")
    public Response obterResumoMesAtual() {
        ResumoFinanceiroDTO resumo = relatorioUseCase.obterResumoMesAtual();
        return Response.ok(resumo).build();
    }

    @GET
    @Path("/resumo/ano-atual")
    public Response obterResumoAnoAtual() {
        ResumoFinanceiroDTO resumo = relatorioUseCase.obterResumoAnoAtual();
        return Response.ok(resumo).build();
    }

    @GET
    @Path("/receitas/por-categoria")
    public Response obterReceitasPorCategoria(
            @QueryParam("inicio") String inicioStr,
            @QueryParam("fim") String fimStr) {
        try {
            LocalDate inicio = LocalDate.parse(inicioStr);
            LocalDate fim = LocalDate.parse(fimStr);
            List<ReceitaPorCategoriaDTO> relatorio = relatorioUseCase.obterReceitasPorCategoria(inicio, fim);
            return Response.ok(relatorio).build();
        } catch (BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage())).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Formato de data inválido. Use: YYYY-MM-DD")).build();
        }
    }

    @GET
    @Path("/despesas/por-categoria")
    public Response obterDespesasPorCategoria(
            @QueryParam("inicio") String inicioStr,
            @QueryParam("fim") String fimStr) {
        try {
            LocalDate inicio = LocalDate.parse(inicioStr);
            LocalDate fim = LocalDate.parse(fimStr);
            List<DespesaPorCategoriaDTO> relatorio = relatorioUseCase.obterDespesasPorCategoria(inicio, fim);
            return Response.ok(relatorio).build();
        } catch (BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage())).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Formato de data inválido. Use: YYYY-MM-DD")).build();
        }
    }

    @GET
    @Path("/fluxo-caixa")
    public Response obterFluxoDeCaixa(
            @QueryParam("inicio") String inicioStr,
            @QueryParam("fim") String fimStr) {
        try {
            LocalDate inicio = LocalDate.parse(inicioStr);
            LocalDate fim = LocalDate.parse(fimStr);
            FluxoCaixaDTO fluxo = relatorioUseCase.obterFluxoDeCaixa(inicio, fim);
            return Response.ok(fluxo).build();
        } catch (BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage())).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Formato de data inválido. Use: YYYY-MM-DD")).build();
        }
    }

    // Classe auxiliar

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
}
