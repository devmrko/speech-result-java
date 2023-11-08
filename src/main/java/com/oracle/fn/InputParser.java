package com.oracle.fn;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

@Component
public class InputParser {

	private static final Logger logger = LoggerFactory.getLogger(InputParser.class);

	@SuppressWarnings({ "unchecked" })
	public String getParsingValue(String input) throws InputParseException {

		logger.debug(input);

		try {
			if (input != null && input.contains("eventType")) {
				Gson result = new Gson();
				Map<String, Object> resultMap = result.fromJson(input, HashMap.class);

				@SuppressWarnings("rawtypes")
				Map data = (Map) resultMap.get("data");

				String resourceName;
				resourceName = (String) data.get("resourceName");
				logger.info("resourceName: {}", resourceName);
				return resourceName;

			} else {
				return input;

			}

		} catch (Exception e) {
			throw new InputParseException("InputParseException", e);

		}

	}
}
