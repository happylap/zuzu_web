package com.lap.zuzuweb.handler.device;

import java.util.Map;

import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.EmptyPayload;
import com.lap.zuzuweb.service.DeviceService;

public class DeviceQueryHandler extends AbstractRequestHandler<EmptyPayload>{

	private DeviceService service = null;
	
	public DeviceQueryHandler(DeviceService service) {
        super(EmptyPayload.class);
        this.service = service;
    }
	
	@Override
	protected Answer processImpl(EmptyPayload value, Map<String, String> urlParams) {
    	if (!urlParams.containsKey(":userid")) {
    		String json = dataToJson(this.service.getAllDevices());
    		return Answer.ok(json); 
        }

    	String userID = urlParams.get(":userid");
        String json = dataToJson(this.service.getDevices(userID));
        return Answer.ok(json); 
	}

}
