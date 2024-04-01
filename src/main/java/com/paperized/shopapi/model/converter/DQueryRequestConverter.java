package com.paperized.shopapi.model.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paperized.shopapi.dquery.DQueryRequest;
import com.paperized.shopapi.dto.DQueryRequestWebhook;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Converter
public class DQueryRequestConverter implements AttributeConverter<DQueryRequestWebhook, String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DQueryRequestConverter.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(DQueryRequestWebhook attribute) {
        if (attribute == null) {
            return null;
        }

        try {
            return MAPPER.writeValueAsString(attribute);
        }
        catch (IOException e) {
            LOGGER.error("Could not convert map to json string.");
            return null;
        }
    }

    @Override
    public DQueryRequestWebhook convertToEntityAttribute(String dQueryRequest) {
        try {
            if(dQueryRequest == null)
                return null;

            return MAPPER.readValue(dQueryRequest, DQueryRequestWebhook.class);
        }
        catch (JsonProcessingException e) {
            LOGGER.error("Convert error while trying to convert string(JSON) to map data structure.");
            return null;
        }
    }
}
