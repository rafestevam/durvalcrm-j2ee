package br.org.cecairbar.durvalcrm.infrastructure.web.exception;

import java.util.logging.Logger;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Map;
import java.util.stream.Collectors;

@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {

    private static final Logger log = Logger.getLogger(GlobalExceptionHandler.class.getName());

    @Override
    public Response toResponse(Exception exception) {
        log.severe("Erro capturado: " + exception.getMessage());

        if (exception instanceof NotFoundException) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of(
                            "error", "Not Found",
                            "message", exception.getMessage(),
                            "status", 404
                    ))
                    .build();
        }

        if (exception instanceof WebApplicationException) {
            WebApplicationException webEx = (WebApplicationException) exception;
            return Response.status(webEx.getResponse().getStatus())
                    .entity(Map.of(
                            "error", "Application Error",
                            "message", webEx.getMessage(),
                            "status", webEx.getResponse().getStatus()
                    ))
                    .build();
        }

        if (exception instanceof ConstraintViolationException) {
            ConstraintViolationException validationEx = (ConstraintViolationException) exception;
            String violations = validationEx.getConstraintViolations()
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of(
                            "error", "Validation Error",
                            "message", violations,
                            "status", 400
                    ))
                    .build();
        }

        // Erro genérico
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of(
                        "error", "Internal Server Error",
                        "message", "Erro interno do servidor",
                        "status", 500
                ))
                .build();
    }
}