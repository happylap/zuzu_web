package com.lap.zuzuweb.service;

import java.util.Optional;

import com.lap.zuzuweb.model.Service;
import com.lap.zuzuweb.model.User;

public interface UserService 
{
	public Optional<User> getUserByEmail(String email);
	
	public Optional<User> getUserById(String userID);
	
	public String createUser(User user);
	
	public void updateUser(User user);
	
	public void deleteUser(String userId, String email);
	
	public Optional<Service> getService(String userID);
	
}
