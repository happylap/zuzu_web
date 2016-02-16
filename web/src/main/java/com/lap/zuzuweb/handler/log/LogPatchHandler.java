package com.lap.zuzuweb.handler.log;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lap.zuzuweb.handler.AbstractRequestArrayHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.PatchPayload;
import com.lap.zuzuweb.handler.payload.Validable;
import com.lap.zuzuweb.service.LogService;

import spark.utils.StringUtils;

public class LogPatchHandler extends AbstractRequestArrayHandler {
	private LogService service = null;

	public LogPatchHandler(LogService service) {
		this.service = service;
	}

	@Override
	protected Answer processImpl(Validable[] values, Map<String, String> urlParams) {
		PatchPayload[] patches = (PatchPayload[]) values;
		for (PatchPayload patch : patches) {
			String op = patch.getOp();
			String path = patch.getPath();
			String value = patch.getValue();
			if (op.equalsIgnoreCase(PatchPayload.OP_ADD)) {
				this.handleAdd(urlParams, path, value);
			}
		}
		return new Answer(200, Answer.SUCCESS);
	}

	private void handleAdd(Map<String, String> urlParams, String path, String value) {
		if (!urlParams.containsKey(":deviceid") || !urlParams.containsKey(":userid")) {
			throw new IllegalArgumentException();
		}

		String deviceId = urlParams.get(":deviceid");
		String userId = urlParams.get(":userid");

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

		if (path.equalsIgnoreCase("/receiveNotifyTime")) {
			try {
				Date receiveNotifyTime = null;
				if (StringUtils.isNotEmpty(value)) {
					receiveNotifyTime = sdf.parse(value);
				}
				this.service.setReceiveNotifyTime(deviceId, userId, receiveNotifyTime);
			} catch (Exception e) {
				throw new IllegalArgumentException();
			}
		} else if (path.equalsIgnoreCase("/registerTime")) {
			try {
				Date registerTime = null;
				if (StringUtils.isNotEmpty(value)) {
					registerTime = sdf.parse(value);
				}
				this.service.setRegisterTime(deviceId, userId, registerTime);
			} catch (Exception e) {
				throw new IllegalArgumentException();
			}
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
