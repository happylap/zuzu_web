package com.lap.zuzuweb.handler.user;

import java.util.Map;

import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.UserUpdatePayload;
import com.lap.zuzuweb.service.UserService;

public class UserUpdateHandler extends AbstractRequestHandler<UserUpdatePayload> {

	private UserService service = null;

	public UserUpdateHandler(UserService service) {
		super(UserUpdatePayload.class);
		this.service = service;
	}

	@Override
	protected Answer processImpl(UserUpdatePayload value, Map<String, String> urlParams) {
		service.updateUser(value.toUser());
		return Answer.ok();
	}
}