package com.fmoraes.ssedemo;

import com.google.common.io.CharStreams;
import com.google.common.io.LineProcessor;
import jakarta.enterprise.context.ApplicationScoped;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class FileProcessor {

    private final Set<ProgressListener> progressListeners = new HashSet<>();

    public void process(InputStream inputStream) throws IOException {
        try (final var baos = new ByteArrayOutputStream()) {
            inputStream.transferTo(baos);
            try (final var firstClone = new ByteArrayInputStream(baos.toByteArray())) {
                final var secondClone = new ByteArrayInputStream(baos.toByteArray());
                final var numberOfLinesTotal = CharStreams.readLines(new InputStreamReader(firstClone)).size();
                CharStreams.readLines(new InputStreamReader(secondClone), new MyLineProcessor(numberOfLinesTotal));
            }
        }
    }

    public void addProgressListener(ProgressListener progressListener) {
        progressListeners.add(progressListener);
    }

    public void removeProgressListener(ProgressListener progressListener) {
        progressListeners.remove(progressListener);
    }

    private static void sleepQuietly() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private class MyLineProcessor implements LineProcessor<Void> {
        private final int numberOfLinesTotal;
        private int numberOfLinesProcessed = 0;

        private MyLineProcessor(final int numberOfLinesTotal) {
            this.numberOfLinesTotal = numberOfLinesTotal;
        }

        @Override
        public boolean processLine(final String line) throws IOException {
            sleepQuietly();
            numberOfLinesProcessed++;
            notifyProgressListeners(line);

            return true;
        }

        @Override
        public Void getResult() {
            return null;
        }

        private void notifyProgressListeners(final String line) {
            for (final var progressListener : progressListeners) {
                final var progress = new Progress(numberOfLinesProcessed / (double) numberOfLinesTotal);
                progressListener.onProgress(progress, "Processing line: " + line);
            }
        }
    }
}
