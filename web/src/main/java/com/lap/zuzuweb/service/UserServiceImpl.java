package com.lap.zuzuweb.service;

import java.util.Optional;

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
		user.setUser_id(CommonUtils.getRandomUUID());
		user.setRegister_time(CommonUtils.getUTCNow());
		
		if (user != null && !user.isValid()) {
			throw new RuntimeException("[User] Missing required field");
		}
		
		Optional<User> existUser = this.getUserByEmail(user.getEmail());
		if (existUser.isPresent()) {
			throw new RuntimeException("[User] Email already exists [" + user.getEmail() + "]");
		}
        
        return this.userDao.createUser(user);
	}
	
	@Override
	public void updateUser(User user) {
		if (user != null && !user.isValid()) {
			throw new RuntimeException("[User] Missing required field");
		}
		
		Optional<User> existUser = this.userDao.getUserById(user.getUser_id());
        if (!existUser.isPresent()) {
        	throw new RuntimeException("User does not exist");
        }
        
		existUser = this.getUserByEmail(user.getEmail());
		if (existUser.isPresent()) {
			throw new RuntimeException("[User] Email already exists [" + user.getEmail() + "]");
		}
		
        this.userDao.updateUser(user);
	}

	@Override
	public Optional<Service> getService(String userID) {
		return this.serviceDao.getService(userID);
	}
}
