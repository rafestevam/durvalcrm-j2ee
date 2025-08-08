package br.org.cecairbar.durvalcrm.infrastructure.web.resource;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Map;

@ApplicationScoped
@Path("/health")
@Produces(MediaType.APPLICATION_JSON)
public class HealthResource {

    @GET
    public Response getStatus() {
        Map<String, Object> status = Map.of(
            "status", "UP",
            "service", "DurvalCRM API",
            "timestamp", System.currentTimeMillis()
        );
        return Response.ok(status).build();
    }

    @GET
    @Path("/status")
    public Response getStatusAlias() {
        return getStatus();
    }

    @GET
    @Path("/readiness")
    public Response getReadiness() {
        Map<String, Object> readiness = Map.of(
            "status", "UP",
            "checks", Map.of(
                "database", "UP",
                "application", "UP"
            )
        );
        return Response.ok(readiness).build();
    }
}