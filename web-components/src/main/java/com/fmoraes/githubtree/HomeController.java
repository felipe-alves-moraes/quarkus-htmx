package com.fmoraes.githubtree;

import io.quarkiverse.renarde.htmx.HxController;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import java.util.List;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

@Path("/")
public class HomeController extends HxController {

    private static final String USERNAME = "wimdeblauwe";

    private final GitHubGateway gateway;

    public HomeController(final GitHubGateway gateway) {
        this.gateway = gateway;
    }

    @CheckedTemplate
    public static class Templates {

        public static native TemplateInstance index();

        public static native TemplateInstance repositories$tree(List<String> repositories);

        public static native TemplateInstance repositories$releases(String repositoryName,
            List<RepositoryRelease> releases);
        public static native TemplateInstance repositories$releaseBody(String releaseBody);
    }

    @Path("/")
    @GET
    public TemplateInstance index() {
        return Templates.index();
    }

    @Path("/repositories")
    @GET
    public TemplateInstance repositoriesTree() {
        onlyHxRequest();
        return Templates.repositories$tree(gateway.getRepositories(USERNAME));
    }

    @Path("/repositories/{id}/releases")
    @GET
    public TemplateInstance repositoryReleaseTree(@PathParam("id") String id) {
        onlyHxRequest();
        final var repositoryReleases = gateway.getRepositoryReleases(USERNAME, id);
        return Templates.repositories$releases(id, repositoryReleases);
    }

    @Path("/repositories/{name}/releases/{id}")
    @GET
    public TemplateInstance repositoryReleaseNotes(@PathParam("name") String name, @PathParam("id") long id) {
        onlyHxRequest();
        final var markdown = gateway.getRepositoryReleases(USERNAME, name, id);
        return Templates.repositories$releaseBody(renderMarkdown(markdown));
    }

    private String renderMarkdown(String markdown) {
        final var parser = Parser.builder().build();
        final var document = parser.parse(markdown);
        final var renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }
}
