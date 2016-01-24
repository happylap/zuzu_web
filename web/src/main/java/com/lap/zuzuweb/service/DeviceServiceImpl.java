package com.lap.zuzuweb.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.lap.zuzuweb.dao.DeviceDao;
import com.lap.zuzuweb.model.Device;
import com.lap.zuzuweb.model.User;

public class DeviceServiceImpl implements DeviceService
{
	private DeviceDao dao = null;
	
	public DeviceServiceImpl(DeviceDao dao)
	{
		this.dao = dao;
	}

	@Override
	public List<Device> getAllDevices() {
		return this.dao.getAllDevices();
	}
	
	@Override
	public List<Device> getDevices(String userID) {
		return this.dao.getDevices(userID);
	}

	@Override
	public String createDevice(Device device) {
        Optional<Device> exist = this.dao.getDevice(device.getUser_id(), device.getDevice_id());
        if (exist.isPresent()) {
            return exist.get().getDevice_id();
        }
		device.setRegister_time(new Date());
		return this.dao.createDevice(device);
	}

	@Override
	public String updateDevice(Device device) {
		return this.dao.updateDevice(device);
	}
	
	@Override
	public String deleteDevice(String deviceId) {
		return this.dao.deleteDevice(deviceId);
	}

	@Override
	public boolean deleteDevicesByUser(String userId) {
		return this.dao.deleteDevicesByUser(userId);
	}

}
