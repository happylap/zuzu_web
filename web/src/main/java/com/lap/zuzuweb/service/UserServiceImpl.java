package com.lap.zuzuweb.service;

import java.util.Date;
import java.util.Optional;

import com.lap.zuzuweb.dao.UserDao;
import com.lap.zuzuweb.model.User;

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
	public String createUser(User user) {
        Optional<User> existUser = this.dao.getUser(user.getUser_id());
        if (existUser.isPresent()) {
            return existUser.get().getUser_id();
        }
        user.setRegister_time(new Date());
		return this.dao.createUser(user);
	}
	
}
