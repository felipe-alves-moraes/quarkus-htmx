package com.fmoraes.ssedemo;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.helpers.MultiEmitterProcessor;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Duration;
import java.util.List;

@ApplicationScoped
public class SseBroker {

    private final MultiEmitterProcessor<ProgressEvent> eventPublisher;

    private final FileProcessor fileProcessor;
    private ProgressListener progressListener;

    public SseBroker(final FileProcessor fileProcessor) {
        this.fileProcessor = fileProcessor;
        eventPublisher = MultiEmitterProcessor.create();
    }

    @PostConstruct
    void init() {
        progressListener = (progress, message) ->
            eventPublisher.emit(new ProgressEvent(progress, message));
        fileProcessor.addProgressListener(progressListener);
    }

    @PreDestroy
    void destroy() {
        fileProcessor.removeProgressListener(progressListener);
    }

    public Multi<List<ProgressEvent>> subscribeToUpdates() {
        return eventPublisher.toMulti()
            .group().intoLists().every(Duration.ofSeconds(1))
            .broadcast().toAllSubscribers();
    }

    public record ProgressEvent(Progress progress, String message) {

    }
}
