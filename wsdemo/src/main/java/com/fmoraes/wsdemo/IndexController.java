package com.fmoraes.wsdemo;

import io.quarkiverse.renarde.htmx.HxController;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/")
public class IndexController extends HxController {

    @CheckedTemplate
    static class Templates {
        public static native TemplateInstance index();
    }

    @Path("/")
    @GET
    public TemplateInstance index() {
        return Templates.index();
    }
}
