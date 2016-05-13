package com.lap.zuzuweb.util;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lap.zuzuweb.Secrets;
import com.lap.zuzuweb.ZuzuLogger;

public class AuthUtils {

	private static final ZuzuLogger logger = ZuzuLogger.getLogger(AuthUtils.class);
	
	private static CrunchifyInMemoryCache<String, Boolean> validTokenCache = new CrunchifyInMemoryCache<String, Boolean>(1800, 1800, 300);
	
	public static CrunchifyInMemoryCache<String, Boolean> getValidTokenCache() {
		return validTokenCache;
	}
	
	public static boolean isSuperTokenValid(String token) {
		logger.entering("isSuperTokenValid");
		
		return StringUtils.equals(Secrets.SUPER_TOKEN, token);
	}

	public static boolean isBasicTokenValid(String token) {
		logger.entering("isBasicTokenValid");
	
		return StringUtils.equals(Secrets.BASIC_TOKEN, token);
	}
	
	public static boolean isPurchaseReceiptValid(InputStream purchaseReceipt) throws Exception {
		logger.entering("isPurchaseReceiptValid");
		
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

	
}
