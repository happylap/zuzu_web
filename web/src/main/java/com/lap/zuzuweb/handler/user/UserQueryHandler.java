package com.lap.zuzuweb.handler.user;

import java.util.Map;
import java.util.Optional;

import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.EmptyPayload;
import com.lap.zuzuweb.model.User;
import com.lap.zuzuweb.service.UserService;
import com.lap.zuzuweb.util.CommonUtils;

public class UserQueryHandler extends AbstractRequestHandler<EmptyPayload> {

	private UserService service = null;
	
	public UserQueryHandler(UserService service) {
        super(EmptyPayload.class);
        this.service = service;
    }

    @Override
    protected Answer processImpl(EmptyPayload value, Map<String,String> urlParams)
    {
    	if (!urlParams.containsKey(":provider") || !urlParams.containsKey(":userid")) {
			return Answer.bad_request();
		}
		
		String userId = CommonUtils.combineUserID(urlParams.get(":provider"), urlParams.get(":userid"));
    	
        Optional<User> user = this.service.getUserById(userId);
        
        if (user.isPresent()) {
            return Answer.ok(user.get());
        }
        
        return Answer.no_data();
    }

}

