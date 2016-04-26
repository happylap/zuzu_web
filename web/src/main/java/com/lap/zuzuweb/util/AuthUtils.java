package com.lap.zuzuweb.util;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Arrays;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.lap.zuzuweb.Secrets;

public class AuthUtils {

	private static final Logger logger = LoggerFactory.getLogger(AuthUtils.class);
	
	private static CrunchifyInMemoryCache<String, Boolean> validTokenCache = new CrunchifyInMemoryCache<String, Boolean>(1800, 1800, 300);
	
	public static CrunchifyInMemoryCache<String, Boolean> getValidTokenCache() {
		return validTokenCache;
	}
	
	public static boolean isSuperTokenValid(String token) {
		return StringUtils.equals(Secrets.SUPER_TOKEN, token);
	}

	public static boolean isBasicTokenValid(String token) {
		return StringUtils.equals(Secrets.BASIC_TOKEN, token);
	}
	
	public static boolean isPurchaseReceiptValid(InputStream purchaseReceipt) throws Exception {
		String url_prod = "https://buy.itunes.apple.com/verifyReceipt";
		String url_sandbox = "https://sandbox.itunes.apple.com/verifyReceipt";

		StringEntity se = new StringEntity(IOUtils.toString(purchaseReceipt));
		se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

		String jsonString = HttpUtils.post(url_prod, se);

		ObjectMapper mapper = new ObjectMapper();
		JsonNode actualObj = mapper.readTree(jsonString);

		if (actualObj != null) {
			JsonNode jsonNode_status = actualObj.get("status");

			// This receipt is from the test environment, but it was sent to the
			// production environment for verification.
			if (jsonNode_status != null && jsonNode_status.intValue() == 21007) {

				jsonString = HttpUtils.post(url_sandbox, se);
				actualObj = mapper.readTree(jsonString);

				if (actualObj != null) {
					jsonNode_status = actualObj.get("status");
				}
			}

			// 0 if the receipt is valid
			if (jsonNode_status != null && jsonNode_status.intValue() == 0) {
				return true;
			}
		}

		return false;
	}

	
	
	public static boolean isGoogleTokenValid(String idTokenString) throws Exception {
		if (StringUtils.isBlank(idTokenString)) {
			return false;
		}
		
		if (validTokenCache.get(idTokenString) != null && validTokenCache.get(idTokenString) == true) {
			logger.info("Google token is valid in cache.");
			return true;
		}
		
		// Set up the HTTP transport and JSON factory
		HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

		GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(httpTransport, jsonFactory)
				.setIssuer("https://accounts.google.com").build();

		GoogleIdToken idToken = verifier.verify(idTokenString);
		if (idToken != null) {
			Payload payload = idToken.getPayload();

			if (CollectionUtils.containsAny(payload.getAudienceAsList(), Arrays.asList(Secrets.GOOGLE_CLIENT_ID))) {
				logger.info("Put valid Google token to cache.");
				validTokenCache.put(idTokenString, true);
				return true;
			}

		} else {
			throw new RuntimeException("Invalid ID token.");
		}

		return false;
	}

	public static boolean isFacebookTokenValid(String token) throws Exception {
		if (StringUtils.isBlank(token)) {
			return false;
		}
		
		if (validTokenCache.get(token) != null && validTokenCache.get(token) == true) {
			logger.info("FB token is valid in cache.");
			return true;
		}
		
		String url = String.format("https://graph.facebook.com/debug_token?input_token=%s&access_token=%s", token,
				URLEncoder.encode(AuthUtils.getFacebookAppToken(), "UTF-8"));
		
		String jsonString = HttpUtils.get(url);

		ObjectMapper mapper = new ObjectMapper();
		JsonNode actualObj = mapper.readTree(jsonString);

		if (!actualObj.isNull()) {			
			JsonNode jsonNode_data = actualObj.get("data");
			if (jsonNode_data != null) {
				JsonNode jsonNode_error = jsonNode_data.get("error");
				if (jsonNode_error != null) {
					JsonNode jsonNode_message = jsonNode_error.get("message");
					if (jsonNode_message != null) {
						logger.error("Verify FB Token Url: " + url);
						logger.error("Verify FB Token Error: " + jsonNode_message.textValue());
						throw new RuntimeException(jsonNode_message.textValue());
					}
				}
				
				JsonNode jsonNode_appId = jsonNode_data.get("app_id");

				String _appId = null;
				if (jsonNode_appId != null) {
					_appId = jsonNode_appId.textValue();
				}

				if (StringUtils.equals(Secrets.FACEBOOK_APP_ID, _appId)) {
					logger.info("Put valid FB token to cache.");
					validTokenCache.put(token, true);
					return true;
				}
			}
		}
		
		return false;
	}
	
	public static String getFacebookAppToken() {
		
		String appToken = null;
		try {
			String url = String.format("https://graph.facebook.com/oauth/access_token?client_id=%s&client_secret=%s&grant_type=client_credentials", Secrets.FACEBOOK_APP_ID,
					Secrets.FACEBOOK_APP_SECRET);
			
			String jsonString = HttpUtils.get(url);
			
			if (StringUtils.startsWith(jsonString, "access_token=")) {
				appToken = StringUtils.substringAfter(jsonString, "access_token=");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		return appToken;
		
	}
}
