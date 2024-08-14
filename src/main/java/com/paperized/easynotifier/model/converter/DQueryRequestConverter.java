package com.paperized.easynotifier.model.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paperized.easynotifier.model.webhookfilter.DQueryRequestScheduled;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@Converter
public class DQueryRequestConverter implements AttributeConverter<DQueryRequestScheduled, String> {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(DQueryRequestScheduled attribute) {
        if (attribute == null) {
            return null;
        }

        try {
            return MAPPER.writeValueAsString(attribute);
        }
        catch (IOException e) {
            log.error("Could not convert map to json string.");
            return null;
        }
    }

    @Override
    public DQueryRequestScheduled convertToEntityAttribute(String dQueryRequest) {
        try {
            if(dQueryRequest == null)
                return null;

            return MAPPER.readValue(dQueryRequest, DQueryRequestScheduled.class);
        }
        catch (JsonProcessingException e) {
            log.error("Convert error while trying to convert string(JSON) to map data structure.");
            return null;
        }
    }
}
