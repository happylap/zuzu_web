package com.lap.zuzuweb.handler.device;

import java.util.Map;

import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.DeviceUpdatePayload;
import com.lap.zuzuweb.service.DeviceService;

public class DeviceUpdateHandler extends AbstractRequestHandler<DeviceUpdatePayload>{

	private DeviceService service = null;
	
	public DeviceUpdateHandler(DeviceService service) {
        super(DeviceUpdatePayload.class);
        this.service = service;
    }
	@Override
	protected Answer processImpl(DeviceUpdatePayload value, Map<String, String> urlParams) {
        String deviceId = service.updateDevice(value);
        return new Answer(201, deviceId);
	}

}