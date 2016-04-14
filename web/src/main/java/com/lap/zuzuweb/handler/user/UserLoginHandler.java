package com.lap.zuzuweb.handler.user;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.UserLoginPayload;
import com.lap.zuzuweb.service.AuthService;


public class UserLoginHandler extends AbstractRequestHandler<UserLoginPayload> {
	
	private static final Logger logger = LoggerFactory.getLogger(UserLoginHandler.class);
	
	private AuthService service = null;
	
	public UserLoginHandler(AuthService service) {
        super(UserLoginPayload.class);
        this.service = service;
    }
	
    @Override
    protected Answer processImpl(UserLoginPayload value, Map<String, String> urlParams) {
    	
    	Answer answer;
    	
    	try {
            logger.info("Validate parameters");
            String email = value.getEmail();
            String timestamp = value.getTimestamp();
            String signature = value.getSignature();
            
            logger.info(String.format("login with email [%s], timestamp [%s]", email, timestamp));
            
            logger.info("Validate request");
            service.validateLoginRequest(email, signature, timestamp);

            logger.info("Get zuzuToken for user email: " + email);
            String data = this.service.getZuzuToken(email);
            
            answer = Answer.ok(data);
        } catch (Exception e) {
            logger.error("Failed to access data", e);
            answer = Answer.error(e.getMessage());
        }

        logger.info("leaving login request");
    	
        return answer;
    }
}

