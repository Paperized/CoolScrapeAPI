package com.paperized.shopapi.model.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paperized.shopapi.dquery.DQueryRequest;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Converter
public class DQueryRequestConverter implements AttributeConverter<String, DQueryRequest> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DQueryRequestConverter.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public DQueryRequest convertToDatabaseColumn(String attribute) {
        if (attribute == null) {
            return null;
        }

        try {
            return MAPPER.readValue(attribute, DQueryRequest.class);
        }
        catch (IOException e) {
            LOGGER.error("Convert error while trying to convert string(JSON) to map data structure.");
            return null;
        }
    }

    @Override
    public String convertToEntityAttribute(DQueryRequest dQueryRequest) {
        try {
            if(dQueryRequest == null)
                return null;

            return MAPPER.writeValueAsString(dQueryRequest);
        }
        catch (JsonProcessingException e) {
            LOGGER.error("Could not convert map to json string.");
            return null;
        }
    }
}
