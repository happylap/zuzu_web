package com.lap.zuzuweb.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.lap.zuzuweb.model.Device;

public interface DeviceService 
{
	public List<Device> getAllDevices();
	
	public List<Device> getDevices(String userID);
	
	public String createDevice(Device device);
	
	public String updateDevice(Device device);
	
	public String deleteDevice(String deviceId);
	
	public boolean deleteDevicesByUser(String userId);
	
	public void setReceiveNotifyTime(String deviceId, String userId, Date receiveNotifyTime);
}
