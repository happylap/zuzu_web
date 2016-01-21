package com.lap.zuzuweb.dao;

import java.util.List;
import java.util.Optional;

import com.lap.zuzuweb.model.Device;

public interface DeviceDao 
{
	public List<Device> getAllDevices();
	
	public Optional<Device> getDevice(String userID, String deviceID);
	
	public List<Device> getDevices(String userID);
	
	public String createDevice(Device device);
	
	public String updateDevice(Device device);
	
	public String deleteDevice(String deviceId);
	
	public boolean deleteDevicesByUser(String userId);
}
