package br.org.cecairbar.durvalcrm.infrastructure.web;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/api/v1")
public class RestApplication extends Application {
    // JAX-RS will automatically discover and register all @Path annotated classes
    // No explicit configuration needed for basic setup
}