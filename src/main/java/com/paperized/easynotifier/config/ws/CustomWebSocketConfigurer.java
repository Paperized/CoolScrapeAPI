package com.paperized.easynotifier.config.ws;

import com.paperized.easynotifier.controller.ws.WSController;
import com.paperized.easynotifier.service.WsListeningHolderService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@EnableWebSocket
public class CustomWebSocketConfigurer implements WebSocketConfigurer {
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final WSController controller;
    private final WsListeningHolderService wsListeningHolderService;

    public CustomWebSocketConfigurer(WSController controller, WsListeningHolderService wsListeningHolderService) {
        this.controller = controller;
        this.wsListeningHolderService = wsListeningHolderService;
    }

    @Bean
    public Map<String, WebSocketSession> getSessions() {
        return sessions;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new CustomWebSocketHandler(sessions, controller, wsListeningHolderService), "/connection")
                .setHandshakeHandler(new CustomWebSocketHandshake())
                .setAllowedOrigins("*");
    }


}


