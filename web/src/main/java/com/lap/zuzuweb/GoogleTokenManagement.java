package com.lap.zuzuweb;

import org.apache.commons.lang3.StringUtils;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.lap.zuzuweb.util.CrunchifyInMemoryCache;

public class GoogleTokenManagement {

	private static final ZuzuLogger logger = ZuzuLogger.getLogger(GoogleTokenManagement.class);

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

	private GoogleIdToken getGoogleIdToken(String accessToken) {
		logger.entering("getGoogleIdToken", "{accessToken: %s}", StringUtils.abbreviateMiddle(accessToken, "...", 15));
		
		try {
			return idv.verify(accessToken);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	private boolean isValid(String accessToken) {
		logger.entering("isValid", "{accessToken: %s}", StringUtils.abbreviateMiddle(accessToken, "...", 15));
		
		if (StringUtils.isBlank(accessToken)) {
			return false;
		}
		return this.getGoogleIdToken(accessToken) != null;
	}
	
	public boolean isValid(String accessToken, boolean useCache) {	
		logger.entering("isValid", "{accessToken: %s, useCache: %s}", StringUtils.abbreviateMiddle(accessToken, "...", 15), useCache);
		
		if (useCache && cache.get(accessToken) != null) {
			logger.info("Found token in cache:: %s", StringUtils.abbreviateMiddle(accessToken, "...", 15));
			return true;
		}
		
		boolean isValid = this.isValid(accessToken);
		
		if (useCache && isValid) {
			logger.info("Put token to cache:: %s", StringUtils.abbreviateMiddle(accessToken, "...", 15));
			cache.put(accessToken, isValid);
		}
		
		return isValid;
	}
	
	private Payload getSocialUserByToken(String accessToken) {
		logger.entering("getSocialUserByToken", "{accessToken: %s}", StringUtils.abbreviateMiddle(accessToken, "...", 15));
		
		GoogleIdToken tokenInfo = this.getGoogleIdToken(accessToken);

		return tokenInfo != null ? tokenInfo.getPayload() : null;
	}

	public String getEmailByToken(String accessToken) {
		logger.entering("getEmailByToken", "{accessToken: %s}", StringUtils.abbreviateMiddle(accessToken, "...", 15));
		
		Payload payload = this.getSocialUserByToken(accessToken);
		return payload != null ? payload.getEmail() : null;
	}

}
