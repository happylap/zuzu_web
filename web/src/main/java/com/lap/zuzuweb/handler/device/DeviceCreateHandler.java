package com.lap.zuzuweb.handler.device;

import java.util.Map;

import com.lap.zuzuweb.ZuzuLogger;
import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.DeviceCreatePayload;
import com.lap.zuzuweb.service.DeviceService;

public class DeviceCreateHandler extends AbstractRequestHandler<DeviceCreatePayload>{

	private static final ZuzuLogger logger = ZuzuLogger.getLogger(DeviceCreateHandler.class);
	
	private DeviceService service = null;
	
	public DeviceCreateHandler(DeviceService service) {
        super(DeviceCreatePayload.class);
        this.service = service;
    }
	@Override
	protected Answer processImpl(DeviceCreatePayload value, Map<String, String> urlParams) {
		logger.entering("processImpl", "{value: %s, urlParams: %s}", value, urlParams);
		
        String deviceId = service.createDevice(value);
        return Answer.ok(deviceId);
	}

}
