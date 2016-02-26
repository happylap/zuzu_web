package com.lap.zuzuweb.handler.user;

import java.util.Map;
import java.util.Optional;

import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.EmptyPayload;
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
    	if (!urlParams.containsKey(":userid")) {
            return Answer.bad_request();
        }

    	String userID = urlParams.get(":userid");
        Optional<User> user = this.service.getUser(userID);
        
        if (user.isPresent()) {
            return Answer.ok(user.get());
        }
        
        return Answer.no_data();
    }

}

