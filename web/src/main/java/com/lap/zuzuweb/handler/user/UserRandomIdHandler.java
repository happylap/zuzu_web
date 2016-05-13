package com.lap.zuzuweb.handler.user;

import java.util.HashMap;
import java.util.Map;

import com.lap.zuzuweb.ZuzuLogger;
import com.lap.zuzuweb.exception.DataAccessException;
import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.EmptyPayload;
import com.lap.zuzuweb.model.User;
import com.lap.zuzuweb.service.UserService;

public class UserRandomIdHandler extends AbstractRequestHandler<EmptyPayload> {

	private static final ZuzuLogger logger = ZuzuLogger.getLogger(UserRandomIdHandler.class);
	
	private UserService service = null;
	
	public UserRandomIdHandler(UserService service) {
        super(EmptyPayload.class);
        this.service = service;
    }

    @Override
    protected Answer processImpl(EmptyPayload value, Map<String,String> urlParams)
    {
    	logger.entering("processImpl", "{value: %s, urlParams: %s}", value, urlParams);
    	
    	Answer answer;
    	
    	try {
        	User randomUser = this.service.registerRandomUser();
            Map<String, String> data = new HashMap<String, String>();
            data.put("userId", randomUser.getUser_id());
            data.put("zuzuToken", randomUser.getZuzu_token());
            answer = Answer.ok(data);
    	} catch (DataAccessException e) {
            logger.info("Failed to get random userid", e);
            answer = Answer.error("Failed to get random userid");
        }
    	
    	return answer;
    }

}

