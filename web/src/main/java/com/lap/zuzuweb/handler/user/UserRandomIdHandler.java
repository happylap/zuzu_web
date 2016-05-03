package com.lap.zuzuweb.handler.user;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lap.zuzuweb.exception.DataAccessException;
import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.EmptyPayload;
import com.lap.zuzuweb.model.User;
import com.lap.zuzuweb.service.UserService;

public class UserRandomIdHandler extends AbstractRequestHandler<EmptyPayload> {

	private static final Logger logger = LoggerFactory.getLogger(UserRandomIdHandler.class);
	
	private UserService service = null;
	
	public UserRandomIdHandler(UserService service) {
        super(EmptyPayload.class);
        this.service = service;
    }

    @Override
    protected Answer processImpl(EmptyPayload value, Map<String,String> urlParams)
    {
    	logger.info("UserRandomIdHandler.processImpl enter:");
    	
    	try {
        	User randomUser = this.service.registerRandomUser();
            return Answer.ok(randomUser.getUser_id());
    	} catch (DataAccessException e) {
            logger.info("Failed to get random userid", e);
            return Answer.error("Failed to get random userid");
        }
    }

}

