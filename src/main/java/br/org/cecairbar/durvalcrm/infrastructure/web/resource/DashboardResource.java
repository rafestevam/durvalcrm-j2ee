package br.org.cecairbar.durvalcrm.infrastructure.web.resource;

import br.org.cecairbar.durvalcrm.application.dto.DashboardDTO;
import br.org.cecairbar.durvalcrm.application.usecase.dashboard.DashboardUseCase;
import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;

@ApplicationScoped
@Path("/dashboard")
@Produces(MediaType.APPLICATION_JSON)
public class DashboardResource {
    
    @Inject
    DashboardUseCase dashboardUseCase;
    
    @GET
    public Response obterDashboard(
            @QueryParam("mes") Integer mes,
            @QueryParam("ano") Integer ano) {
        
        // Se não informado, usar mês/ano atual
        if (mes == null || ano == null) {
            LocalDate hoje = LocalDate.now();
            mes = mes != null ? mes : hoje.getMonthValue();
            ano = ano != null ? ano : hoje.getYear();
        }
        
        // Validar parâmetros
        if (mes < 1 || mes > 12) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Mês deve estar entre 1 e 12")
                    .build();
        }
        
        if (ano < 2000 || ano > 2100) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Ano inválido")
                    .build();
        }
        
        DashboardDTO dashboard = dashboardUseCase.obterDashboard(mes, ano);
        return Response.ok(dashboard).build();
    }
    
    @GET
    @ApplicationScoped
@Path("/receitas-por-metodo-pagamento")
    public Response obterReceitasPorMetodoPagamento() {
        var receitas = dashboardUseCase.obterReceitasPorMetodoPagamento();
        return Response.ok(receitas).build();
    }
}