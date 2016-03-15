package com.lap.zuzuweb.service;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lap.zuzuweb.dao.ServiceDao;
import com.lap.zuzuweb.dao.UserDao;
import com.lap.zuzuweb.model.Service;
import com.lap.zuzuweb.model.User;
import com.lap.zuzuweb.util.CommonUtils;

public class UserServiceImpl implements UserService
{

	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	
	private UserDao userDao = null;
	private ServiceDao serviceDao = null;
	
	public UserServiceImpl(UserDao userDao, ServiceDao serviceDao)
	{
		this.userDao = userDao;
		this.serviceDao = serviceDao;
	}

	@Override
	public Optional<User> getUserByEmail(String email) {
		logger.info("UserService.getUserByEmail:  + email");
		return this.userDao.getUserByEmail(email);
	}
	
	@Override
	public Optional<User> getUserById(String userID) 
	{
		logger.info("UserService.getUserById:  + userID");
		return this.userDao.getUserById(userID);
	}

	@Override
	public String createUser(User user) 
	{
		logger.info("UserService.createUser:  + user");
		
		if (user == null || StringUtils.isEmpty(user.getEmail())) {
			throw new RuntimeException("Missing required field");
		}
		
		user.setUser_id(CommonUtils.getRandomUUID());
		user.setRegister_time(CommonUtils.getUTCNow());
		
		Optional<User> existUser = this.getUserByEmail(user.getEmail());
		if (existUser.isPresent()) {
			throw new RuntimeException("Email already exists [" + user.getEmail() + "]");
		}
		
		user.setUpdate_time(CommonUtils.getUTCNow());
        return this.userDao.createUser(user);
	}
	
	@Override
	public void updateUser(User user) 
	{
		logger.info("UserService.updateUser:  + user");
		
		if (user == null || StringUtils.isEmpty(user.getUser_id())) {
			throw new RuntimeException("Missing required field");
		}
		
		Optional<User> existUser = this.userDao.getUserById(user.getUser_id());
        if (!existUser.isPresent()) {
        	throw new RuntimeException("User does not exist");
        }

		user.setUpdate_time(CommonUtils.getUTCNow());
        this.userDao.updateUser(user);
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
}
