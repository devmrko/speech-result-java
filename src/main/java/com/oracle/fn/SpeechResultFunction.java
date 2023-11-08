package com.oracle.fn;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.oracle.bmc.auth.ResourcePrincipalAuthenticationDetailsProvider;

@Configuration
@ComponentScan(basePackages = "com.oracle.fn")
public class SpeechResultFunction {
	
	private static final Logger logger = LoggerFactory.getLogger(SpeechResultFunction.class);

	public String handleRequest(String input) throws IOException {
		
		try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SpeechResultFunction.class)) {

			InputParser inputParser = context.getBean(InputParser.class);
			OciSdkHelper ociSdkHelper = context.getBean(OciSdkHelper.class);
			RestAPIHelper restAPIHelper = context.getBean(RestAPIHelper.class);

			Resource resource = new ClassPathResource("application.properties");
			Properties properties;
			properties = PropertiesLoaderUtils.loadProperties(resource);

//			String configurationFilePath = properties.getProperty("configurationFilePath");
//			String profile = properties.getProperty("profile");
			String namespaceName = properties.getProperty("namespaceName");
			String compartmentId = properties.getProperty("compartmentId");
			String urlStr = properties.getProperty("speech.post.api.url");
			String outBucketName = properties.getProperty("outBucketName");
			
			try {
				
				String fileName;
				fileName = inputParser.getParsingValue(input);
				logger.debug("fileName: {}", fileName);

				ResourcePrincipalAuthenticationDetailsProvider p = ociSdkHelper.getResourcePrincipalAuthenticationDetailsProvider();
				String objectResult = ociSdkHelper.getObjectStorageObject(p, compartmentId, namespaceName, outBucketName, fileName);
				logger.info("{}", objectResult);
				
				URI uri = new URI(urlStr);
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);
				headers.add("FILE_NM", fileName);
				headers.add("REQUEST_ID", fileName.split("___")[0]);
				restAPIHelper.callPostForObject(uri, headers, objectResult);
				
				return objectResult;
		        
			} catch (InputParseException e) {
				e.printStackTrace();
				logger.error("InputParseException error: {}", e.getMessage());
				return "Input parameter parse error";
			} catch (URISyntaxException e) {
				e.printStackTrace();
				logger.error("URISyntaxException error: {}", e.getMessage());
				return "URISyntaxException error";
			}

		} catch (IOException e) {
			e.printStackTrace();
			logger.error("PropertiesLoaderUtils error: {}", e.getMessage());
			return "properties variables load error";
			
		}
	
	}


}