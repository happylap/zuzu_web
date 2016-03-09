package com.lap.zuzuweb.handler.log;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lap.zuzuweb.handler.AbstractRequestArrayHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.PatchPayload;
import com.lap.zuzuweb.handler.payload.Validable;
import com.lap.zuzuweb.service.LogService;
import com.lap.zuzuweb.util.CommonUtils;

public class LogPatchHandler extends AbstractRequestArrayHandler {
	private LogService service = null;

	public LogPatchHandler(LogService service) {
		this.service = service;
	}

	@Override
	protected Answer processImpl(Validable[] values, Map<String, String> urlParams) {
		try {
			PatchPayload[] patches = (PatchPayload[]) values;
			for (PatchPayload patch : patches) {
				String op = patch.getOp();
				String path = patch.getPath();
				String value = patch.getValue();
				if (op.equalsIgnoreCase(PatchPayload.OP_ADD)) {
					this.handleAdd(urlParams, path, value);
				}
			}
			return Answer.ok();
		}catch(Exception e) {
			return Answer.error(e.getMessage());
		}
	}

	private void handleAdd(Map<String, String> urlParams, String path, String value) {
		
		if (!urlParams.containsKey(":userid") || !urlParams.containsKey(":deviceid")) {
			throw new IllegalArgumentException();
		}
		
		String userId = urlParams.get(":userid");
		//String deviceId = CommonUtils.decodeFromBase64String(urlParams.get(":deviceid"));
		String deviceId = urlParams.get(":deviceid");
		
		if (path.equalsIgnoreCase("/receiveNotifyTime")) {
			this.service.setReceiveNotifyTime(deviceId, userId, CommonUtils.getUTCNow());
		} else if (path.equalsIgnoreCase("/registerTime")) {
			this.service.setRegisterTime(deviceId, userId, CommonUtils.getUTCNow());
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	protected Validable[] parsePayloas(String reqBody) throws JsonParseException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		PatchPayload[] values = objectMapper.readValue(reqBody, PatchPayload[].class);
		return values;
	}

}
