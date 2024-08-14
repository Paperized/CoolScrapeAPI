package com.paperized.easynotifier.config.ws;

import com.paperized.easynotifier.controller.ws.WSController;
import com.paperized.easynotifier.dto.ws.WebSocketCommand;
import com.paperized.easynotifier.utils.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;

import static com.paperized.easynotifier.dto.ws.WebSocketCommand.StartListening;
import static com.paperized.easynotifier.dto.ws.WebSocketCommand.Unknown;
import static org.springframework.web.socket.CloseStatus.SERVER_ERROR;

@Slf4j
public class CustomWebSocketHandler extends TextWebSocketHandler {
    private final Map<String, WebSocketSession> sessions;
    private final WSController controller;

    public CustomWebSocketHandler(Map<String, WebSocketSession> sessions, WSController controller) {
        this.sessions = sessions;
        this.controller = controller;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String textMessage = message.getPayload();
        int endOfCommand = textMessage.indexOf(';');

        if (endOfCommand == -1) {
            session.sendMessage(AppUtils.toSocketMessage(
                    Unknown,
                    Map.of("error", "No command found, malformed message!")));
            return;
        }

        WebSocketCommand command = WebSocketCommand.valueOf(textMessage.substring(0, endOfCommand));
        Object result;
        if (StartListening.equals(command)) {
            var input = AppUtils.fromJson(textMessage.substring(endOfCommand + 1), Map.class);
            result = controller.listenToTrackerId(session, input.get("trackerId").toString());
        } else {
            result = Map.of("error", "No command found, malformed message!");
        }

        if (result != null) {
            session.sendMessage(AppUtils.toSocketMessage(command, result));
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        var principal = session.getPrincipal();

        if (principal == null || StringUtils.isBlank(principal.getName())) {
            session.close(SERVER_ERROR.withReason("User must be authenticated"));
            return;
        }

        sessions.put(principal.getName(), session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        var principal = session.getPrincipal();
        if (principal == null || StringUtils.isBlank(principal.getName())) {
            log.warn("Principal is null or principal name is blank during connection closed!");
            return;
        }

        sessions.remove(principal.getName());
    }


}
