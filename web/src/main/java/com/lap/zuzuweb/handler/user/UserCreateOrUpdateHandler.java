package com.lap.zuzuweb.handler.user;

import java.util.Map;

import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.UserCreatePayload;
import com.lap.zuzuweb.service.UserService;
import com.lap.zuzuweb.util.CommonUtils;


public class UserCreateOrUpdateHandler extends AbstractRequestHandler<UserCreatePayload> {
	
	private UserService service = null;
	
	public UserCreateOrUpdateHandler(UserService service) {
        super(UserCreatePayload.class);
        this.service = service;
    }
	
    @Override
    protected Answer processImpl(UserCreatePayload value, Map<String, String> urlParams) {
    	if (!urlParams.containsKey(":provider") || !urlParams.containsKey(":userid")) {
			return Answer.bad_request();
		}
    	
		String userId = CommonUtils.combineUserID(urlParams.get(":provider"), urlParams.get(":userid"));
		
		value.setUser_id(userId);
        
        return Answer.ok(service.createOrUpdateUser(value));
    }
}

