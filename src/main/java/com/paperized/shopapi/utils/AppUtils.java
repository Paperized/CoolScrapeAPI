package com.paperized.shopapi.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paperized.shopapi.model.TrackerAction;

import java.util.List;

public class AppUtils {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static Object fromJson(String json, TrackerAction trackerAction) throws JsonProcessingException {
        if(trackerAction.isReturnsList()) {
            return MAPPER.readValue(json, MAPPER.getTypeFactory().constructCollectionType(List.class, trackerAction.getClazz()));
        }

        return MAPPER.readValue(json, trackerAction.getClazz());
    }

    public static String toJson(Object obj) throws JsonProcessingException {
        return MAPPER.writeValueAsString(obj);
    }
}
