package com.lap.zuzuweb;

import java.util.Map;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.services.cognitoidentity.AmazonCognitoIdentityClient;
import com.amazonaws.services.cognitoidentity.model.GetOpenIdTokenForDeveloperIdentityRequest;
import com.amazonaws.services.cognitoidentity.model.GetOpenIdTokenForDeveloperIdentityResult;

public class CognitoDeveloperIdentityManagement {

	private static final ZuzuLogger logger = ZuzuLogger.getLogger(CognitoDeveloperIdentityManagement.class);

	AmazonCognitoIdentityClient cib;

	public CognitoDeveloperIdentityManagement() {
		cib = new AmazonCognitoIdentityClient(
				new BasicAWSCredentials(Configuration.AWS_ACCESS_KEY_ID, Configuration.AWS_SECRET_ACCESS_KEY));
		cib.setRegion(RegionUtils.getRegion(Configuration.REGION));
	}

	public GetOpenIdTokenForDeveloperIdentityResult getOpenIdTokenFromCognito(Map<String, String> logins,
			String identityId) throws Exception {
		
		logger.entering("getOpenIdTokenFromCognito", "{logins: %s, identityId: %s}", logins, identityId);
		
		if (Configuration.IDENTITY_POOL_ID == null) {
			return null;
		}
		
		try {
			GetOpenIdTokenForDeveloperIdentityRequest request = new GetOpenIdTokenForDeveloperIdentityRequest();
			request.setIdentityPoolId(Configuration.IDENTITY_POOL_ID);
			request.setTokenDuration(Long.parseLong(Configuration.SESSION_DURATION));
			request.setLogins(logins);
			if (identityId != null && !identityId.equals("")) {
				request.setIdentityId(identityId);
			}
			logger.info("Requesting identity Id: " + identityId);
			GetOpenIdTokenForDeveloperIdentityResult result = cib.getOpenIdTokenForDeveloperIdentity(request);
			logger.info("Response identity Id: " + result.getIdentityId());
			return result;
		} catch (Exception exception) {
			logger.error("Exception during getTemporaryCredentials", exception);
			throw exception;
		}
		
	}
}
