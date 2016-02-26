package com.lap.zuzuweb.dao;

import java.util.Optional;

import com.lap.zuzuweb.model.User;

public interface UserDao 
{
	public Optional<User> getUser(String userID);
	
	public String createUser(User user);
	
	public String updateUser(User user);
}
