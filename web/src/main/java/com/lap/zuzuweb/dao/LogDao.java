package com.lap.zuzuweb.dao;

import java.util.Optional;

import com.lap.zuzuweb.model.Log;

public interface LogDao {
	public Optional<Log> getLog(String deviceId, String userId);

	public String createLog(Log log);
}
