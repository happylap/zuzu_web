package com.lap.zuzuweb.handler.user;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.EmptyPayload;
import com.lap.zuzuweb.handler.payload.UserCheckPayload;
import com.lap.zuzuweb.model.User;
import com.lap.zuzuweb.service.UserService;

public class UserCheckHandler extends AbstractRequestHandler<EmptyPayload> {

	private static final Logger logger = LoggerFactory.getLogger(UserCheckHandler.class);
	
	private UserService service = null;
	
	public UserCheckHandler(UserService service) {
        super(EmptyPayload.class);
        this.service = service;
    }

    @Override
    protected Answer processImpl(EmptyPayload value, Map<String,String> urlParams)
    {
    	logger.info("UserEmailExistHandler.processImpl enter:");
    	
    	if (!urlParams.containsKey(":email")) {
			return Answer.bad_request();
		}
		
    	String email = urlParams.get(":email");
    	
    	logger.info("UserEmailCheckHandler.processImpl urlParams email: " + email);
    	
        Optional<User> existUser = this.service.getUserByEmail(email);
        
        if (existUser.isPresent()) {
        	UserCheckPayload payload = new UserCheckPayload();
        	payload.fromUser(existUser.get());
            return Answer.ok(payload);
        }
        
        return Answer.no_data();
    }

}

