package com.lap.zuzuweb.handler.user;

import java.util.Map;

import com.lap.zuzuweb.exception.DataAccessException;
import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.UserCreatePayload;
import com.lap.zuzuweb.service.UserService;

public class UserCreateHandler extends AbstractRequestHandler<UserCreatePayload> {

	private UserService service = null;

	public UserCreateHandler(UserService service) {
		super(UserCreatePayload.class);
		this.service = service;
	}

	@Override
	protected Answer processImpl(UserCreatePayload value, Map<String, String> urlParams) {

		try {
			service.createUser(value.toUser());
			return Answer.ok();
		} catch (DataAccessException e) {
			return Answer.error(e.getMessage());
		}

	}
}
