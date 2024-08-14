package com.paperized.easynotifier.service.impl;

import com.paperized.easynotifier.service.WsListeningHolderService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bean as singleton in config files, concurrent data strucuters.
 */
public class WsListeningHolderServiceImpl implements WsListeningHolderService {
    // TODO: currently I can have more users in the same list, to fix
    private final ConcurrentHashMap<String, List<WebSocketSession>> trackerIdToWsListeners = new ConcurrentHashMap<>();

    @Override
    public void removeTrackerId(String trackerId) {
        trackerIdToWsListeners.remove(trackerId);
    }

    @Override
    public void addWsUserToTrackerId(WebSocketSession userSession, String trackerId) {
        trackerIdToWsListeners.compute(trackerId, (id, curr) -> {
            if (curr == null) {
                List<WebSocketSession> newList = new ArrayList<>();
                newList.add(userSession);

                curr = Collections.synchronizedList(newList);
            } else {
                curr.add(userSession);
            }

            return curr;
        });
    }

    @Override
    public void removeWsUserFromTrackerId(String user, String trackerId) {
        trackerIdToWsListeners.compute(trackerId, (id, curr) -> {
            if (curr != null) {
                WebSocketSession userSession = curr.stream()
                        .filter(x -> x.getPrincipal().getName().equals(user))
                        .findFirst()
                        .orElse(null);

                if(userSession != null) {
                    curr.remove(userSession);
                }
            }

            return curr;
        });
    }

    @Override
    public List<WebSocketSession> getWsUsersListeningToTrackerId(String trackerId) {
        return List.copyOf(CollectionUtils.emptyIfNull(trackerIdToWsListeners.get(trackerId)));
    }
}