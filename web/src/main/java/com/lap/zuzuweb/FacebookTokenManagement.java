package com.lap.zuzuweb;

import org.apache.commons.lang3.StringUtils;

import com.lap.zuzuweb.util.CrunchifyInMemoryCache;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.FacebookClient.AccessToken;
import com.restfb.FacebookClient.DebugTokenInfo;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.types.User;

public class FacebookTokenManagement {

	private static final ZuzuLogger logger = ZuzuLogger.getLogger(FacebookTokenManagement.class);
	
	private  FacebookClient fbc;
	
	private CrunchifyInMemoryCache<String, Boolean> cache = new CrunchifyInMemoryCache<String, Boolean>(1800, 1800, 300);
	
	public FacebookTokenManagement() {
		AccessToken accessToken = new DefaultFacebookClient(Version.LATEST)
				.obtainAppAccessToken(Secrets.FACEBOOK_APP_ID, Secrets.FACEBOOK_APP_SECRET);
		fbc = new DefaultFacebookClient(accessToken.getAccessToken(), Version.LATEST);
	}
	
//	private boolean isValid(String accessToken) {
//		logger.entering("isValid", StringUtils.abbreviateMiddle(accessToken, "...", 15));
//		
//		if (StringUtils.isBlank(accessToken)) {
//			return false;
//		}
//		DebugTokenInfo info = fbc.debugToken(accessToken);
//		return info != null ? info.isValid() : false;
//	}
	
	public boolean isValid(String accessToken, boolean useCache) {	
		logger.entering("isValid", "{accessToken: %s, useCache: %s}", StringUtils.abbreviateMiddle(accessToken, "...", 15), useCache);
		
		if (StringUtils.isBlank(accessToken)) {
			return false;
		}
		
		if (useCache && cache.get(accessToken) != null) {
			logger.info("Found token in cache:: %s", StringUtils.abbreviateMiddle(accessToken, "...", 15));
			return true;
		}
		
		DebugTokenInfo debugTokenInfo = fbc.debugToken(accessToken);
		boolean isValid = (debugTokenInfo != null ? debugTokenInfo.isValid() : false);
		
		if (useCache && isValid) {
			logger.info("Put token to cache:: %s", StringUtils.abbreviateMiddle(accessToken, "...", 15));
			cache.put(accessToken, isValid);
		}
		
		return isValid;
	}
	
	private User getSocialUserByToken(String accessToken) {
		logger.entering("getSocialUserByToken", "{accessToken: %s}", StringUtils.abbreviateMiddle(accessToken, "...", 15));
		
		if (isValid(accessToken, false)) {
			FacebookClient facebookClient = new DefaultFacebookClient(accessToken, Version.LATEST);

			// fetch user type with id, name and email prefilled
			return facebookClient.fetchObject("me", User.class, Parameter.with("fields", "id, name, email"));
		}
		return null;
	}

	public String getEmailByToken(String accessToken) {
		logger.entering("getEmailByToken", "{accessToken: %s}", StringUtils.abbreviateMiddle(accessToken, "...", 15));
		
		User user = getSocialUserByToken(accessToken);
		return user != null ? user.getEmail() : null;
	}
}
