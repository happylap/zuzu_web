package com.lap.zuzuweb.handler.user;

import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lap.zuzuweb.Utilities;
import com.lap.zuzuweb.common.Provider;
import com.lap.zuzuweb.exception.DataAccessException;
import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.UserRegisterPayload;
import com.lap.zuzuweb.model.User;
import com.lap.zuzuweb.service.UserService;


public class UserRegisterHandler extends AbstractRequestHandler<UserRegisterPayload> {
	
	private static final Logger logger = LoggerFactory.getLogger(UserRegisterHandler.class);
	
	private UserService service = null;
	
	public UserRegisterHandler(UserService service) {
        super(UserRegisterPayload.class);
        this.service = service;
    }
	
    @Override
    protected Answer processImpl(UserRegisterPayload value, Map<String, String> urlParams) {
    	String provider = value.getProvider();
    	String email = value.getEmail();
    	String password = value.getPassword();
    	
    	logger.info("Validate email and password");
    	
    	if (!Utilities.isValidEmail(email)) {
            logger.warn(String.format("Invalid parameters: email [%s]", email));
            return Answer.bad_request(String.format("Invalid parameters: email [%s]", email));
        }
    	
    	if (StringUtils.equalsIgnoreCase(provider, Provider.ZUZU.toString()) && !Utilities.isValidPassword(password)) {
            logger.warn("Invalid parameters: password");
            return Answer.bad_request("Invalid parameters: password");
    	}
    	
        try {
        	
            logger.info("Register user: " + email);
            boolean result = false;
            if (StringUtils.equalsIgnoreCase(provider, Provider.ZUZU.toString())) {
            	result = service.registerUser(email, password);
            } else {
            	User user = new User();
        		user.setProvider(value.getProvider());
        		user.setEmail(value.getEmail());
        		user.setName(value.getName());
        		user.setGender(value.getGender());
        		user.setBirthday(value.getBirthday());
        		user.setPicture_url(value.getPictureUrl());
            	result = service.registerUser(user);
            }
            
            if (!result) {
                logger.warn(String.format("Duplicate registration [%s]", email));
                return Answer.not_acceptable(String.format("Duplicate registration [%s]", email));
            }
        } catch (DataAccessException e) {
            logger.info(String.format("Failed to register user [%s]", email), e);
            return Answer.error(String.format("Failed to register user [%s]", email));
        }
        
        logger.info(String.format("User [%s] registered successfully", email));
        
        String userId = null;
        Optional<User> existUser = service.getUserByEmail(email);
        if (existUser.isPresent()) {
        	userId = existUser.get().getUser_id();
        }
        return Answer.ok(userId);
    }
}
