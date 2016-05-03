package com.lap.zuzuweb.service;

import java.util.Optional;

import com.lap.zuzuweb.exception.DataAccessException;
import com.lap.zuzuweb.model.Service;
import com.lap.zuzuweb.model.User;

public interface UserService {

	public Optional<User> getUserByEmail(String email);

	public Optional<User> getUserById(String userID);
	
	public void updateUser(User user) throws DataAccessException;

	public void deleteUser(String userId, String email);

	public Optional<Service> getService(String userID);

	public boolean registerUser(User user) throws DataAccessException;
	
	public boolean registerUser(String email, String password) throws DataAccessException;
	
	public boolean registerUser(String email, String password, String userId) throws DataAccessException;

	public User registerRandomUser() throws DataAccessException;
	
	public boolean authenticateUser(String email, String password) throws DataAccessException;
	
	public boolean authenticateUserSignature(String email, String timestamp, String signature) throws DataAccessException;
	
	public boolean checkEmailExists(String email) throws DataAccessException;
	
	public boolean regenerateZuzuToken(String userID) throws DataAccessException;
	
}
