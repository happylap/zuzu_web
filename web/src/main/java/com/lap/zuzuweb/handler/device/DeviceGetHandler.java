package com.lap.zuzuweb.handler.device;

import java.util.Map;
import java.util.Optional;

import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.EmptyPayload;
import com.lap.zuzuweb.model.Device;
import com.lap.zuzuweb.service.DeviceService;

public class DeviceGetHandler extends AbstractRequestHandler<EmptyPayload>{

	private DeviceService service = null;
	
	public DeviceGetHandler(DeviceService service) {
        super(EmptyPayload.class);
        this.service = service;
    }
	
	@Override
	protected Answer processImpl(EmptyPayload value, Map<String, String> urlParams) {
    	
		if (!urlParams.containsKey(":userid") || !urlParams.containsKey(":deviceid")) {
			return Answer.bad_request();
		}
		
		String userId = urlParams.get(":userid");
    	String deviceId = urlParams.get(":deviceid");
    	
    	Optional<Device> existDevice = this.service.getDevice(userId, deviceId);
            
        if (!existDevice.isPresent()) {
        	return Answer.no_data();
        }
            
    	return Answer.ok(existDevice.get());
	}
}
