package com.alien.utils.core.json;

import java.io.File;
import java.io.IOException;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class JsonHelper {

	public String format(String jsonText) {
		try {
			
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			JsonParser jsonParser = new JsonParser();
			JsonElement jsonElement = jsonParser.parse(jsonText);
			String jsonString = gson.toJson(jsonElement);
			
			return jsonString;
			
		} catch (JsonSyntaxException e) {
			
			return jsonText;
		}
	}
	
	public <T> T jsonToObject(String json, Class<T> clz) throws JsonParseException, JsonMappingException, IOException {

		final ObjectMapper mapper = new ObjectMapper();

		return mapper.readValue(json, clz);

	}

	public <T> T jsonToObject(File json, Class<T> clz) throws JsonParseException, JsonMappingException, IOException {

		final ObjectMapper mapper = new ObjectMapper();

		return mapper.readValue(json, clz);

	}

	public <T> String objectToJson(T obj) throws JsonParseException, JsonMappingException, IOException {

		final ObjectMapper mapper = new ObjectMapper();

		return mapper.writeValueAsString(obj);

	}	

}
