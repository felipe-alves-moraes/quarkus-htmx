package com.fmoraes.wsdemo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.qute.Engine;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jboss.logging.Logger;

@ServerEndpoint("/chatroom")
@ApplicationScoped
public class SocketHandler {

    private static final Logger LOG = Logger.getLogger(SocketHandler.class);
    private List<Session> sessions = new CopyOnWriteArrayList<>();

    private final ObjectMapper objectMapper;
    private final Engine engine;

    public SocketHandler(final ObjectMapper objectMapper, final Engine engine) {
        this.objectMapper = objectMapper;
        this.engine = engine;
    }

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
    }

    @OnMessage
    public void onMessage(Session session, String message) throws JsonProcessingException {
        final Map<String, Object> value = objectMapper.readValue(message, new TypeReference<>() {
        });

        final var userMessage = (String) value.get("chatMessage");
        sendMessageToWebSocketSession(session, userMessage);
    }

    private void sendMessageToWebSocketSession(final Session currentSession, final String message) {
        for (var session : sessions) {
            if (!session.isOpen()) {
                continue;
            }
            if (session.equals(currentSession)) {
                session.getAsyncRemote().sendText(userMessageHtml(message), result -> {
                    if (result.getException() != null) {
                        LOG.error("Unable to send message to " + session, result.getException());
                    }
                });
            } else {
                session.getAsyncRemote().sendText(incomingMessageHtml(message), result -> {
                    if (result.getException() != null) {
                        LOG.error("Unable to send message to " + session, result.getException());
                    }
                });
            }
        }
    }

    private String userMessageHtml(String message) {
        return engine.getTemplate("IndexController/fragments").getFragment("userMessage").data("message", message)
            .render();
    }

    private String incomingMessageHtml(String message) {
        return engine.getTemplate("IndexController/fragments").getFragment("incomingMessage").data("message", message)
            .render();
    }
}
