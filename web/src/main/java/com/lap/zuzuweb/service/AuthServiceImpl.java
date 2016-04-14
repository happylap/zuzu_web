/**
 * 
 */
package com.lap.zuzuweb.service;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.cognitoidentity.model.GetOpenIdTokenForDeveloperIdentityResult;
import com.lap.zuzuweb.CognitoDeveloperIdentityManagement;
import com.lap.zuzuweb.Configuration;
import com.lap.zuzuweb.Utilities;
import com.lap.zuzuweb.dao.UserDao;
import com.lap.zuzuweb.exception.DataAccessException;
import com.lap.zuzuweb.exception.UnauthorizedException;
import com.lap.zuzuweb.handler.payload.CognitoTokenResultPayload;
import com.lap.zuzuweb.model.User;

/**
 * @author eechih
 *
 */
public class AuthServiceImpl implements AuthService {

	private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

	private final CognitoDeveloperIdentityManagement byoiManagement;
	
	private UserDao userDao;

	public AuthServiceImpl(UserDao userDao) {
		this.byoiManagement = new CognitoDeveloperIdentityManagement();
		this.userDao = userDao;
	}
	

	@Override
	public boolean validateSignature(String stringToSign, String key, String targetSignature) {
		String computedSignature = Utilities.sign(stringToSign, key);
		return Utilities.slowStringComparison(targetSignature, computedSignature);
	}
		

	@Override
	public void validateTokenRequest(String userId, String signature, String stringToSign)
			throws DataAccessException, UnauthorizedException {

		Optional<User> existUser = this.userDao.getUserById(userId);
		if (!existUser.isPresent()) {
			throw new UnauthorizedException("Couldn't find user: " + userId);
		}

		User user = existUser.get();

		if (!validateSignature(stringToSign, user.getZuzu_token(), signature)) {
			logger.info("String to sign: " + stringToSign);
			throw new UnauthorizedException("Invalid signature: " + signature);
		}
		logger.info("Signature matched!!!");
	}
	

	@Override
	public CognitoTokenResultPayload getCognitoToken(String userId, Map<String, String> logins, String identityId) throws Exception {

		Optional<User> existUser = this.userDao.getUserById(userId);
		if (!existUser.isPresent()) {
			throw new UnauthorizedException("Couldn't find user: " + userId);
		}

		User user = existUser.get();

		if (user != null && !user.getEmail().equals(logins.get(Configuration.DEVELOPER_PROVIDER_NAME))) {
			throw new UnauthorizedException("User mismatch for provider and logins map");
		}

		logger.info("Creating temporary credentials");
		GetOpenIdTokenForDeveloperIdentityResult result = byoiManagement.getOpenIdTokenFromCognito(logins, identityId);

		logger.info("Generating session tokens for userId : " + userId);
		
		CognitoTokenResultPayload openIdToken = new CognitoTokenResultPayload();
		openIdToken.setIdentityId(result.getIdentityId());
		openIdToken.setToken(result.getToken());
		return openIdToken;
	}
	
	@Override
	public void validateLoginRequest(String email, String signature, String timestamp)
			throws DataAccessException, UnauthorizedException {
		
		// Validate signature
		logger.info("Validate signature: " + signature);
		Optional<User> existUser = this.userDao.getUserByEmail(email);
		if (!existUser.isPresent()) {
			throw new UnauthorizedException("Couldn't find user by email: " + email);
		}
		
		User user = existUser.get();
		
		if (!validateSignature(user.getHashed_password(), timestamp, signature)) {
			throw new UnauthorizedException("Invalid signature: " + signature);
		}

		logger.info("Signature matched!!!");

		this.regenerateZuzuToken(user.getUser_id());
		
		logger.info("ZuzuToken registered successfully!!!");
	}
	
	private boolean regenerateZuzuToken(String userID) throws DataAccessException {
		logger.info("Generating encryption zuzu token");
		
		Optional<User> existUser = this.userDao.getUserById(userID);
		if (!existUser.isPresent()) {
			throw new DataAccessException("Couldn't find user: " + userID);
	    }
	    
	    User user = existUser.get();
	    
	    String encryptionToken = Utilities.generateRandomString();
	    user.setZuzu_token(encryptionToken);
	    
	    this.userDao.updateUser(user);
	    
	    return true;
	}

	@Override
	public String getZuzuToken(String email) throws DataAccessException, UnauthorizedException {
		Optional<User> existUser = this.userDao.getUserByEmail(email);
        if (!existUser.isPresent()) {
        	throw new UnauthorizedException("Couldn't find user by email: " + email);
        }
        
        User user = existUser.get();
        
        logger.info("Responding with encrypted key for email : " + email);
        return user.getZuzu_token();
	}
}
