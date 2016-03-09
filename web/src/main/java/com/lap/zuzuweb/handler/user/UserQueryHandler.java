package com.lap.zuzuweb.handler.user;

import java.util.Map;
import java.util.Optional;

import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.EmptyPayload;
import com.lap.zuzuweb.handler.payload.UserQueryPayload;
import com.lap.zuzuweb.model.User;
import com.lap.zuzuweb.service.UserService;

public class UserQueryHandler extends AbstractRequestHandler<EmptyPayload> {

	private UserService service = null;
	
	public UserQueryHandler(UserService service) {
        super(EmptyPayload.class);
        this.service = service;
    }

    @Override
    protected Answer processImpl(EmptyPayload value, Map<String,String> urlParams)
    {
    	Optional<User> existUser = Optional.empty();
    	
    	if (urlParams.containsKey(":userid")) {
    		existUser = this.service.getUserById(urlParams.get(":userid"));
    	} else if (urlParams.containsKey(":email")) {
    		existUser = this.service.getUserByEmail(urlParams.get(":email"));
    	} else {
    		return Answer.bad_request();
    	}
    	
		if (existUser.isPresent()) {
        	UserQueryPayload userPayload = new UserQueryPayload();
        	userPayload.fromUser(existUser.get());
            return Answer.ok(userPayload);
        }
        
        return Answer.no_data();
    }

}

