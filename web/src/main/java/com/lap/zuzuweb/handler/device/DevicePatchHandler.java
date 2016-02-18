package com.lap.zuzuweb.handler.device;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lap.zuzuweb.handler.AbstractRequestArrayHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.PatchPayload;
import com.lap.zuzuweb.handler.payload.Validable;
import com.lap.zuzuweb.service.DeviceService;
import com.lap.zuzuweb.util.CommonUtils;

public class DevicePatchHandler extends AbstractRequestArrayHandler{
	private DeviceService service = null;
	
	public DevicePatchHandler(DeviceService service) {
        this.service = service;
    }

	@Override
	protected Answer processImpl(Validable[] values, Map<String, String> urlParams) {
		PatchPayload[] patches = (PatchPayload[])values;
		for (PatchPayload patch: patches){
			String op = patch.getOp();
			String path = patch.getPath();
			String value = patch.getValue();
			if (op.equalsIgnoreCase(PatchPayload.OP_REPLACE)){
				this.handleReplace(urlParams, path, value);
			}
		}
		return new Answer(200, Answer.SUCCESS);
	}

	private void handleReplace(Map<String, String> urlParams, String path, String value) {
    	if (!urlParams.containsKey(":deviceid") || !urlParams.containsKey(":userid")) {
            throw new IllegalArgumentException();
        }
    	
    	String deviceId = urlParams.get(":deviceid");
    	String userId = urlParams.get(":userid");
    	
    	if (path.equalsIgnoreCase("/receiveNotifyTime")){
			try
			{
				Date receiveNotifyTime = CommonUtils.getUTCDateFromString(value);
				this.service.setReceiveNotifyTime(deviceId, userId, receiveNotifyTime);
			}
			catch(Exception e)
			{
				throw new IllegalArgumentException();
			}
		}
	}

	@Override
	protected Validable[] parsePayloas(String reqBody) throws JsonParseException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        PatchPayload[] values = objectMapper.readValue(reqBody, PatchPayload[].class);
        return values;
	}


}
