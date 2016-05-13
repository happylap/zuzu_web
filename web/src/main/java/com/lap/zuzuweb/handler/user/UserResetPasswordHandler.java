package com.lap.zuzuweb.handler.user;

import java.util.Map;
import java.util.Optional;

import com.lap.zuzuweb.Utilities;
import com.lap.zuzuweb.ZuzuLogger;
import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.UserResetPasswordPayload;
import com.lap.zuzuweb.model.User;
import com.lap.zuzuweb.service.AuthService;
import com.lap.zuzuweb.service.UserService;


public class UserResetPasswordHandler extends AbstractRequestHandler<UserResetPasswordPayload> {
	
	private static final ZuzuLogger logger = ZuzuLogger.getLogger(UserResetPasswordHandler.class);
	
	private AuthService service = null;

	private UserService userSvc = null;
	
	public UserResetPasswordHandler(AuthService service, UserService userSvc) {
        super(UserResetPasswordPayload.class);
        this.service = service;
        this.userSvc = userSvc;
    }
	
	
    @Override
    protected Answer processImpl(UserResetPasswordPayload value, Map<String, String> urlParams) {
    	logger.entering("processImpl", "{value: %s, urlParams: %s}", value, urlParams);
    	
    	String email = value.getEmail();
    	String password = value.getPassword();
    	String verificationCode = value.getVerification_code();
    	
    	logger.info("Validate email and password");
    	
    	if (!Utilities.isValidEmail(email) || !Utilities.isValidPassword(password)) {
            logger.warn(String.format("Invalid parameters: email [%s]", email));
            return Answer.bad_request(String.format("Invalid parameters: email [%s] or password", email));
        }
    	
        logger.info("Reset password of user: " + email);
        
        if (!service.isVerificationCodeValid(email, verificationCode)) {
        	logger.info(String.format("Invalid verification code [%s]", verificationCode));
        	return Answer.error(String.format("Invalid verification code [%s]", verificationCode));
        }
        
        if (!service.resetPassword(email, password)) {
        	logger.info(String.format("Failed to reset password for user [%s]", email));
            return Answer.error(String.format("Failed to reset password for user [%s]", email));
        }
        
        logger.info(String.format("User [%s] reset password successfully", email));
        
        Optional<User> existUser = this.userSvc.getUserByEmail(email);
        
        if (!existUser.isPresent()) {
        	return Answer.error("Failed to find user by email: " + email);
        }
        
        User user = existUser.get();
        
        return Answer.ok(user.getUser_id());
    }
}

