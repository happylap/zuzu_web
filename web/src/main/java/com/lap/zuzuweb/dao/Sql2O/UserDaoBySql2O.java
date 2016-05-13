package com.lap.zuzuweb.dao.Sql2O;

import java.util.List;
import java.util.Optional;

import org.sql2o.Connection;

import com.lap.zuzuweb.ZuzuLogger;
import com.lap.zuzuweb.dao.UserDao;
import com.lap.zuzuweb.model.User;

public class UserDaoBySql2O extends AbstratcDaoBySql2O implements UserDao
{
	private static final ZuzuLogger logger = ZuzuLogger.getLogger(UserDaoBySql2O.class);
	
	static private String SQL_GET_USER_BY_USERID = "SELECT user_id, email, register_time, name, gender, birthday, picture_url, purchase_receipt, update_time, provider, hashed_password, zuzu_token, verification_code, verify_expire_time"
			+ " FROM \"ZuZuUser\" WHERE user_id=:user_id";
	
	static private String SQL_GET_USER_BY_EMAIL = "SELECT user_id, email, register_time, name, gender, birthday, picture_url, purchase_receipt, update_time, provider, hashed_password, zuzu_token, verification_code, verify_expire_time"
			+ " FROM \"ZuZuUser\" WHERE email=:email";
	
	static private String SQL_GET_USER_BY_TOKEN = "SELECT user_id, email, register_time, name, gender, birthday, picture_url, purchase_receipt, update_time, provider, hashed_password, zuzu_token, verification_code, verify_expire_time"
			+ " FROM \"ZuZuUser\" WHERE zuzu_token=:zuzu_token";
	
	static private String SQL_CREATE_USER = "INSERT INTO \"ZuZuUser\"(user_id, email, register_time, name, gender, birthday, picture_url, update_time, provider, hashed_password, zuzu_token, verification_code, verify_expire_time) "
			+ " VALUES (:user_id, :email, :register_time, :name, :gender, :birthday, :picture_url, :update_time, :provider, :hashed_password, :zuzu_token, :verification_code, :verify_expire_time)";
	
	static private String SQL_UPDATE_USER = "UPDATE \"ZuZuUser\" SET name=:name, gender=:gender, birthday=:birthday, picture_url=:picture_url, update_time=:update_time, provider=:provider, hashed_password=:hashed_password, zuzu_token=:zuzu_token, verification_code=:verification_code, verify_expire_time=:verify_expire_time"
			+ " WHERE user_id=:user_id";
	
	static private String SQL_LINK_USER = "UPDATE \"ZuZuUser\" SET email=:email, name=:name, gender=:gender, birthday=:birthday, picture_url=:picture_url, update_time=:update_time, provider=:provider, hashed_password=:hashed_password, zuzu_token=:zuzu_token, verification_code=:verification_code, verify_expire_time=:verify_expire_time"
			+ " WHERE user_id=:user_id";
	
	
	static private String SQL_REMOVE_USER_BY_ID_AND_EMAIL = "DELETE FROM \"ZuZuUser\" Where user_id=:user_id AND email=:email";
	
	@Override
	public Optional<User> getUserByEmail(String email) 
	{
		logger.entering("getUserByEmail", "{email: %s}", email);
		
		try (Connection conn = sql2o.open()) {
            User user = conn.createQuery(SQL_GET_USER_BY_EMAIL)
                .addParameter("email", email)
                .executeAndFetchFirst(User.class);
            
            return user != null ? Optional.of(user) : Optional.empty();
        }
	}
	
	
	@Override
	public Optional<User> getUserById(String userID)
	{
		logger.entering("getUserById", "{userID: %s}", userID);
		
        try (Connection conn = sql2o.open()) {
            List<User> users = conn.createQuery(SQL_GET_USER_BY_USERID)
                .addParameter("user_id", userID)
                .executeAndFetch(User.class);
    		
            if (users.size() == 0) {
                return Optional.empty();
            } else if (users.size() == 1) {
                return Optional.of(users.get(0));
            } else {
                throw new RuntimeException();
            }
        }
	}

	@Override
	public Optional<User> getUserByToken(String zuzuToken) 
	{
		logger.entering("getUserByToken", "{zuzuToken: %s}", zuzuToken);
		
		try (Connection conn = sql2o.open()) {
            List<User> users = conn.createQuery(SQL_GET_USER_BY_TOKEN)
                .addParameter("zuzu_token", zuzuToken)
                .executeAndFetch(User.class);
            
            if (users.size() == 0) {
                return Optional.empty();
            } else if (users.size() == 1) {
                return Optional.of(users.get(0));
            } else {
                throw new RuntimeException();
            }
        }
	}

	@Override
	public String createUser(User user) {

		logger.entering("createUser", "{user: %s}", user);
		
        try (Connection conn = sql2o.beginTransaction()) {
            conn.createQuery(SQL_CREATE_USER)
                .addParameter("user_id", user.getUser_id())
                .addParameter("email", user.getEmail())
                .addParameter("register_time", user.getRegister_time())
	            .addParameter("name", user.getName())
	            .addParameter("gender", user.getGender())
	            .addParameter("birthday", user.getBirthday())
	            .addParameter("picture_url", user.getPicture_url())
	            .addParameter("update_time", user.getUpdate_time())
	            .addParameter("provider", user.getProvider())
	            .addParameter("hashed_password", user.getHashed_password())
	            .addParameter("zuzu_token", user.getZuzu_token())
	            .addParameter("verification_code", user.getVerification_code())
	            .addParameter("verify_expire_time", user.getVerify_expire_time())
                .executeUpdate();
            
            conn.commit();
            return user.getUser_id();
        }
	}
	
	@Override
	public String updateUser(User user) {
		logger.entering("updateUser", "{user: %s}", user);
		
        try (Connection conn = sql2o.beginTransaction()) {
            conn.createQuery(SQL_UPDATE_USER)
	            .addParameter("name", user.getName())
	            .addParameter("gender", user.getGender())
	            .addParameter("birthday", user.getBirthday())
	            .addParameter("picture_url", user.getPicture_url())
	            .addParameter("update_time", user.getUpdate_time())
	            .addParameter("provider", user.getProvider())
            	.addParameter("hashed_password", user.getHashed_password())
            	.addParameter("zuzu_token", user.getZuzu_token())
	            .addParameter("verification_code", user.getVerification_code())
	            .addParameter("verify_expire_time", user.getVerify_expire_time())
	            .addParameter("user_id", user.getUser_id())
                .executeUpdate();
            	
            conn.commit();
            return user.getUser_id();
        }
	}
	
	@Override
	public String linkUser(User user) {
		logger.entering("linkUser", "{user: %s}", user);
		
        try (Connection conn = sql2o.beginTransaction()) {
            conn.createQuery(SQL_LINK_USER)
            	.addParameter("email", user.getEmail())
	            .addParameter("name", user.getName())
	            .addParameter("gender", user.getGender())
	            .addParameter("birthday", user.getBirthday())
	            .addParameter("picture_url", user.getPicture_url())
	            .addParameter("update_time", user.getUpdate_time())
	            .addParameter("provider", user.getProvider())
            	.addParameter("hashed_password", user.getHashed_password())
            	.addParameter("zuzu_token", user.getZuzu_token())
	            .addParameter("verification_code", user.getVerification_code())
	            .addParameter("verify_expire_time", user.getVerify_expire_time())
	            .addParameter("user_id", user.getUser_id())
                .executeUpdate();
            	
            conn.commit();
            return user.getUser_id();
        }
	}
	
	@Override
	public void deleteUserByIdAndEmail(String userID, String email) {
		logger.entering("deleteUserByIdAndEmail", "{userID: %s, email: %s}", userID, email);
		
		try (Connection conn = sql2o.beginTransaction()) {
            conn.createQuery(SQL_REMOVE_USER_BY_ID_AND_EMAIL)
	            .addParameter("user_id", userID)
	            .addParameter("email", email)
                .executeUpdate();
            conn.commit();
        }
	}

}
