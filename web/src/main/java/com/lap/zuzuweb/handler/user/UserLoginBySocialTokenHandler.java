package com.lap.zuzuweb.handler.user;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.UserLoginBySocialTokenPayload;
import com.lap.zuzuweb.model.User;
import com.lap.zuzuweb.service.AuthService;
import com.lap.zuzuweb.service.UserService;

public class UserLoginBySocialTokenHandler extends AbstractRequestHandler<UserLoginBySocialTokenPayload> {

	private static final Logger logger = LoggerFactory.getLogger(UserLoginBySocialTokenHandler.class);

	private AuthService service = null;

	private UserService userSvc = null;

	public UserLoginBySocialTokenHandler(AuthService service, UserService userSvc) {
		super(UserLoginBySocialTokenPayload.class);
		this.service = service;
		this.userSvc = userSvc;
	}

	@Override
    protected Answer processImpl(UserLoginBySocialTokenPayload value, Map<String, String> urlParams) {
    	
    	Answer answer;
    	
    	try {
            logger.info("Validate parameters");
            String provider = value.getProvider();
            String accessToken = value.getAccess_token();
            
            logger.info(String.format("login with provider [%s], accessToken [%s]", provider, accessToken));
            
            logger.info("Validate request");
            String userId = service.validateLoginRequest(provider, accessToken);
            
            logger.info("Get user for userId: " + userId);
            
            Optional<User> existUser = this.userSvc.getUserById(userId);
            
            if (!existUser.isPresent()) {
            	 throw new Exception("Failed to find user by userId: " + userId);
            }
            
            User user = existUser.get();
            
            Map<String, String> data = new HashMap<String, String>();
            data.put("userId", user.getUser_id());
            data.put("email", user.getEmail());
            data.put("zuzuToken", user.getZuzu_token());
            answer = Answer.ok(data);
            
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            answer = Answer.error(e.getMessage());
        }
    	
        logger.info("leaving login request");
    	
        return answer;
    }
}
