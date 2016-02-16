package com.lap.zuzuweb.service;

import java.util.Date;

public interface LogService {
	
	public void setReceiveNotifyTime(String deviceID, String userID, Date time);

	public void setRegisterTime(String deviceID, String userID, Date time);
}
