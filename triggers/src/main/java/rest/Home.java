package rest;

import io.quarkiverse.renarde.htmx.HxController;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import java.time.LocalTime;

public class Home extends HxController {
    
    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance index();
        public static native TemplateInstance fragments$message(String currentTime);
    }

    @Path("/")
    public TemplateInstance index() {
        return Templates.index();
    }

    @Path("/htmx")
    public TemplateInstance htmx(@QueryParam("delay") Integer delay) throws InterruptedException {
        onlyHxRequest();
        if (delay != null) {
            Thread.sleep(delay * 1000L);
        }

        return Templates.fragments$message(LocalTime.now().toString());
    }

}