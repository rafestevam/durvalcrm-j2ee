package br.org.cecairbar.durvalcrm.infrastructure.web.resource;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@Path("/health")
@Produces(MediaType.APPLICATION_JSON)
public class HealthResource {

    @GET
    @Path("/api/status")
    public Response getStatus() {
        Map<String, Object> status = Map.of(
            "status", "UP",
            "service", "DurvalCRM API",
            "timestamp", System.currentTimeMillis()
        );
        return Response.ok(status).build();
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