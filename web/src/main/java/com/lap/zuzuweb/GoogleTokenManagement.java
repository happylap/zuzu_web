package com.lap.zuzuweb;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.lap.zuzuweb.util.CrunchifyInMemoryCache;

public class GoogleTokenManagement {

	private static final Logger logger = LoggerFactory.getLogger(FacebookTokenManagement.class);

	private CrunchifyInMemoryCache<String, Boolean> cache = new CrunchifyInMemoryCache<String, Boolean>(1800, 1800, 300);
	
	GoogleIdTokenVerifier idv;

	public GoogleTokenManagement() {
		HttpTransport httpTransport = null;
		JsonFactory jsonFactory = null;
		try {
			// Set up the HTTP transport and JSON factory
			httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			jsonFactory = JacksonFactory.getDefaultInstance();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		idv = new GoogleIdTokenVerifier.Builder(httpTransport, jsonFactory).setIssuer("https://accounts.google.com")
				.build();
	}

	public GoogleIdToken getTokenInfo(String accessToken) {
		try {
			return idv.verify(accessToken);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	public boolean isValid(String accessToken) {
		if (StringUtils.isBlank(accessToken)) {
			return false;
		}
		return this.getTokenInfo(accessToken) != null;
	}
	
	public boolean isValid(String accessToken, boolean useCache) {	
		if (useCache && cache.get(accessToken) != null) {
			logger.info("found valid google token in cache.");
			return true;
		}
		
		boolean isValid = this.isValid(accessToken);
		
		if (useCache && isValid) {
			logger.info("put valid google token to cache.");
			cache.put(accessToken, isValid);
		}
		
		return isValid;
	}

	public Payload getUserInfo(String accessToken) {
		GoogleIdToken tokenInfo = this.getTokenInfo(accessToken);

		return tokenInfo != null ? tokenInfo.getPayload() : null;
	}

	public String getEmail(String accessToken) {
		Payload payload = this.getUserInfo(accessToken);
		return payload != null ? payload.getEmail() : null;
	}

}
