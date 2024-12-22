package com.fmoraes.contactsapp.contact.service;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class ArchiveProcessInfo {

    private final AtomicInteger progress = new AtomicInteger(0);
    private Status status = Status.RUNNING;
    private Future<String> future;

    public int getProgress() {
        return progress.intValue();
    }

    public void setProgress(int progress) {
        this.progress.set(progress);
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }


    public Future<String> getFuture() {
        return future;
    }

    public void setFuture(Future<String> future) {
        this.future = future;

    }

    public enum Status {
        RUNNING,
        COMPLETE,
        FAILED
    }
}