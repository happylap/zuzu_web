package com.lap.zuzuweb.service;

import java.util.Optional;

import com.lap.zuzuweb.dao.UserDao;
import com.lap.zuzuweb.model.User;
import com.lap.zuzuweb.util.CommonUtils;

public class UserServiceImpl implements UserService
{

	private UserDao dao = null;
	
	public UserServiceImpl(UserDao dao)
	{
		this.dao = dao;
	}
	
	@Override
	public Optional<User> getUser(String userID) 
	{
		return this.dao.getUser(userID);
	}

	@Override
	public String createOrUpdateUser(User user) {
        Optional<User> existUser = this.dao.getUser(user.getUser_id());
        if (!existUser.isPresent()) {
        	user.setRegister_time(CommonUtils.getUTCNow());
    		return this.dao.createUser(user);    
        } else {
        	return this.dao.updateUser(user);
        }
	}
	
}
