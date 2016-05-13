package com.lap.zuzuweb.handler.user;

import java.util.Map;

import com.lap.zuzuweb.ZuzuLogger;
import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.EmptyPayload;
import com.lap.zuzuweb.service.AuthService;

public class UserForgetPasswordHandler extends AbstractRequestHandler<EmptyPayload> {

	private static final ZuzuLogger logger = ZuzuLogger.getLogger(UserForgetPasswordHandler.class);

	private AuthService service = null;

	public UserForgetPasswordHandler(AuthService service) {
		super(EmptyPayload.class);
		this.service = service;
	}

	@Override
	protected Answer processImpl(EmptyPayload value, Map<String, String> urlParams) {
		logger.entering("processImpl", "{value: %s, urlParams: %s}", value, urlParams);

		if (!urlParams.containsKey(":email")) {
			return Answer.bad_request();
		}

		String email = urlParams.get(":email");

		try {
			this.service.forgotPassword(email);
		} catch (Exception e) {
			logger.exit("Failed to forgot password for user [%s]", email);
			return Answer.error(String.format("Failed to forgot password for user [%s]", email));
		}

		return Answer.ok();
	}

}
