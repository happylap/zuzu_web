/**
 * 
 */
package com.lap.zuzuweb.service;

import java.io.InputStream;
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
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.lap.zuzuweb.App;
import com.lap.zuzuweb.Secrets;
import com.lap.zuzuweb.util.HttpUtils;

/**
 * @author eechih
 *
 */
public class AuthServiceImpl implements AuthService {

	private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
	
	private final String GOOGLE_CLIENT_ID = "846012605406-9tnrh80j8kcbcma29omhlsekot2mo0gm.apps.googleusercontent.com";
	private final String FACEBOOK_APP_ID = "1039275546115316";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.lap.zuzuweb.service.AuthService#isSuperTokenValid(java.lang.String)
	 */
	@Override
	public boolean isSuperTokenValid(String token) {
		return StringUtils.equals(Secrets.SUPER_TOKEN, token);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.lap.zuzuweb.service.AuthService#isBasicTokenValid(java.lang.String)
	 */
	@Override
	public boolean isBasicTokenValid(String token) {
		return StringUtils.equals(Secrets.BASIC_TOKEN, token);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lap.zuzuweb.service.AuthService#isPurchaseReceiptValid(java.io.
	 * InputStream)
	 */
	@Override
	public boolean isPurchaseReceiptValid(InputStream purchaseReceipt) throws Exception {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.lap.zuzuweb.service.AuthService#isGoogleTokenValid(java.lang.String)
	 */
	@Override
	public boolean isGoogleTokenValid(String idTokenString) throws Exception {
		// Set up the HTTP transport and JSON factory
		HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

		GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(httpTransport, jsonFactory)
				.setIssuer("https://accounts.google.com").build();

		GoogleIdToken idToken = verifier.verify(idTokenString);
		if (idToken != null) {
			Payload payload = idToken.getPayload();

			if (CollectionUtils.containsAny(payload.getAudienceAsList(), Arrays.asList(GOOGLE_CLIENT_ID))) {
				return true;
			}

		} else {
			throw new RuntimeException("Invalid ID token.");
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lap.zuzuweb.service.AuthService#isFacebookTokenValid(java.lang.
	 * String)
	 */
	@Override
	public boolean isFacebookTokenValid(String token) throws Exception {
		String url = String.format("https://graph.facebook.com/debug_token?input_token=%s&access_token=%s", token,
				token);
		
		String jsonString = HttpUtils.get(url);

		ObjectMapper mapper = new ObjectMapper();
		JsonNode actualObj = mapper.readTree(jsonString);

		if (!actualObj.isNull()) {
			JsonNode jsonNode_error = actualObj.get("error");
			if (jsonNode_error != null) {
				JsonNode jsonNode_message = jsonNode_error.get("message");
				if (jsonNode_message != null) {
					throw new RuntimeException(jsonNode_message.textValue());
				}
			}

			JsonNode jsonNode_data = actualObj.get("data");

			if (jsonNode_data != null) {
				JsonNode jsonNode_appId = jsonNode_data.get("app_id");

				String _appId = null;
				if (jsonNode_appId != null) {
					_appId = jsonNode_appId.textValue();
				}

				if (StringUtils.equals(FACEBOOK_APP_ID, _appId)) {
					return true;
				}
			}
		}
		
		return false;
	}

}
