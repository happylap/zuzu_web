package com.lap.zuzuweb.handler.user;

import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.lap.zuzuweb.Utilities;
import com.lap.zuzuweb.ZuzuLogger;
import com.lap.zuzuweb.common.Provider;
import com.lap.zuzuweb.exception.DataAccessException;
import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.UserRegisterPayload;
import com.lap.zuzuweb.model.User;
import com.lap.zuzuweb.service.UserService;


public class UserRegisterHandler extends AbstractRequestHandler<UserRegisterPayload> {
	
	private static final ZuzuLogger logger = ZuzuLogger.getLogger(UserRegisterHandler.class);
	
	private UserService service = null;
	
	public UserRegisterHandler(UserService service) {
        super(UserRegisterPayload.class);
        this.service = service;
    }
	
    @Override
    protected Answer processImpl(UserRegisterPayload value, Map<String, String> urlParams) {
    	logger.entering("processImpl", "{value: %s, urlParams: %s}", value, urlParams);
    	
    	String provider = value.getProvider();
    	String email = value.getEmail();
    	String password = value.getPassword();
    	
    	//logger.info("Validate email and password");
    	
    	if (!Utilities.isValidEmail(email)) {
            logger.error("Invalid parameters: email [%s]", email);
            return Answer.bad_request(String.format("Invalid parameters: email [%s]", email));
        }
    	
    	if (StringUtils.equalsIgnoreCase(provider, Provider.ZUZU.toString()) && !Utilities.isValidPassword(password)) {
            logger.error("Invalid parameters: password");
            return Answer.bad_request("Invalid parameters: password");
    	}
    	
        try {
            boolean result = false;
            if (StringUtils.equalsIgnoreCase(provider, Provider.ZUZU.toString())) {
            	if (value.getUser_id() != null) {
            		result = service.registerUser(email, password, value.getUser_id());
            	} else {
            		result = service.registerUser(email, password);
            	}
            } else {
            	User newUser = new User();
            	newUser.setUser_id(value.getUser_id());
        		newUser.setProvider(value.getProvider());
        		newUser.setEmail(value.getEmail());
        		newUser.setName(value.getName());
        		newUser.setGender(value.getGender());
        		newUser.setBirthday(value.getBirthday());
        		newUser.setPicture_url(value.getPicture_url());
            	result = service.registerUser(newUser);
            }
            
            if (!result) {
                logger.error("Duplicate registration [%s]", email);
                return Answer.not_acceptable(String.format("Duplicate registration [%s]", email));
            }
        } catch (DataAccessException e) {
            logger.error("Failed to register user", e);
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

