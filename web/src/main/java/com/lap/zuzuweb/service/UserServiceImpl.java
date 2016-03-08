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
	public Optional<User> getUser(String userID) 
	{
		return this.userDao.getUser(userID);
	}

	@Override
	public String createOrUpdateUser(User user) {
        Optional<User> existUser = this.userDao.getUser(user.getUser_id());
        if (!existUser.isPresent()) {
        	user.setRegister_time(CommonUtils.getUTCNow());
    		return this.userDao.createUser(user);    
        } else {
        	return this.userDao.updateUser(user);
        }
	}

	@Override
	public Optional<Service> getService(String userID) {
		return this.serviceDao.getService(userID);
	}
}
