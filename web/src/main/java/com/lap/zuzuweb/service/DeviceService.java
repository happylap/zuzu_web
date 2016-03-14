package com.lap.zuzuweb.service;

import java.util.List;
import java.util.Optional;

import com.lap.zuzuweb.model.Device;

public interface DeviceService {

	public Optional<Device> getDevice(String userId, String deviceId);

	public List<Device> getDevices(String userId);

	public List<Device> getValidDevices();

	public String createDevice(Device device);

	public String deleteDevice(String userId, String deviceId);

	public boolean deleteDevicesByUser(String userId);
}
