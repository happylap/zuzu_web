package com.lap.zuzuweb.service;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.lap.zuzuweb.dao.ServiceDao;
import com.lap.zuzuweb.dao.UserDao;
import com.lap.zuzuweb.model.Service;
import com.lap.zuzuweb.model.User;
import com.lap.zuzuweb.util.CommonUtils;

public class UserServiceImpl implements UserService
{

	private UserDao userDao = null;
	private ServiceDao serviceDao = null;
	
	public UserServiceImpl(UserDao userDao, ServiceDao serviceDao)
	{
		this.userDao = userDao;
		this.serviceDao = serviceDao;
	}

	@Override
	public Optional<User> getUserByEmail(String email) {
		return this.userDao.getUserByEmail(email);
	}
	
	@Override
	public Optional<User> getUserById(String userID) 
	{
		return this.userDao.getUserById(userID);
	}

	@Override
	public String createUser(User user) {

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
	public void updateUser(User user) {
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
	public Optional<Service> getService(String userID) {
		return this.serviceDao.getService(userID);
	}
}
