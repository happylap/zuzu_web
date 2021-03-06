package com.lap.zuzuweb.handler.device;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.lap.zuzuweb.ZuzuLogger;
import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.EmptyPayload;
import com.lap.zuzuweb.service.DeviceService;

public class DeviceDeleteHandler extends AbstractRequestHandler<EmptyPayload> {

	private static final ZuzuLogger logger = ZuzuLogger.getLogger(DeviceDeleteHandler.class);
	
	private DeviceService service = null;

	public DeviceDeleteHandler(DeviceService service) {
		super(EmptyPayload.class);
		this.service = service;
	}

	@Override
	protected Answer processImpl(EmptyPayload value, Map<String, String> urlParams) {
		logger.entering("processImpl", "{value: %s, urlParams: %s}", value, urlParams);
		
		if (!urlParams.containsKey(":userid")) {
			return Answer.bad_request();
		}

		String userId = urlParams.get(":userid");
		String deviceId = urlParams.get(":deviceid");

		if (StringUtils.isNotEmpty(userId) && StringUtils.isNotEmpty(deviceId)) {
			service.deleteDevice(userId, deviceId);
		} else if (StringUtils.isNotEmpty(userId)) {
			service.deleteDevicesByUser(userId);
		}

		return Answer.ok();
	}
}