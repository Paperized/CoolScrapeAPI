package com.paperized.shopapi.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paperized.shopapi.model.TrackingAction;

import java.util.List;

public class AppUtils {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static Object fromJson(String json, TrackingAction trackingAction) throws JsonProcessingException {
        if(trackingAction.isReturnsList()) {
            return MAPPER.readValue(json, MAPPER.getTypeFactory().constructCollectionType(List.class, trackingAction.getClazz()));
        }

        return MAPPER.readValue(json, trackingAction.getClazz());
    }

    public static String toJson(Object obj) throws JsonProcessingException {
        return MAPPER.writeValueAsString(obj);
    }
}
