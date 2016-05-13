package com.lap.zuzuweb.service;

import java.util.Date;

import com.lap.zuzuweb.ZuzuLogger;
import com.lap.zuzuweb.dao.LogDao;
import com.lap.zuzuweb.model.Log;

public class LogServiceImpl implements LogService {

	private static final ZuzuLogger logger = ZuzuLogger.getLogger(LogServiceImpl.class);
	
	private LogDao dao = null;

	public LogServiceImpl(LogDao dao) {
		this.dao = dao;
	}

	@Override
	public void setReceiveNotifyTime(String deviceID, String userID, Date time) {
		logger.entering("setReceiveNotifyTime", "{deviceID: %s, userID: %s, time: %s}", deviceID, userID, time);
		
		if (time == null) {
			return;
		}
		Log log = new Log();
		log.setDevice_id(deviceID);
		log.setUser_id(userID);
		log.setLog_type(Log.Type.RECEIVE_NOTIFY_TIME.toString());
		log.setLog_comment("");
		log.setLog_time(time);
		this.dao.createLog(log);
	}

	@Override
	public void setRegisterTime(String deviceID, String userID, Date time) {
		logger.entering("setRegisterTime", "{deviceID: %s, userID: %s, time: %s}", deviceID, userID, time);
		
		Log log = new Log();
		log.setDevice_id(deviceID);
		log.setUser_id(userID);
		log.setLog_type(Log.Type.REGISTER_TIME.toString());
		log.setLog_comment("");
		if (time == null) {
			time = new Date();
		}
		log.setLog_time(time);
		this.dao.createLog(log);
	}

}
