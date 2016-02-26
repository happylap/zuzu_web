package com.lap.zuzuweb.service;

import java.util.Optional;

import com.lap.zuzuweb.model.User;

public interface UserService 
{
	public Optional<User> getUser(String userID);
	
	public String createOrUpdateUser(User user);
}
