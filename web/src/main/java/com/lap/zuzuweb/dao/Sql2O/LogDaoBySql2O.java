package com.lap.zuzuweb.dao.Sql2O;

import java.util.Date;
import java.util.Optional;

import org.sql2o.Connection;

import com.lap.zuzuweb.dao.LogDao;
import com.lap.zuzuweb.model.Log;

public class LogDaoBySql2O extends AbstratcDaoBySql2O implements LogDao
{
	
	static private String SQL_CREATE_LOG = "INSERT INTO \"ZuzuLog\"(device_id, user_id, log_type, log_comment, log_time) "
			+ " VALUES (:device_id, :user_id, :log_type, :log_comment, :log_time)";
	
	static private String SQL_QUERY_LOG_BY_USERID_AND_TYPE = "SELECT device_id, user_id, log_type, log_comment, log_time" 
			+ " FROM \"ZuzuLog\" WHERE user_id=:user_id AND log_type=:log_type ORDER BY log_time desc";

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
	
	@Override
	public Optional<Date> getLatestNotifyTime(String userId) {
		try (Connection conn = sql2o.open()) {
            Log firstLog = conn.createQuery(SQL_QUERY_LOG_BY_USERID_AND_TYPE)
                    .addParameter("user_id", userId)
                    .addParameter("log_type", Log.Type.RECEIVE_NOTIFY_TIME.name())
                    .executeAndFetchFirst(Log.class);
            
            if (firstLog != null && firstLog.getLog_time() != null) {
            	return Optional.of(firstLog.getLog_time());
            }
            
            return Optional.empty();
        }
	}

}
