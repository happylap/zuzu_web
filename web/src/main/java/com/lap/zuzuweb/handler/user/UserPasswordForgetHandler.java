package com.lap.zuzuweb.handler.user;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.EmptyPayload;
import com.lap.zuzuweb.service.AuthService;

public class UserPasswordForgetHandler extends AbstractRequestHandler<EmptyPayload> {

	private static final Logger logger = LoggerFactory.getLogger(UserPasswordForgetHandler.class);

	private AuthService service = null;

	public UserPasswordForgetHandler(AuthService service) {
		super(EmptyPayload.class);
		this.service = service;
	}

	@Override
	protected Answer processImpl(EmptyPayload value, Map<String, String> urlParams) {
		logger.info("UserPasswordForgetHandler.processImpl enter:");

		if (!urlParams.containsKey(":email")) {
			return Answer.bad_request();
		}

		String email = urlParams.get(":email");

		logger.info("UserPasswordForgetHandler.processImpl urlParams email: " + email);

		try {
			this.service.forgotPassword(email);
		} catch (Exception e) {
			logger.info(String.format("Failed to forgot password for user [%s]", email), e);
			return Answer.error(String.format("Failed to forgot password for user [%s]", email));
		}

		return Answer.ok();
	}

}
