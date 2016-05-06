/**
 * 
 */
package com.lap.zuzuweb.service;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.cognitoidentity.model.GetOpenIdTokenForDeveloperIdentityResult;
import com.lap.zuzuweb.CognitoDeveloperIdentityManagement;
import com.lap.zuzuweb.Configuration;
import com.lap.zuzuweb.FacebookTokenManagement;
import com.lap.zuzuweb.GoogleTokenManagement;
import com.lap.zuzuweb.Utilities;
import com.lap.zuzuweb.ZuzuTokenManagement;
import com.lap.zuzuweb.common.Provider;
import com.lap.zuzuweb.dao.UserDao;
import com.lap.zuzuweb.exception.DataAccessException;
import com.lap.zuzuweb.exception.UnauthorizedException;
import com.lap.zuzuweb.handler.payload.CognitoTokenResultPayload;
import com.lap.zuzuweb.model.User;
import com.lap.zuzuweb.util.AuthUtils;
import com.lap.zuzuweb.util.CommonUtils;
import com.lap.zuzuweb.util.mail.Mail;
import com.lap.zuzuweb.util.mail.MailSender;

/**
 * @author eechih
 *
 */
public class AuthServiceImpl implements AuthService {

	private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

	private final CognitoDeveloperIdentityManagement byoiManagement;
	private final GoogleTokenManagement gtManagement;
	private final FacebookTokenManagement ftManagement;
	private final ZuzuTokenManagement ztManagement;
	
	private UserDao userDao;

	public AuthServiceImpl(UserDao userDao) {
		this.byoiManagement = new CognitoDeveloperIdentityManagement();
		this.gtManagement = new GoogleTokenManagement();
		this.ftManagement = new FacebookTokenManagement();
		this.ztManagement = new ZuzuTokenManagement(userDao);
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

		if (user != null && !user.getUser_id().equals(logins.get(Configuration.DEVELOPER_PROVIDER_NAME))) {
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
	public boolean validateToken(String provider, String accessToken) {
		logger.info(String.format("validate %s token: %s", provider, accessToken));
		
		boolean isValid = false;
		
		if (StringUtils.equalsIgnoreCase(Provider.FB.toString(), provider)) {
			isValid = ftManagement.isValid(accessToken);
		}
		
		if (StringUtils.equalsIgnoreCase(Provider.GOOGLE.toString(), provider)) {
			isValid = gtManagement.isValid(accessToken);
		}
		
		if (StringUtils.equalsIgnoreCase(Provider.ZUZU.toString(), provider)) {
			isValid = ztManagement.isValid(accessToken);
		}
		
		if (StringUtils.equalsIgnoreCase(Provider.ZUZU_NOLOGIN.toString(), provider)) {
			isValid = ztManagement.isValid(accessToken);
		}
		
		return isValid;
	}

	@Override
	public String validateLoginRequest(String provider, String accessToken) 
			throws DataAccessException, UnauthorizedException {
		// Validate accessToken
		logger.info(String.format("Validate login provider [%s], accessToken [%s]", provider, accessToken));
		
		String email = null;
		
		if (StringUtils.equalsIgnoreCase(Provider.FB.toString(), provider)) {
			email = ftManagement.isValid(accessToken) ? ftManagement.getEmail(accessToken) : null; 
		}
		
		if (StringUtils.equalsIgnoreCase(Provider.GOOGLE.toString(), provider)) {
			email = gtManagement.isValid(accessToken) ? gtManagement.getEmail(accessToken) : null;
		}
		
		if (email == null) {
			logger.warn("Failed to login");
        	throw new UnauthorizedException("Failed to login");
        }
		
		Optional<User> existUser = this.userDao.getUserByEmail(email);
		
		if (!existUser.isPresent()) {
			logger.warn("Failed to find user by email: " + email);
			throw new UnauthorizedException("Failed to find user by email: " + email);
		}
		
		User user = existUser.get();
		
		this.regenerateZuzuToken(user.getUser_id());
		
		return user.getUser_id();
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

	@Override
	public boolean isZuzuTokenValid(String zuzuToken) {
		if (StringUtils.isBlank(zuzuToken)) {
			return false;
		}
		
		if (AuthUtils.getValidTokenCache().get(zuzuToken) != null && AuthUtils.getValidTokenCache().get(zuzuToken) == true) {
			logger.info("Zuzu token is valid in cache.");
			return true;
		}
		
		Optional<User> existUser = this.userDao.getUserByToken(zuzuToken);
		
		if (existUser.isPresent()) {
			logger.info("Put valid Zuzu token to cache.");
			AuthUtils.getValidTokenCache().put(zuzuToken, true);
			return true;
		}
		
		return false;
	}
	
	@Override
	public void forgotPassword(String email) throws Exception {
		Optional<User> existUser = this.userDao.getUserByEmail(email);
		if (existUser.isPresent()) {
			String verificationCode = Utilities.generateRandomNumber(4);
			
			Calendar c = Calendar.getInstance();
			c.setTime(CommonUtils.getUTCNow());
			c.add(Calendar.MINUTE, 30);
			Date verify_expire_time = c.getTime();
			
			User user = existUser.get();
			user.setVerification_code(verificationCode);
			user.setVerify_expire_time(verify_expire_time);
			this.userDao.updateUser(user);
			
			String subjectTemplate = "豬豬快租-發送驗證碼 %s";
			String bodyTemplate = "*** 此為系統自動發送，請勿直接回覆 ***<br/><br/><h2>驗證碼: %s</h2><br/>提醒您，此驗證碼將於30分鐘後失效。";
			Mail mail = new Mail();
			mail.subject = String.format(subjectTemplate, CommonUtils.getUTCStringFromDate(CommonUtils.getUTCNow()));
			mail.body = String.format(bodyTemplate, user.getVerification_code());
			mail.contentType = "text/html;charset=UTF-8";
			mail.addMailTo(email);
			MailSender.sendEmail(mail);
		}
	}
	
	@Override
	public boolean isVerificationCodeValid(String email, String verificationCode) {
		if (StringUtils.isBlank(email) || StringUtils.isBlank(verificationCode)) {
			logger.warn(String.format("Bad paramters email [%s], verificationCode [%s]", email, verificationCode));
			return false;
		}
		
		Optional<User> existUser = this.userDao.getUserByEmail(email);
        if (!existUser.isPresent()) {
            logger.warn(String.format("Failed to find user [%s]", email));
        	return false;
        }
        
        User user = existUser.get();
        
        if (StringUtils.isBlank(user.getVerification_code()) || user.getVerify_expire_time() == null) {
        	logger.warn(String.format("Failed to find verification_code or verify_expire_time for user [%s]", email));
            return false; 
        }
        
        if (CommonUtils.getUTCNow().after(user.getVerify_expire_time())) {
        	logger.warn(String.format("verify_expire_time has expired for user [%s]", email));
        	return false;
        }
        
        if (!StringUtils.equalsIgnoreCase(user.getVerification_code(), verificationCode)) {
        	logger.warn(String.format("Invalid verification code [%s] for user [%s]", verificationCode, email));
        	return false;
        }
        
        return true; 
	}

	@Override
	public boolean resetPassword(String email, String password) {
		
		Optional<User> existUser = this.userDao.getUserByEmail(email);
        if (existUser.isPresent()) {
        	String hashedSaltedPassword = Utilities.getSaltedPassword(email, password);
    		
    		User user = existUser.get();
    		user.setHashed_password(hashedSaltedPassword);
    		user.setVerification_code(null);
    		user.setVerify_expire_time(null);
    		this.userDao.updateUser(user);
            
    		return true;
        }
        
        return false;
	}
}
