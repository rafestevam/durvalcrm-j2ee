package br.org.cecairbar.durvalcrm.infrastructure.web;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/api")
public class RestApplication extends Application {
    // JAX-RS will automatically discover and register all @Path annotated classes
    // No explicit configuration needed for basic setup
}