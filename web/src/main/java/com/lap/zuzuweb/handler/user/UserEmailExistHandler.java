package com.lap.zuzuweb.handler.user;

import java.util.Map;
import java.util.Optional;

import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.EmptyPayload;
import com.lap.zuzuweb.model.User;
import com.lap.zuzuweb.service.UserService;

public class UserEmailExistHandler extends AbstractRequestHandler<EmptyPayload> {

	private UserService service = null;
	
	public UserEmailExistHandler(UserService service) {
        super(EmptyPayload.class);
        this.service = service;
    }

    @Override
    protected Answer processImpl(EmptyPayload value, Map<String,String> urlParams)
    {
    	if (!urlParams.containsKey(":email")) {
			return Answer.bad_request();
		}
		
    	String email = urlParams.get(":email");
    	
        Optional<User> existUser = this.service.getUserByEmail(email);
        
        return Answer.ok(existUser.isPresent());
    }

}

