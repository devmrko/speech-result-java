package com.oracle.fn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;

@Component
public class RestAPIHelper {

	private static final Logger logger = LoggerFactory.getLogger(RestAPIHelper.class);

	public int callPostAPI(URL url, HttpURLConnection connection, Object object) {
		Gson gson = new Gson();
		String payload = gson.toJson(object);
		logger.debug("payload: {}", payload);

		try (OutputStream os = connection.getOutputStream()) {
			byte[] input = payload.getBytes("utf-8");
			os.write(input, 0, input.length);

			int responseCode = connection.getResponseCode();
			logger.info("responseCode: {}", responseCode);

			try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
				StringBuilder sb = new StringBuilder();
				String responseLine;
				while ((responseLine = br.readLine()) != null) {
					sb.append(responseLine.trim());
				}
				logger.info("response: {}", sb.toString());
				return responseCode;
			}

		} catch (IOException e1) {
			e1.printStackTrace();
			logger.error("getOutputStream error: {}", e1.getMessage());
			return 500;
		}

	}
	
	public String callPostForObject(URI uri, HttpHeaders headers, String object) {
		
		RestTemplate restTemplate = new RestTemplate();
        HttpEntity<Object> requestEntity = new HttpEntity<>(object, headers);
        String result = restTemplate.postForObject(uri, requestEntity, String.class);

		if (result != null) {
			logger.info("response: {}", result);
		}
		Gson gson = new Gson();
		return gson.toJson(result);
		
	}

}
