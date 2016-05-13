package com.lap.zuzuweb.handler.user;

import java.util.Map;

import com.lap.zuzuweb.ZuzuLogger;
import com.lap.zuzuweb.exception.DataAccessException;
import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.UserUpdatePayload;
import com.lap.zuzuweb.service.UserService;

public class UserUpdateHandler extends AbstractRequestHandler<UserUpdatePayload> {
	
	private static final ZuzuLogger logger = ZuzuLogger.getLogger(UserUpdateHandler.class);
	
	private UserService service = null;

	public UserUpdateHandler(UserService service) {
		super(UserUpdatePayload.class);
		this.service = service;
	}

	@Override
	protected Answer processImpl(UserUpdatePayload value, Map<String, String> urlParams) {
		logger.entering("processImpl", "{value: %s, urlParams: %s}", value, urlParams);
		
		try {
	    	service.updateUser(value.toUser());
	    	return Answer.ok();
    	} catch (DataAccessException e) {
    		return Answer.error(e.getMessage());
    	}
	}
}
