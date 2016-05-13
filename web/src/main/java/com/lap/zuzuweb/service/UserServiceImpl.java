package com.lap.zuzuweb.service;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.lap.zuzuweb.Utilities;
import com.lap.zuzuweb.ZuzuLogger;
import com.lap.zuzuweb.common.Provider;
import com.lap.zuzuweb.dao.ServiceDao;
import com.lap.zuzuweb.dao.UserDao;
import com.lap.zuzuweb.exception.DataAccessException;
import com.lap.zuzuweb.model.Service;
import com.lap.zuzuweb.model.User;
import com.lap.zuzuweb.util.CommonUtils;

public class UserServiceImpl implements UserService {

	private static final ZuzuLogger logger = ZuzuLogger.getLogger(UserServiceImpl.class);
	
	private UserDao userDao = null;
	private ServiceDao serviceDao = null;

	public UserServiceImpl(UserDao userDao, ServiceDao serviceDao) {
		this.userDao = userDao;
		this.serviceDao = serviceDao;
	}

	@Override
	public Optional<User> getUserByEmail(String email) {
		logger.entering("getUserByEmail", "{email: %s}", email);
		
		return this.userDao.getUserByEmail(email);
	}

	@Override
	public Optional<User> getUserById(String userID) {
		logger.entering("getUserById", "{userID: %s}", userID);
		
		return this.userDao.getUserById(userID);
	}

	@Override
	public void updateUser(User user) throws DataAccessException {
		logger.entering("updateUser", "{user: %s}", user);
		
		if (user == null) {
			return;
		}

		Optional<User> existUser = this.userDao.getUserById(user.getUser_id());
		if (!existUser.isPresent()) {
			throw new DataAccessException("Failed to get user: " + user.getUser_id());
		}
		
		try {
			user.setUpdate_time(CommonUtils.getUTCNow());
			this.userDao.updateUser(user);
		} catch(Exception e) {
			throw new DataAccessException("Failed to update user: " + user.getUser_id());
		}
	}

	@Override
	public void deleteUser(String userId, String email) {
		logger.entering("deleteUser", "{userId: %s, email: %s}", userId, email);
		
		if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(email)) {
			throw new RuntimeException("Missing required field");
		}
		this.userDao.deleteUserByIdAndEmail(userId, email);
	}

	@Override
	public Optional<Service> getService(String userID) {
		logger.entering("getService", "{userID: %s}", userID);
		
		return this.serviceDao.getService(userID);
	}

	@Override
	public User registerRandomUser() throws DataAccessException {
		logger.entering("registerRandomUser");
		
		try {
			String randomId = CommonUtils.getRandomUUID();
			String encryptionToken = Utilities.generateRandomString();
		    
			User randomUser = new User();
			randomUser.setProvider(Provider.ZUZU_NOLOGIN.toString());
			randomUser.setUser_id(randomId);
			randomUser.setEmail(randomId);
			randomUser.setZuzu_token(encryptionToken);
			randomUser.setRegister_time(CommonUtils.getUTCNow());
			randomUser.setUpdate_time(CommonUtils.getUTCNow());	
			this.userDao.createUser(randomUser);
			return randomUser;
		} catch(Exception e) {
			throw new DataAccessException("Failed to create random user");
		}
	}
	
	@Override
	public boolean registerUser(User user) throws DataAccessException {
		logger.entering("registerUser", "{user: %s}", user);
		
		if (checkEmailExists(user.getEmail())) {
            return false;
        }
		
		String userID = user.getUser_id();
	
		if (userID != null) {
			Optional<User> existUser = this.getUserById(userID);
			if (!existUser.isPresent()) {
				throw new DataAccessException("Couldn't find user: " + userID);
	        }
			this.userDao.linkUser(user);
			
		} else {
			this.createUser(user);
		}
		
		return true;
	}
	
	@Override
	public boolean registerUser(String email, String password) throws DataAccessException {
		logger.entering("registerUser", "{email: %s, password: ****}", email);
		
		if (checkEmailExists(email)) {
            return false;
        }
		
		String hashedSaltedPassword = Utilities.getSaltedPassword(email, password);
		
		User newUser = new User();
		newUser.setEmail(email);
		newUser.setHashed_password(hashedSaltedPassword);
		newUser.setProvider(Provider.ZUZU.toString());
		this.createUser(newUser);
        
		return true;
	}
	
	@Override
	public boolean registerUser(String email, String password, String userID) throws DataAccessException {
		logger.entering("registerUser", "{email: %s, password: ****, userID: %s}", email, userID);
		
		if (checkEmailExists(email)) {
            return false;
        }
		
		Optional<User> existUser = this.getUserById(userID);
		
		if (!existUser.isPresent()) {
			throw new DataAccessException("Couldn't find user: " + userID);
        }
		
		String hashedSaltedPassword = Utilities.getSaltedPassword(email, password);
		
		User user = existUser.get();
		user.setEmail(email);
		user.setHashed_password(hashedSaltedPassword);
		user.setProvider(Provider.ZUZU.toString());
		this.userDao.linkUser(user);
		
		return true;
	}

	@Override
	public boolean authenticateUser(String email, String password) throws DataAccessException {
		logger.entering("authenticateUser", "{email: %s, password: ****}", email);
		
		Optional<User> existUser = this.userDao.getUserByEmail(email);
        if (existUser == null) {
            return false;
        }
        
        User user = existUser.get();

        String computedSignature = Utilities.sign(email, password);
        
        return Utilities.slowStringComparison(user.getHashed_password(), computedSignature);
	}
	
	@Override
	public boolean authenticateUserSignature(String email, String timestamp, String signature) throws DataAccessException {
		logger.entering("authenticateUserSignature", "{email: %s, timestamp: ****, signature: ****}", email);
		
		Optional<User> existUser = this.userDao.getUserByEmail(email);
        if (existUser == null) {
            return false;
        }
        
        User user = existUser.get();

        String computedSignature = Utilities.sign(timestamp, user.getHashed_password());
        return Utilities.slowStringComparison(signature, computedSignature);
	}

	@Override
	public boolean checkEmailExists(String email) throws DataAccessException {
		logger.entering("checkEmailExists", "{email: %s}", email);
		
		return this.userDao.getUserByEmail(email).isPresent();
	}
	
	@Override
	public boolean regenerateZuzuToken(String userID) throws DataAccessException {
		logger.entering("regenerateZuzuToken", "{userID: %s}", userID);
		
		Optional<User> existUser = this.userDao.getUserById(userID);
		if (!existUser.isPresent()) {
			throw new DataAccessException("Couldn't find user: " + userID);
        }
        
        User user = existUser.get();
        
        String encryptionToken = Utilities.generateRandomString();
        user.setZuzu_token(encryptionToken);
        
        this.updateUser(user);
        
        return true;
	}

	private void createUser(User user) throws DataAccessException {
		logger.entering("createUser", "{user: %s}", user);
		
		if (user == null) {
			return;
		}
		
		if (checkEmailExists(user.getEmail())) {
            return;
        }
		
		try {
			user.setUser_id(CommonUtils.getRandomUUID());
			user.setRegister_time(CommonUtils.getUTCNow());
			user.setUpdate_time(CommonUtils.getUTCNow());	
			this.userDao.createUser(user);
		} catch(Exception e) {
			throw new DataAccessException("Failed to create user: " + user.getEmail());
		}
	}
	
}
