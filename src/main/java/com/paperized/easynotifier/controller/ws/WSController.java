package com.paperized.easynotifier.controller.ws;

import com.paperized.easynotifier.dto.ErrorResponse;
import com.paperized.easynotifier.service.ProductTrackerService;
import com.paperized.easynotifier.service.WsListeningHolderService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class WSController {
    private final ProductTrackerService productTrackerService;
    private final WsListeningHolderService wsListeningHolderService;

    public WSController(ProductTrackerService productTrackerService, WsListeningHolderService wsListeningHolderService) {
        this.productTrackerService = productTrackerService;
        this.wsListeningHolderService = wsListeningHolderService;
    }

    public ErrorResponse listenToTrackerId(WebSocketSession session, String trackerId) throws Exception {
        ErrorResponse errorResponse = new ErrorResponse();
        if (productTrackerService.existsTrackerIdWithWS(trackerId)) {
            wsListeningHolderService.addWsUserToTrackerId(session, trackerId);

            errorResponse.setHttpStatus(200);
            return errorResponse;
        }

        errorResponse.setHttpStatus(400);
        errorResponse.setErrorCode("NO_WS_ENABLED");
        errorResponse.setErrorDescription("This TrackerID does not allow web socket deliver!");
        return errorResponse;
    }
}
