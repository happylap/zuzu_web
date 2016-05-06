package com.lap.zuzuweb;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lap.zuzuweb.util.CrunchifyInMemoryCache;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.FacebookClient.AccessToken;
import com.restfb.FacebookClient.DebugTokenInfo;
import com.restfb.types.User;
import com.restfb.Version;

public class FacebookTokenManagement {

	private static final Logger logger = LoggerFactory.getLogger(FacebookTokenManagement.class);

	private  FacebookClient fbc;
	
	private CrunchifyInMemoryCache<String, Boolean> cache = new CrunchifyInMemoryCache<String, Boolean>(1800, 1800, 300);
	
	public FacebookTokenManagement() {
		AccessToken accessToken = new DefaultFacebookClient(Version.LATEST)
				.obtainAppAccessToken(Secrets.FACEBOOK_APP_ID, Secrets.FACEBOOK_APP_SECRET);
		fbc = new DefaultFacebookClient(accessToken.getAccessToken(), Version.LATEST);
	}

	public DebugTokenInfo getTokenInfo(String accessToken) {
		return fbc.debugToken(accessToken);
	}
	
	public boolean isValid(String accessToken) {
		if (StringUtils.isBlank(accessToken)) {
			return false;
		}
		DebugTokenInfo info = this.getTokenInfo(accessToken);
		return info != null ? info.isValid() : false;
	}
	
	public boolean isValid(String accessToken, boolean useCache) {	
		if (useCache && cache.get(accessToken) != null) {
			logger.info("found valid facebook token in cache.");
			return true;
		}
		
		boolean isValid = this.isValid(accessToken);
		
		if (useCache && isValid) {
			logger.info("put valid facebook token to cache.");
			cache.put(accessToken, isValid);
		}
		
		return isValid;
	}
	
	public User getUserInfo(String accessToken) {
		if (isValid(accessToken)) {
			FacebookClient facebookClient = new DefaultFacebookClient(accessToken, Version.LATEST);

			// fetch user type with id, name and email prefilled
			return facebookClient.fetchObject("me", User.class, Parameter.with("fields", "id, name, email"));
		}
		return null;
	}

	public String getEmail(String accessToken) {
		User user = getUserInfo(accessToken);
		return user != null ? user.getEmail() : null;
	}
}
