package com.lap.zuzuweb.dao;

import java.util.Date;
import java.util.Optional;

import com.lap.zuzuweb.model.Log;

public interface LogDao {
	public String createLog(Log log);
	public Optional<Date> getLatestNotifyTime(String userId);
}
