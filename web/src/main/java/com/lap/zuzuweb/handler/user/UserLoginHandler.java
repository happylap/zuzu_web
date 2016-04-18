package com.lap.zuzuweb.handler.user;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.UserLoginPayload;
import com.lap.zuzuweb.model.User;
import com.lap.zuzuweb.service.AuthService;
import com.lap.zuzuweb.service.UserService;


public class UserLoginHandler extends AbstractRequestHandler<UserLoginPayload> {
	
	private static final Logger logger = LoggerFactory.getLogger(UserLoginHandler.class);
	
	private AuthService service = null;

	private UserService userSvc = null;
	
	public UserLoginHandler(AuthService service, UserService userSvc) {
        super(UserLoginPayload.class);
        this.service = service;
        this.userSvc = userSvc;
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

            logger.info("Get user for email: " + email);
            //String data = this.service.getZuzuToken(email);
            
            Optional<User> existUser = this.userSvc.getUserByEmail(email);
            
            if (!existUser.isPresent()) {
            	 throw new Exception("Failed to find user by email: " + email);
            }
            
            User user = existUser.get();
            
            Map<String, String> data = new HashMap<String, String>();
            data.put("userId", user.getUser_id());
            data.put("zuzuToken", user.getZuzu_token());
            answer = Answer.ok(data);
            
        } catch (Exception e) {
            logger.error("Failed to access data", e);
            answer = Answer.error(e.getMessage());
        }

        logger.info("leaving login request");
    	
        return answer;
    }
}

