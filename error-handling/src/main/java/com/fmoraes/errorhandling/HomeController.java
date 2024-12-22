package com.fmoraes.errorhandling;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

@ApplicationScoped
@Path("/")
public class HomeController {

    private final NumbersApiGateway numbersApiGateway;

    public HomeController(final NumbersApiGateway numbersApiGateway) {
        this.numbersApiGateway = numbersApiGateway;
    }

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance index();
        public static native TemplateInstance fragments$result(String fact);
    }

    @GET
    @Path("/")
    public TemplateInstance index() {
        return Templates.index();
    }

    @GET
    @Path("/number-facts")
    public TemplateInstance getRandomNumberFact(@QueryParam("number") Integer number) {
        return Templates.fragments$result(numbersApiGateway.getFact(number));
    }
}
