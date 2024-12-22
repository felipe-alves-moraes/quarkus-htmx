package com.fmoraes.inlineediting.issue.web;

import com.fmoraes.inlineediting.issue.Status;
import io.quarkus.qute.i18n.Message;
import io.quarkus.qute.i18n.MessageBundle;

@MessageBundle
public interface Messages {
    @Message
    String status(Status status);
}
