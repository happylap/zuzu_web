package com.lap.zuzuweb.handler.user;

import java.util.Map;

import com.lap.zuzuweb.ZuzuLogger;
import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.EmptyPayload;
import com.lap.zuzuweb.service.UserService;

public class UserRemoveHandler extends AbstractRequestHandler<EmptyPayload> {

	private static final ZuzuLogger logger = ZuzuLogger.getLogger(UserRemoveHandler.class);
	
	private UserService service = null;

	public UserRemoveHandler(UserService service) {
		super(EmptyPayload.class);
		this.service = service;
	}

	@Override
	protected Answer processImpl(EmptyPayload value, Map<String, String> urlParams) {
		logger.entering("processImpl", "{value: %s, urlParams: %s}", value, urlParams);
		
		if (urlParams.containsKey(":userid") && urlParams.containsKey(":email")) {
			String userId = urlParams.get(":userid");
			String email = urlParams.get(":email");
			service.deleteUser(userId, email);
			return Answer.ok();
		} else {
			return Answer.bad_request();
		}
	}

}
