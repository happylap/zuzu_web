package com.lap.zuzuweb.dao.Sql2O;

import java.util.List;
import java.util.Optional;

import org.sql2o.Connection;

import com.lap.zuzuweb.dao.DeviceDao;
import com.lap.zuzuweb.model.Device;

public class DeviceDaoBySql2O extends AbstratcDaoBySql2O implements DeviceDao
{
	static private String SQL_GET_ALL_DEVICES = "SELECT device_id, user_id, register_time, enabled, register_time, receive_notify_time"
			+ " FROM \"Device\" ";
		
	static private String SQL_GET_DEVICES_BY_USER = "SELECT device_id, user_id, register_time, enabled, register_time, receive_notify_time"
			+ " FROM \"Device\" WHERE user_id=:user_id";
	
	static private String SQL_GET_DEVICE = "SELECT device_id, user_id, register_time, enabled, register_time, receive_notify_time"
			+ " FROM \"Device\" WHERE user_id=:user_id AND device_id=:device_id";
	
	static private String SQL_CREATE_DEVICE = "INSERT INTO \"Device\"(device_id, user_id, register_time, enabled)"
			+ " VALUES (:device_id, :user_id, :register_time, :enabled)";
	
	static private String SQL_UPDATE_DEVICE = "UPDATE \"Device\" SET enabled=:enabled, receive_notify_time=:receive_notify_time"
			+ " WHERE device_id=:device_id AND user_id=:user_id";
	
	static private String SQL_REMOVE_DEVICE = "DELETE FROM \"Device\" WHERE device_id=:device_id";
	
	static private String SQL_REMOVE_DEVICE_BY_USER = "DELETE FROM \"Device\" WHERE user_id=:user_id";

	@Override
	public List<Device> getAllDevices() {
        try (Connection conn = sql2o.open()) {
            List<Device> devices = conn.createQuery(SQL_GET_ALL_DEVICES)
                    .executeAndFetch(Device.class);
            return devices;
        }
	}
	
	@Override
	public List<Device> getDevices(String userID)
	{
        try (Connection conn = sql2o.open()) {
            List<Device> devices = conn.createQuery(SQL_GET_DEVICES_BY_USER)
                    .addParameter("user_id", userID)
                    .executeAndFetch(Device.class);
            return devices;
        }
	}

	@Override
	public Optional<Device> getDevice(String userID, String deviceID)
	{
        try (Connection conn = sql2o.open()) {
            List<Device> devices = conn.createQuery(SQL_GET_DEVICE)
                    .addParameter("user_id", userID)
                    .addParameter("device_id", deviceID)
                    .executeAndFetch(Device.class);
            
            if (devices.size() == 0) {
                return Optional.empty();
            } else if (devices.size() == 1) {
                return Optional.of(devices.get(0));
            } else {
                throw new RuntimeException();
            }
        }
        

	}

	@Override
	public String createDevice(Device device) {
        try (Connection conn = sql2o.beginTransaction()) {
            conn.createQuery(SQL_CREATE_DEVICE)
            		.addParameter("device_id", device.getDevice_id())
            		.addParameter("user_id", device.getUser_id())
            		.addParameter("enabled", device.isEnabled())
                    .addParameter("register_time", device.getRegister_time())
                    .executeUpdate();
            conn.commit();
            return device.getDevice_id();
        }
	}

	@Override
	public String updateDevice(Device device) {
        try (Connection conn = sql2o.beginTransaction()) {
            conn.createQuery(SQL_UPDATE_DEVICE)
            		.addParameter("device_id", device.getDevice_id())
            		.addParameter("user_id", device.getUser_id())
            		.addParameter("enabled", device.isEnabled())
                    .addParameter("receive_notify_time", device.getReceive_notify_time())
                    .executeUpdate();
            conn.commit();
            return device.getDevice_id();
        }
	}
	
	
	@Override
	public String deleteDevice(String deviceId) {
        try (Connection conn = sql2o.beginTransaction()) {
            conn.createQuery(SQL_REMOVE_DEVICE)
            		.addParameter("device_id", deviceId)
                    .executeUpdate();
            conn.commit();
            return deviceId;
        }
	}


	@Override
	public boolean deleteDevicesByUser(String userId) {
	       try (Connection conn = sql2o.beginTransaction()) {
	            conn.createQuery(SQL_REMOVE_DEVICE_BY_USER)
	            		.addParameter("user_id", userId)
	                    .executeUpdate();
	            conn.commit();
	            return true;
	        }
	}

}
