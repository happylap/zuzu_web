package com.lap.zuzuweb.service;

import java.util.List;
import java.util.Optional;

import com.lap.zuzuweb.dao.DeviceDao;
import com.lap.zuzuweb.model.Device;
import com.lap.zuzuweb.util.CommonUtils;

public class DeviceServiceImpl implements DeviceService
{
	private DeviceDao dao = null;
	
	public DeviceServiceImpl(DeviceDao dao)
	{
		this.dao = dao;
	}

	@Override
	public Optional<Device> getDevice(String userId, String deviceId) {
		return this.dao.getDevice(userId, deviceId);
	}
	
	@Override
	public List<Device> getDevices(String userID) {
		return this.dao.getDevices(userID);
	}
	
	@Override
	public List<Device> getValidDevices() {
		return this.dao.getValidDevice();
	}
	
	@Override
	public String createDevice(Device device) {
        Optional<Device> existDevice = this.dao.getDevice(device.getUser_id(), device.getDevice_id());
        if (existDevice.isPresent()) {
            return existDevice.get().getDevice_id();
        }
		device.setRegister_time(CommonUtils.getUTCNow());
		return this.dao.createDevice(device);
	}
	
	@Override
	public String deleteDevice(String userId, String deviceId) {
		return this.dao.deleteDevice(userId, deviceId);
	}

	@Override
	public boolean deleteDevicesByUser(String userId) {
		return this.dao.deleteDevicesByUser(userId);
	}

}
