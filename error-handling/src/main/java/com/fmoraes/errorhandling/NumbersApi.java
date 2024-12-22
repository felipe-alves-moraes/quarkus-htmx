package com.fmoraes.errorhandling;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(baseUri = "http://numbersapi.com")
public interface NumbersApi {
    @GET
    @Path("/{number}")
    String getFact(@PathParam("number") int number);

}

