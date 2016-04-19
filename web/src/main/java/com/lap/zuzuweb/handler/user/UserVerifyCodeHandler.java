package com.lap.zuzuweb.handler.user;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.EmptyPayload;
import com.lap.zuzuweb.service.AuthService;

public class UserVerifyCodeHandler extends AbstractRequestHandler<EmptyPayload> {

	private static final Logger logger = LoggerFactory.getLogger(UserVerifyCodeHandler.class);
	
	private AuthService service = null;
	
	public UserVerifyCodeHandler(AuthService service) {
        super(EmptyPayload.class);
        this.service = service;
    }

    @Override
    protected Answer processImpl(EmptyPayload value, Map<String,String> urlParams)
    {
    	logger.info("UserVerifyCodeHandler.processImpl enter:");
    	
    	if (!urlParams.containsKey(":email")) {
			return Answer.bad_request();
		}
		
    	String email = urlParams.get(":email");		
    	String verificationCode = urlParams.get(":verificationcode");
    	
    	logger.info("UserVerifyCodeHandler.processImpl urlParams email: " + email);
    	
        return Answer.ok(this.service.isVerificationCodeValid(email, verificationCode));
    }

}

