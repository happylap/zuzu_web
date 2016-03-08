package com.lap.zuzuweb.handler.device;

import java.util.Map;

import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.DeviceCreatePayload;
import com.lap.zuzuweb.service.DeviceService;
import com.lap.zuzuweb.util.CommonUtils;

public class DeviceCreateHandler extends AbstractRequestHandler<DeviceCreatePayload>{

	private DeviceService service = null;
	
	public DeviceCreateHandler(DeviceService service) {
        super(DeviceCreatePayload.class);
        this.service = service;
    }
	@Override
	protected Answer processImpl(DeviceCreatePayload value, Map<String, String> urlParams) {
		if (!urlParams.containsKey(":provider") || !urlParams.containsKey(":userid")) {
			return Answer.bad_request();
		}
		
		String userId = CommonUtils.combineUserID(urlParams.get(":provider"), urlParams.get(":userid"));
		value.setUser_id(userId);
		
        String deviceId = service.createDevice(value);
        
        return Answer.ok(deviceId);
	}

}
