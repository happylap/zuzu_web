package com.lap.zuzuweb.dao.Sql2O;

import java.util.List;
import java.util.Optional;

import org.sql2o.Connection;

import com.lap.zuzuweb.ZuzuLogger;
import com.lap.zuzuweb.dao.DeviceDao;
import com.lap.zuzuweb.model.Device;
import com.lap.zuzuweb.util.CommonUtils;

public class DeviceDaoBySql2O extends AbstratcDaoBySql2O implements DeviceDao
{

	private static final ZuzuLogger logger = ZuzuLogger.getLogger(DeviceDaoBySql2O.class);
	
	static private String SQL_GET_DEVICES_BY_USER = "SELECT device_id, user_id, register_time"
			+ " FROM \"Device\" WHERE user_id=:user_id";
	
	static private String SQL_GET_VALID_DEVICES = "SELECT d.device_id, d.user_id, d.register_time"
			+ " FROM \"Device\" d, \"Criteria\" c, \"ZuzuService\" s"
			+ " WHERE d.user_id = c.user_id AND c.user_id = s.user_id"
			// + " AND c.enabled = true AND s.expire_time > now()"
			+ " AND s.expire_time > now()"
			+ " ORDER BY d.user_id";
	
	static private String SQL_GET_DEVICE = "SELECT device_id, user_id, register_time"
			+ " FROM \"Device\" WHERE user_id=:user_id AND device_id=:device_id";
	
	static private String SQL_GET_DEVICE_BY_DEVICE_ID = "SELECT device_id, user_id, register_time"
			+ " FROM \"Device\" WHERE device_id=:device_id";
	
	static private String SQL_CREATE_DEVICE = "INSERT INTO \"Device\"(device_id, user_id, register_time, update_time)"
			+ " VALUES (:device_id, :user_id, :register_time, :update_time)";
	
	static private String SQL_UPDATE_DEVICE = "UPDATE \"Device\" SET user_id=:user_id, update_time=:update_time"
			+ " WHERE device_id=:device_id";
	
	static private String SQL_REMOVE_DEVICE = "DELETE FROM \"Device\" WHERE user_id=:user_id AND device_id=:device_id";
	
	static private String SQL_REMOVE_DEVICE_BY_USER = "DELETE FROM \"Device\" WHERE user_id=:user_id";
	
	@Override
	public List<Device> getDevices(String userID)
	{
		logger.entering("getDevices", "{userID: %s}", userID);
		
        try (Connection conn = sql2o.open()) {
            List<Device> devices = conn.createQuery(SQL_GET_DEVICES_BY_USER)
                    .addParameter("user_id", userID)
                    .executeAndFetch(Device.class);
            return devices;
        }
	}

	public List<Device> getValidDevice() {
		logger.entering("getValidDevice");
		
		try (Connection conn = sql2o.open()) {
            List<Device> devices = conn.createQuery(SQL_GET_VALID_DEVICES)
                    .executeAndFetch(Device.class);
            return devices;
        }
	}

	@Override
	public Optional<Device> getDevice(String userID, String deviceID)
	{
		logger.entering("getDevice", "{userID: %s, deviceID: %s}", userID, deviceID);
		
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
	public Optional<Device> getDevice(String deviceID) {
		logger.entering("getDevice", "{deviceID: %s}", deviceID);
		
		Optional<Device> device = Optional.empty();
		
        try (Connection conn = sql2o.open()) {
            List<Device> devices = conn.createQuery(SQL_GET_DEVICE_BY_DEVICE_ID)
                    .addParameter("device_id", deviceID)
                    .executeAndFetch(Device.class);
            
            if (devices.size() == 1) {
                device = Optional.of(devices.get(0));
            } else {
                throw new RuntimeException();
            }
        }
        
        logger.exit("getDevice", "%s", device);
        return device;
	}

	@Override
	public String createDevice(Device device) {

		logger.entering("createDevice", "{device: %s}", device);
		
        try (Connection conn = sql2o.beginTransaction()) {
            conn.createQuery(SQL_CREATE_DEVICE)
            		.addParameter("device_id", device.getDevice_id())
            		.addParameter("user_id", device.getUser_id())
                    .addParameter("register_time", device.getRegister_time())
                    .addParameter("update_time", CommonUtils.getUTCNow())
                    .executeUpdate();
            conn.commit();
            return device.getDevice_id();
        }
	}
	
	@Override
	public String updateDevice(Device device) {
		logger.entering("updateDevice", "{device: %s}", device);
		
        try (Connection conn = sql2o.beginTransaction()) {
            conn.createQuery(SQL_UPDATE_DEVICE)
            		.addParameter("user_id", device.getUser_id())
                    .addParameter("update_time", CommonUtils.getUTCNow())
            		.addParameter("device_id", device.getDevice_id())
                    .executeUpdate();
            conn.commit();
            return device.getDevice_id();
        }
	}
	
	@Override
	public String deleteDevice(String userId, String deviceId) {
		logger.entering("deleteDevice", "{userID: %s, deviceID: %s}", userId, deviceId);
		
        try (Connection conn = sql2o.beginTransaction()) {
            conn.createQuery(SQL_REMOVE_DEVICE)
    				.addParameter("user_id", userId)
            		.addParameter("device_id", deviceId)
                    .executeUpdate();
            conn.commit();
            return deviceId;
        }
	}

	@Override
	public boolean deleteDevicesByUser(String userId) {
		logger.entering("deleteDevicesByUser", "{userID: %s}", userId);
		
        try (Connection conn = sql2o.beginTransaction()) {
            conn.createQuery(SQL_REMOVE_DEVICE_BY_USER)
            		.addParameter("user_id", userId)
                    .executeUpdate();
            conn.commit();
            return true;
        }
	}

}
