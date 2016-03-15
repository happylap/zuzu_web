package com.lap.zuzuweb.handler.user;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.EmptyPayload;
import com.lap.zuzuweb.model.User;
import com.lap.zuzuweb.service.UserService;

public class UserEmailExistHandler extends AbstractRequestHandler<EmptyPayload> {

	private static final Logger logger = LoggerFactory.getLogger(UserEmailExistHandler.class);
	
	private UserService service = null;
	
	public UserEmailExistHandler(UserService service) {
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
    	
    	logger.info("UserEmailExistHandler.processImpl urlParams email: " + email);
    	
        Optional<User> existUser = this.service.getUserByEmail(email);

    	logger.info("UserEmailExistHandler.processImpl exit.");
        return Answer.ok(existUser.isPresent());
    }

}

