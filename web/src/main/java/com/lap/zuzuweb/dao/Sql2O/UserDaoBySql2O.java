package com.lap.zuzuweb.dao.Sql2O;

import java.util.List;
import java.util.Optional;

import org.sql2o.Connection;

import com.lap.zuzuweb.dao.UserDao;
import com.lap.zuzuweb.model.User;

public class UserDaoBySql2O extends AbstratcDaoBySql2O implements UserDao
{
	static private String SQL_GET_USER_BY_USERID = "SELECT user_id, email, register_time, name, gender, birthday, picture_url, purchase_receipt, update_time"
			+ " FROM \"ZuZuUser\" WHERE user_id=:user_id";
	
	static private String SQL_GET_USER_BY_EMAIL = "SELECT user_id, email, register_time, name, gender, birthday, picture_url, purchase_receipt, update_time"
			+ " FROM \"ZuZuUser\" WHERE email=:email";
	
	
	static private String SQL_CREATE_USER = "INSERT INTO \"ZuZuUser\"(user_id, email, register_time, name, gender, birthday, picture_url, update_time) "
			+ " VALUES (:user_id, :email, :register_time, :name, :gender, :birthday, :picture_url, :update_time)";
	
	static private String SQL_UPDATE_USER = "UPDATE \"ZuZuUser\" SET name=:name, gender=:gender, birthday=:birthday, picture_url=:picture_url, update_time=:update_time"
			+ " WHERE user_id=:user_id";
	
	static private String SQL_REMOVE_USER_BY_ID_AND_EMAIL = "DELETE FROM \"ZuZuUser\" Where user_id=:user_id AND email=:email";

	@Override
	public Optional<User> getUserByEmail(String email) {
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
	public String createUser(User user) {
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
                .executeUpdate();
            conn.commit();
            return user.getUser_id();
        }
	}
	
	@Override
	public String updateUser(User user) {
        try (Connection conn = sql2o.beginTransaction()) {
            conn.createQuery(SQL_UPDATE_USER)
            	//.addParameter("email", user.getEmail())
	            .addParameter("name", user.getName())
	            .addParameter("gender", user.getGender())
	            .addParameter("birthday", user.getBirthday())
	            .addParameter("picture_url", user.getPicture_url())
	            .addParameter("update_time", user.getUpdate_time())
	            .addParameter("user_id", user.getUser_id())
                .executeUpdate();
            conn.commit();
            return user.getUser_id();
        }
	}
	
	@Override
	public void deleteUserByIdAndEmail(String userID, String email) {
		try (Connection conn = sql2o.beginTransaction()) {
            conn.createQuery(SQL_REMOVE_USER_BY_ID_AND_EMAIL)
	            .addParameter("user_id", userID)
	            .addParameter("email", email)
                .executeUpdate();
            conn.commit();
        }
	}

}
