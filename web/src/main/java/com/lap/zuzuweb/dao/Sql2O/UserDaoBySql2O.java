package com.lap.zuzuweb.dao.Sql2O;

import java.util.List;
import java.util.Optional;

import org.sql2o.Connection;

import com.lap.zuzuweb.dao.UserDao;
import com.lap.zuzuweb.model.User;

public class UserDaoBySql2O extends AbstratcDaoBySql2O implements UserDao
{
	static private String SQL_SINGLE_USER = "SELECT user_id, register_time, provider, email, name, gender, birthday, picture_url"
			+ " FROM \"ZuZuUser\" WHERE user_id=:user_id";
	
	static private String SQL_CREATE_USER = "INSERT INTO \"ZuZuUser\"(user_id, register_time, provider, email, name, gender, birthday, picture_url) "
			+ " VALUES (:user_id, :register_time, :provider, :email, :name, :gender, :birthday, :picture_url)";
	
	static private String SQL_UPDATE_USER = "UPDATE \"ZuZuUser\" SET provider=:provider, email=:email, name=:name, gender=:gender, birthday=:birthday, picture_url=:picture_url"
			+ " WHERE user_id=:user_id";
	
	@Override
	public Optional<User> getUser(String userID)
	{
        try (Connection conn = sql2o.open()) {
            List<User> users = conn.createQuery(SQL_SINGLE_USER)
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
            		.addParameter("provider", user.getProvider())
                    .addParameter("user_id", user.getUser_id())
                    .addParameter("email", user.getEmail())
		            .addParameter("name", user.getName())
		            .addParameter("gender", user.getGender())
		            .addParameter("birthday", user.getBirthday())
		            .addParameter("picture_url", user.getPicture_url())
                    .addParameter("register_time", user.getRegister_time())
                    .executeUpdate();
            conn.commit();
            return user.getUser_id();
        }
	}
	
	@Override
	public String updateUser(User user) {
        try (Connection conn = sql2o.beginTransaction()) {
            conn.createQuery(SQL_UPDATE_USER)
    				.addParameter("provider", user.getProvider())
		            .addParameter("email", user.getEmail())
		            .addParameter("name", user.getName())
		            .addParameter("gender", user.getGender())
		            .addParameter("birthday", user.getBirthday())
		            .addParameter("picture_url", user.getPicture_url())
		            .addParameter("user_id", user.getUser_id())
                    .executeUpdate();
            conn.commit();
            return user.getUser_id();
        }
	}

}
