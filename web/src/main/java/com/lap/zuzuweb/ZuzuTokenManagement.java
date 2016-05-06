package com.lap.zuzuweb;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lap.zuzuweb.dao.UserDao;
import com.lap.zuzuweb.model.User;
import com.lap.zuzuweb.util.CrunchifyInMemoryCache;

public class ZuzuTokenManagement {

	private static final Logger logger = LoggerFactory.getLogger(FacebookTokenManagement.class);

	private CrunchifyInMemoryCache<String, Boolean> cache = new CrunchifyInMemoryCache<String, Boolean>(1800, 1800, 300);
	
	UserDao userDao;

	public ZuzuTokenManagement(UserDao userDao) {
		this.userDao = userDao;
	}
	
	public boolean isValid(String zuzuToken) {
		if (StringUtils.isBlank(zuzuToken)) {
			return false;
		}
		Optional<User> existUser = this.userDao.getUserByToken(zuzuToken);
		return existUser.isPresent();
	}
	
	public boolean isValid(String accessToken, boolean useCache) {	
		if (useCache && cache.get(accessToken) != null) {
			logger.info("found valid zuzu token in cache.");
			return true;
		}
		
		boolean isValid = this.isValid(accessToken);
		
		if (useCache && isValid) {
			logger.info("put valid zuzu token to cache.");
			cache.put(accessToken, isValid);
		}
		
		return isValid;
	}
	
}
