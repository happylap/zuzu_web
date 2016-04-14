package com.lap.zuzuweb.service;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lap.zuzuweb.Utilities;
import com.lap.zuzuweb.common.Provider;
import com.lap.zuzuweb.dao.ServiceDao;
import com.lap.zuzuweb.dao.UserDao;
import com.lap.zuzuweb.exception.DataAccessException;
import com.lap.zuzuweb.exception.UnauthorizedException;
import com.lap.zuzuweb.model.Service;
import com.lap.zuzuweb.model.User;
import com.lap.zuzuweb.util.CommonUtils;

public class UserServiceImpl implements UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	private UserDao userDao = null;
	private ServiceDao serviceDao = null;

	public UserServiceImpl(UserDao userDao, ServiceDao serviceDao) {
		this.userDao = userDao;
		this.serviceDao = serviceDao;
	}

	@Override
	public Optional<User> getUserByEmail(String email) {
		logger.info("UserService.getUserByEmail:  + email");
		return this.userDao.getUserByEmail(email);
	}

	@Override
	public Optional<User> getUserById(String userID) {
		logger.info("UserService.getUserById:  + userID");
		return this.userDao.getUserById(userID);
	}
	
	@Override
	public void createUser(User user) throws DataAccessException {
		logger.info("UserService.createUser:  + user");
		
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

	@Override
	public void updateUser(User user) throws DataAccessException {
		logger.info("UserService.updateUser:  + user");
		
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
		if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(email)) {
			throw new RuntimeException("Missing required field");
		}
		this.userDao.deleteUserByIdAndEmail(userId, email);
	}

	@Override
	public Optional<Service> getService(String userID) {
		return this.serviceDao.getService(userID);
	}

	@Override
	public boolean registerUser(User user) throws DataAccessException {
		if (checkEmailExists(user.getEmail())) {
            return false;
        }
		this.createUser(user);
		return true;
	}
	
	@Override
	public boolean registerUser(String email, String password) throws DataAccessException {
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
	public boolean authenticateUser(String email, String password) throws DataAccessException {
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
		Optional<User> existUser = this.userDao.getUserByEmail(email);
        if (existUser == null) {
            return false;
        }
        
        User user = existUser.get();

        String computedSignature = Utilities.sign(timestamp, user.getHashed_password());
        return Utilities.slowStringComparison(signature, computedSignature);
	}
	
	public boolean checkEmailExists(String email) throws DataAccessException {
		return this.userDao.getUserByEmail(email).isPresent();
	}
	
	
	public boolean regenerateZuzuToken(String userID) throws DataAccessException {
		logger.info("Generating encryption zuzu token");
		
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

	
}
