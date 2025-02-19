package org.orderhub.pr.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.orderhub.pr.util.exception.ExceptionMessage.COVERT_JSON_EXCEPTION_MESSAGE;

@Converter(autoApply = true)
public class HashMapConverter implements AttributeConverter<Map<String, Object>, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        try {
            return attribute == null ? "{}" : objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(COVERT_JSON_EXCEPTION_MESSAGE, e);
        }
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        try {
            return dbData == null ? new HashMap<>() : objectMapper.readValue(dbData, HashMap.class);
        } catch (IOException e) {
            throw new RuntimeException(COVERT_JSON_EXCEPTION_MESSAGE, e);
        }
    }
}
