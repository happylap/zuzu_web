package com.lap.zuzuweb.handler.cognito;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.lap.zuzuweb.Configuration;
import com.lap.zuzuweb.ZuzuLogger;
import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.CognitoTokenRequestPayload;
import com.lap.zuzuweb.handler.payload.CognitoTokenResultPayload;
import com.lap.zuzuweb.service.AuthService;

public class CognitoTokenHandler extends AbstractRequestHandler<CognitoTokenRequestPayload> {

	private static final ZuzuLogger logger = ZuzuLogger.getLogger(CognitoTokenHandler.class);

	private AuthService service = null;

	public CognitoTokenHandler(AuthService service) {
		super(CognitoTokenRequestPayload.class);
		this.service = service;
	}

	@Override
	protected Answer processImpl(CognitoTokenRequestPayload value, Map<String, String> urlParams) {
		logger.entering("processImpl", "{value: %s, urlParams: %s}", value, urlParams);
		
		try {
			String userId = value.getUserId();
			String signature = value.getSignature();
			String timestamp = value.getTimestamp();
			String identityId = StringUtils.defaultIfBlank(value.getIdentityId(), null);

			Map<String, String> logins = value.getLogins();

			// build the string to sign
			StringBuilder stringToSign = new StringBuilder();
			stringToSign.append(timestamp);
			if (logins != null && logins.containsKey(Configuration.DEVELOPER_PROVIDER_NAME)) {
				logger.info(String.format("adding token from [%s]", Configuration.DEVELOPER_PROVIDER_NAME));
				stringToSign.append(Configuration.DEVELOPER_PROVIDER_NAME);
				stringToSign.append(logins.get(Configuration.DEVELOPER_PROVIDER_NAME));
			}

			if (identityId != null) {
				stringToSign.append(identityId);
			}
			logger.info(String.format("Get token with userId [%s] timestamp [%s]", userId, timestamp));

			logger.info("validate token request");

			service.validateTokenRequest(userId, signature, stringToSign.toString());

			logger.info("get cognito token for user: " + userId);
			CognitoTokenResultPayload data = service.getCognitoToken(userId, logins, identityId);

			return Answer.ok(data);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return Answer.error(e.getMessage());
		}
	}
}
