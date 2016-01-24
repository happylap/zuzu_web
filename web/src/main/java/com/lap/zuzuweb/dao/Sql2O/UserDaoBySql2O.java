package com.lap.zuzuweb.dao.Sql2O;

import java.util.List;
import java.util.Optional;

import org.sql2o.Connection;

import com.lap.zuzuweb.dao.UserDao;
import com.lap.zuzuweb.model.User;

public class UserDaoBySql2O extends AbstratcDaoBySql2O implements UserDao
{
	static private String SQL_SINGLE_USER = "SELECT user_id, register_time, facebook_id, facebook_name,"
			+ " facebook_email, facebook_picture_url, facebook_first_name, facebook_last_name, facebook_gender, facebook_birthday"
			+ " FROM \"ZuZuUser\" WHERE user_id=:user_id";
	
	static private String SQL_CREATE_USER = "INSERT INTO \"ZuZuUser\"(user_id, register_time, facebook_id, facebook_name,"
			+ " facebook_email, facebook_picture_url, facebook_first_name, facebook_last_name, facebook_gender, facebook_birthday) "
			+ " VALUES (:user_id, :register_time, :facebook_id, :facebook_name, :facebook_email, :facebook_picture_url, "
			+ " :facebook_first_name, :facebook_last_name, :facebook_gender, :facebook_birthday,)";
	
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
                    .addParameter("user_id", user.getUser_id())
                    .addParameter("register_time", user.getRegister_time())
                    .addParameter("facebook_id", user.getFacebook_id())
                    .addParameter("facebook_name", user.getFacebook_name())
                    .addParameter("facebook_email", user.getFacebook_email())
                    .addParameter("facebook_picture_url", user.getFacebook_picture_url())
                    .addParameter("facebook_first_name", user.getFacebook_first_name())
                    .addParameter("facebook_last_name", user.getFacebook_last_name())
                    .addParameter("facebook_birthday", user.getFacebook_birthday())
                    .addParameter("facebook_gender", user.getFacebook_gender())
                    .executeUpdate();
            conn.commit();
            return user.getUser_id();
        }
	}

}
