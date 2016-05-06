package com.lap.zuzuweb.service;

import java.util.Map;

import com.lap.zuzuweb.exception.DataAccessException;
import com.lap.zuzuweb.exception.UnauthorizedException;
import com.lap.zuzuweb.handler.payload.CognitoTokenResultPayload;

public interface AuthService {

	
	public boolean validateSignature(String stringToSign, String key, String targetSignature);
	public void validateTokenRequest(String userId, String signature, String stringToSign) throws DataAccessException, UnauthorizedException;
	public CognitoTokenResultPayload getCognitoToken(String userId, Map<String, String> logins, String identityId) throws Exception;
	
	public boolean validateToken(String provider, String accessToken);
	
	/**
	 * 
	 * @param provider FB or GOOGLE
	 * @param accessToken 
	 * @return userId
	 * @throws DataAccessException
	 * @throws UnauthorizedException
	 */
	public String validateLoginRequest(String provider, String accessToken) throws DataAccessException, UnauthorizedException;
	public void validateLoginRequest(String email, String signature, String timestamp) throws DataAccessException, UnauthorizedException;
	
	public String getZuzuToken(String email) throws DataAccessException, UnauthorizedException;
	public boolean isZuzuTokenValid(String zuzuToken);
	
	public void forgotPassword(String email) throws Exception;
	public boolean isVerificationCodeValid(String email, String verificationCode);
	public boolean resetPassword(String email, String password);
}
