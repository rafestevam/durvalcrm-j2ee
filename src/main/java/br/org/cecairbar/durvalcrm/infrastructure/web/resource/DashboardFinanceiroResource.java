package br.org.cecairbar.durvalcrm.infrastructure.web.resource;

import br.org.cecairbar.durvalcrm.application.dto.DashboardFinanceiroDTO;
import br.org.cecairbar.durvalcrm.application.dto.ExtratoContaDTO;
import br.org.cecairbar.durvalcrm.application.usecase.financeiro.GetDashboardFinanceiroUseCase;
import br.org.cecairbar.durvalcrm.application.usecase.financeiro.GetExtratoContaUseCase;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.util.UUID;

/**
 * REST endpoint for financial dashboard (US-062, US-064)
 */
@Path("/dashboard-financeiro")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DashboardFinanceiroResource {

    @Inject
    private GetDashboardFinanceiroUseCase getDashboardFinanceiroUseCase;

    @Inject
    private GetExtratoContaUseCase getExtratoContaUseCase;

    /**
     * Get consolidated financial dashboard
     * US-062: Dashboard de Saldos Consolidados
     *
     * @param dataInicio Start date for period (optional, defaults to first day of current month)
     * @param dataFim End date for period (optional, defaults to today)
     * @return Dashboard with consolidated balances and receipts
     */
    @GET
    public Response getDashboard(
            @QueryParam("dataInicio") String dataInicio,
            @QueryParam("dataFim") String dataFim) {

        try {
            LocalDate inicio = (dataInicio != null) ? LocalDate.parse(dataInicio) : null;
            LocalDate fim = (dataFim != null) ? LocalDate.parse(dataFim) : null;

            DashboardFinanceiroDTO dashboard = getDashboardFinanceiroUseCase.execute(inicio, fim);
            return Response.ok(dashboard).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Erro ao buscar dashboard financeiro: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Get detailed account statement
     * US-064: Extrato Detalhado por Conta
     *
     * @param contaId Account ID
     * @param dataInicio Start date for period (optional)
     * @param dataFim End date for period (optional)
     * @return Detailed statement with all transactions
     */
    @GET
    @Path("/extrato/{contaId}")
    public Response getExtrato(
            @PathParam("contaId") String contaId,
            @QueryParam("dataInicio") String dataInicio,
            @QueryParam("dataFim") String dataFim) {

        try {
            UUID uuid = UUID.fromString(contaId);
            LocalDate inicio = (dataInicio != null) ? LocalDate.parse(dataInicio) : null;
            LocalDate fim = (dataFim != null) ? LocalDate.parse(dataFim) : null;

            ExtratoContaDTO extrato = getExtratoContaUseCase.execute(uuid, inicio, fim);
            return Response.ok(extrato).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Erro ao buscar extrato: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Error response DTO
     */
    public static class ErrorResponse {
        private String message;

        public ErrorResponse() {
        }

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
