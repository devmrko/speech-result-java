package com.oracle.fn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.springframework.stereotype.Component;

import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.auth.ResourcePrincipalAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.requests.GetObjectRequest;
import com.oracle.bmc.objectstorage.responses.GetObjectResponse;

@Component
public class OciSdkHelper {

	public AuthenticationDetailsProvider getAuthenticationDetailsProvider(String configurationFilePath, String profile)
			throws IOException {
		final ConfigFileReader.ConfigFile configFile = ConfigFileReader.parse(configurationFilePath, profile);
		return new ConfigFileAuthenticationDetailsProvider(configFile);
	}

	public ResourcePrincipalAuthenticationDetailsProvider getResourcePrincipalAuthenticationDetailsProvider() {
		return ResourcePrincipalAuthenticationDetailsProvider.builder().build();
	}

	public String getObjectStorageObject(AbstractAuthenticationDetailsProvider p, String compartmentId,
			String namespaceName, String bucketName, String fileName) throws UnsupportedEncodingException, IOException {

		ObjectStorageClient client = ObjectStorageClient.builder().build(p);

		GetObjectRequest getObjectRequest = GetObjectRequest.builder().namespaceName(namespaceName)
				.bucketName(bucketName).objectName(fileName).build();

		GetObjectResponse response = client.getObject(getObjectRequest);

		try (BufferedReader br = new BufferedReader(new InputStreamReader(response.getInputStream(), "utf-8"))) {
			StringBuilder sb = new StringBuilder();
			String responseLine;
			while ((responseLine = br.readLine()) != null) {
				sb.append(responseLine.trim());
			}
			return sb.toString();
		}
		
	}
	
}
