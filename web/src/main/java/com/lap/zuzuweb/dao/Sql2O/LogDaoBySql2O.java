package com.lap.zuzuweb.dao.Sql2O;

import java.util.List;
import java.util.Optional;

import org.sql2o.Connection;

import com.lap.zuzuweb.dao.LogDao;
import com.lap.zuzuweb.model.Log;

public class LogDaoBySql2O extends AbstratcDaoBySql2O implements LogDao
{
	static private String SQL_SINGLE_LOG = "SELECT device_id, user_id, log_type, log_comment, log_time"
			+ " FROM \"ZuzuLog\" WHERE device_id=:device_id and user_id=:user_id";
	
	static private String SQL_CREATE_LOG = "INSERT INTO \"ZuzuLog\"(device_id, user_id, log_type, log_comment, log_time) "
			+ " VALUES (:device_id, :user_id, :log_type, :log_comment, :log_time)";
	
	@Override
	public Optional<Log> getLog(String deviceID, String userID)
	{
        try (Connection conn = sql2o.open()) {
            List<Log> logs = conn.createQuery(SQL_SINGLE_LOG)
                    .addParameter("device_id", deviceID)
                    .addParameter("user_id", userID)
                    .executeAndFetch(Log.class);
            if (logs.size() == 0) {
                return Optional.empty();
            } else if (logs.size() == 1) {
                return Optional.of(logs.get(0));
            } else {
                throw new RuntimeException();
            }
        }
	}

	@Override
	public String createLog(Log log) {
        try (Connection conn = sql2o.beginTransaction()) {
            conn.createQuery(SQL_CREATE_LOG)
            		.addParameter("device_id", log.getDevice_id())
                    .addParameter("user_id", log.getUser_id())
                    .addParameter("log_type", log.getLog_type())
                    .addParameter("log_comment", log.getLog_comment())
                    .addParameter("log_time", log.getLog_time())
                    .executeUpdate();
            conn.commit();
            return log.getDevice_id();
        }
	}

}
