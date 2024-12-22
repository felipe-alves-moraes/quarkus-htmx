package com.fmoraes.ssedemo;

import com.fmoraes.ssedemo.SseBroker.ProgressEvent;
import io.quarkiverse.renarde.htmx.HxController;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.smallrye.mutiny.Multi;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.OutboundSseEvent;
import jakarta.ws.rs.sse.Sse;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestStreamElementType;
import org.jboss.resteasy.reactive.multipart.FileUpload;

@Path("/")
public class HomeController extends HxController {

    private static final Logger LOG = Logger.getLogger(HomeController.class);

    private final FileProcessor fileProcessor;
    private final SseBroker broker;
    @Inject
    Sse sse;

    public HomeController(final FileProcessor fileProcessor, final SseBroker broker) {
        this.fileProcessor = fileProcessor;
        this.broker = broker;
    }

    @CheckedTemplate
    public static class Templates {

        public static native TemplateInstance index();

    }

    @Path("/")
    @GET
    public TemplateInstance index() {
        return Templates.index();
    }

    @Path("/")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public void runImport(@FormParam("file") FileUpload part) throws IOException {
        fileProcessor.process(new FileInputStream(part.uploadedFile().toFile()));
    }

    @Path("/progress")
    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @RestStreamElementType(MediaType.TEXT_PLAIN)
    public Multi<OutboundSseEvent> progress() {
        final var heartbeat = Multi.createFrom().ticks().every(Duration.ofSeconds(5))
            .map(it -> sse.newEvent("heartbeat"));
        final var updates = broker.subscribeToUpdates();
        final var progressEvents = updates
            .onItem().transformToMultiAndMerge(
                events -> Multi.createFrom().items(createLogEvents(events), createProgressEvents(events))
                    .filter(Objects::nonNull))
            .onSubscription()
            .invoke(subscription -> LOG.debugf("Subscriptions: %s", subscription))
            .onCancellation()
            .invoke(() -> LOG.debug("Cancel"))
            .onFailure()
            .invoke(throwable -> LOG.debug(throwable.getMessage(), throwable));

        return Multi.createBy().merging().streams(heartbeat, progressEvents);
    }

    private OutboundSseEvent createLogEvents(final List<ProgressEvent> events) {
        return sse.newEvent("log-event",
            events.stream()
            .map(progressEvent -> "<div>%s</div>".formatted(replaceNewLines(progressEvent.message())))
            .collect(Collectors.joining())
        );
    }

    private OutboundSseEvent createProgressEvents(final List<ProgressEvent> events) {
        return sse.newEvent("progress-event",
            events.stream()
                .max(Comparator.comparing(progressEvent -> progressEvent.progress().value()))
                .map(progressEvent -> "<progress class=\"progress w-full h-6\" value=\"%d\" max=\"100\"></progress>".formatted((int) (progressEvent.progress().value() * 100)))
                .orElse(null)
        );
    }

    private String replaceNewLines(String message) {
        return message.replaceAll("\n", "<br>");
    }
}
