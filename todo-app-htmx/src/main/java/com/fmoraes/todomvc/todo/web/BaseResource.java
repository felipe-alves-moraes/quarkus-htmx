package com.fmoraes.todomvc.todo.web;


import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/")
public class BaseResource {

    @GET
    public Response home() {
        return Response.status(Status.TEMPORARY_REDIRECT)
            .header("Location", "/todo")
            .build();
    }
}