package com.lap.zuzuweb.dao;

import java.util.Optional;

import com.lap.zuzuweb.model.User;

public interface UserDao 
{
	public Optional<User> getUserByEmail(String email);
	
	public Optional<User> getUserById(String userID);

	public Optional<User> getUserByToken(String zuzuToken);
	
	public String createUser(User user);
	
	public String updateUser(User user);
	
	public String linkUser(User user);
	
	public void deleteUserByIdAndEmail(String userID, String email);
}
