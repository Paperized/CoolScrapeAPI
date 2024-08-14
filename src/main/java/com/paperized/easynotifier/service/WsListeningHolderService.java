package com.paperized.easynotifier.service;

import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.List;

/**
 * OK in my case where I dont need replicas, otherwise consider Redis cache to share these data across all istances
 */
public interface WsListeningHolderService {
    void removeTrackerId(String trackerId);
    void addWsUserToTrackerId(WebSocketSession userSession, String trackerId);
    void removeWsUserFromTrackerId(String user, String trackerId);
    List<WebSocketSession> getWsUsersListeningToTrackerId(String trackerId);
}
