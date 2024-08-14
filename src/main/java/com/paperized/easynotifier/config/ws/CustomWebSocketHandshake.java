package com.paperized.easynotifier.config.ws;

import com.paperized.easynotifier.dto.ws.CustomPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class CustomWebSocketHandshake extends DefaultHandshakeHandler {
    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        Principal principal = super.determineUser(request, wsHandler, attributes);
        if(principal != null) {
            CustomPrincipal customPrincipal = new CustomPrincipal();
            customPrincipal.setName(principal.getName());
            customPrincipal.setAnonymous(false);
            return customPrincipal;
        }

        return new CustomPrincipal(UUID.randomUUID().toString(), true);
    }
}
