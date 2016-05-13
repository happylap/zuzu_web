package com.lap.zuzuweb.handler.user;

import java.util.Map;

import com.lap.zuzuweb.ZuzuLogger;
import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.EmptyPayload;
import com.lap.zuzuweb.service.AuthService;

public class UserVerifyCodeHandler extends AbstractRequestHandler<EmptyPayload> {

	private static final ZuzuLogger logger = ZuzuLogger.getLogger(UserVerifyCodeHandler.class);
	
	private AuthService service = null;
	
	public UserVerifyCodeHandler(AuthService service) {
        super(EmptyPayload.class);
        this.service = service;
    }

    @Override
    protected Answer processImpl(EmptyPayload value, Map<String,String> urlParams)
    {
    	logger.entering("processImpl", "{value: %s, urlParams: %s}", value, urlParams);
    	
    	if (!urlParams.containsKey(":email")) {
			return Answer.bad_request();
		}
		
    	String email = urlParams.get(":email");		
    	String verificationCode = urlParams.get(":verificationcode");
    	
    	logger.info("UserVerifyCodeHandler.processImpl urlParams email: " + email);
    	
        return Answer.ok(this.service.isVerificationCodeValid(email, verificationCode));
    }

}

