package com.alien.utils.core.assertion.convertor;

import java.io.IOException;

import com.alien.utils.core.assertion.exception.AcceptanceTestException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class Json {

    public String convertToString(final Object sourceObject) {

        final ObjectWriter writer = new ObjectMapper().writer().withDefaultPrettyPrinter();
        try {
            return writer.writeValueAsString(sourceObject);
        } catch (Exception e) {
            throw new AcceptanceTestException(e);
        }

    }

    public <T> T convertToObject(final String json, final Class<T> clz) {

        final ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json.getBytes(), clz);
        } catch (IOException e) {
            throw new AcceptanceTestException(e);
        }

    }

}
