package com.lap.zuzuweb.dao.Sql2O;

import org.sql2o.Connection;

import com.lap.zuzuweb.dao.LogDao;
import com.lap.zuzuweb.model.Log;

public class LogDaoBySql2O extends AbstratcDaoBySql2O implements LogDao
{
	
	static private String SQL_CREATE_LOG = "INSERT INTO \"ZuzuLog\"(device_id, user_id, log_type, log_comment, log_time) "
			+ " VALUES (:device_id, :user_id, :log_type, :log_comment, :log_time)";

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
