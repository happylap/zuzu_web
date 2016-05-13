package com.lap.zuzuweb;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.lap.zuzuweb.dao.UserDao;
import com.lap.zuzuweb.model.User;
import com.lap.zuzuweb.util.CrunchifyInMemoryCache;

public class ZuzuTokenManagement {

	private static final ZuzuLogger logger = ZuzuLogger.getLogger(ZuzuTokenManagement.class);
	

	private CrunchifyInMemoryCache<String, Boolean> cache = new CrunchifyInMemoryCache<String, Boolean>(1800, 1800, 300);
	
	UserDao userDao;

	public ZuzuTokenManagement(UserDao userDao) {
		this.userDao = userDao;
	}
	
	public boolean isValid(String accessToken) {
		logger.entering("isValid", "{accessToken: %s}", StringUtils.abbreviateMiddle(accessToken, "...", 15));
		
		if (StringUtils.isBlank(accessToken)) {
			return false;
		}
		Optional<User> existUser = this.userDao.getUserByToken(accessToken);
		return existUser.isPresent();
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
	
}
