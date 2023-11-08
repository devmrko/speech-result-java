package com.oracle.fn;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.oracle.bmc.auth.AuthenticationDetailsProvider;

@Configuration
@ComponentScan(basePackages = "com.oracle.fn")
public class SpeechResultTest {

	private static final Logger logger = LoggerFactory.getLogger(SpeechResultTest.class);

	public static void main(String[] args) throws IOException, InterruptedException {

		try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
				SpeechResultTest.class)) {

			OciSdkHelper ociSdkHelper = context.getBean(OciSdkHelper.class);
			RestAPIHelper restAPIHelper = context.getBean(RestAPIHelper.class);

			Resource resource = new ClassPathResource("application.properties");
			Properties properties;
			properties = PropertiesLoaderUtils.loadProperties(resource);

			String configurationFilePath = properties.getProperty("configurationFilePath");
			String profile = properties.getProperty("profile");
			String namespaceName = properties.getProperty("namespaceName");
			String bucketName = properties.getProperty("bucketName");
			String compartmentId = properties.getProperty("compartmentId");
			String urlStr = properties.getProperty("speech.post.api.url");

			//String input = "{\"eventType\":\"com.oraclecloud.objectstorage.createobject\",\"cloudEventsVersion\":\"0.1\",\"eventTypeVersion\":\"2.0\",\"source\":\"ObjectStorage\",\"eventTime\":\"2023-11-06T14:39:49Z\",\"contentType\":\"application/json\",\"data\":{\"compartmentId\":\"ocid1.tenancy.oc1..aaaaaaaa2wpfjruuzputbhuzhz2cbwnizjgdyg5q2xqf6nlzm66wayav7a2a\",\"compartmentName\":\"joungminkoaws\",\"resourceName\":\"PXL_20230704_015632626.jpg\",\"resourceId\":\"/n/idyhsdamac8c/b/test/o/PXL_20230704_015632626.jpg\",\"availabilityDomain\":\"IAD-AD-2\",\"additionalDetails\":{\"bucketName\":\"test\",\"versionId\":\"6d5fa574-713a-40f0-ab4c-517d3cca8afe\",\"archivalState\":\"Available\",\"namespace\":\"idyhsdamac8c\",\"bucketId\":\"ocid1.bucket.oc1.iad.aaaaaaaapiej32u3nbo7qgpm6uikkq3phbonb3wq6ugyrmm7l5xasaijscmq\",\"eTag\":\"51ce4324-653e-4dcf-ab3e-5ec1bd4dbd9f\"}},\"eventID\":\"3291a385-fc5b-dda4-ac2a-6eadff71d112\",\"extensions\":{\"compartmentId\":\"ocid1.tenancy.oc1..aaaaaaaa2wpfjruuzputbhuzhz2cbwnizjgdyg5q2xqf6nlzm66wayav7a2a\"}}";

			String fileName = "result/job-amaaaaaawpphycial5u4ch2alpbyvfwymg43pgwvtcmhnjs4s3b6ui7ey5yq/idyhsdamac8c_test-speech_eng_m1.wav.json";

			AuthenticationDetailsProvider p = ociSdkHelper.getAuthenticationDetailsProvider(configurationFilePath,
					profile);

			String result = ociSdkHelper.getObjectStorageObject(p, compartmentId, namespaceName, bucketName, fileName);

			logger.info("{}", result);

			URI uri = new URI(urlStr);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("FILE_NM", fileName);
			headers.add("REQUEST_ID", fileName.split("___")[0]);
			restAPIHelper.callPostForObject(uri, headers, result);

		} catch (BeansException e) {
			e.printStackTrace();

		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

	}

}
