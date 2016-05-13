package com.lap.zuzuweb.handler.device;

import java.util.Map;

import com.lap.zuzuweb.ZuzuLogger;
import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.EmptyPayload;
import com.lap.zuzuweb.service.DeviceService;

public class DeviceQueryHandler extends AbstractRequestHandler<EmptyPayload> {

	private static final ZuzuLogger logger = ZuzuLogger.getLogger(DeviceQueryHandler.class);
	
	private DeviceService service = null;

	public DeviceQueryHandler(DeviceService service) {
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

		return Answer.ok(this.service.getDevices(userId));
	}
}
