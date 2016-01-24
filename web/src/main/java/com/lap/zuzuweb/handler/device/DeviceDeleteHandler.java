package com.lap.zuzuweb.handler.device;

import java.util.Map;

import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.EmptyPayload;
import com.lap.zuzuweb.service.DeviceService;


public class DeviceDeleteHandler extends AbstractRequestHandler<EmptyPayload>{

	private DeviceService service = null;
	
	public DeviceDeleteHandler(DeviceService service) {
        super(EmptyPayload.class);
        this.service = service;
    }
	@Override
	protected Answer processImpl(EmptyPayload value, Map<String, String> urlParams) {
    	if (!urlParams.containsKey(":deviceid") && !urlParams.containsKey(":userid")) {
            throw new IllegalArgumentException();
        }
    	
    	if (urlParams.containsKey(":deviceid")){
        	String deviceId = urlParams.get(":deviceid");
            service.deleteDevice(deviceId);
            return new Answer(200, deviceId);   		
    	}
    	else{
        	String userid = urlParams.get(":userid");
            service.deleteDevicesByUser(userid);
            return new Answer(200, Answer.SUCCESS);       		
    	}
    	

	}

}