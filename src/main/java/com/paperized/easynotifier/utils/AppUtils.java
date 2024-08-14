package com.paperized.easynotifier.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paperized.easynotifier.dto.ws.WebSocketCommand;
import com.paperized.easynotifier.model.TrackerAction;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;

@Slf4j
public class AppUtils {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static Object fromJson(String json, TrackerAction trackerAction) throws JsonProcessingException {
        if(trackerAction.isReturnsList()) {
            return MAPPER.readValue(json, MAPPER.getTypeFactory().constructCollectionType(List.class, trackerAction.getClazz()));
        }

        return MAPPER.readValue(json, trackerAction.getClazz());
    }

    public static <T> T fromJson(String json, Class<T> clazz) throws JsonProcessingException {
        return MAPPER.readValue(json, clazz);
    }

    public static <T> List<T> fromJsonList(String json, Class<T> clazz) throws JsonProcessingException {
        return MAPPER.readValue(json, MAPPER.getTypeFactory().constructCollectionType(List.class, clazz));
    }

    public static String toJson(Object obj) throws JsonProcessingException {
        return MAPPER.writeValueAsString(obj);
    }

    public static TextMessage toSocketMessage(WebSocketCommand cmd, Object obj) throws JsonProcessingException {
        if(obj != null) {
            String json = toJson(obj);
            return new TextMessage(cmd.name() + ";" + json);
        }

        return new TextMessage(cmd.name() + ";");
    }

    public static void sendSocketMessageNoThrow(WebSocketSession session, WebSocketMessage<?> message) {
        try {
            session.sendMessage(message);
        } catch (IOException e) {
            log.warn("Error while sending socket message: " + e.getMessage());
        }
    }

    public static void checkWsOrWebhookMandatory(String webhookUrl, Boolean wsEnabled) {
        if(StringUtils.isBlank(webhookUrl) && !Boolean.TRUE.equals(wsEnabled)) {
            throw new RuntimeException("WebhookUrl or WsEnabled are mandatory to start tracking!");
        }
    }
}
