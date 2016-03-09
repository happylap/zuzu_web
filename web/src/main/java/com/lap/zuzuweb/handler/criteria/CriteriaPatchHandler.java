package com.lap.zuzuweb.handler.criteria;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import org.postgresql.util.PGobject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lap.zuzuweb.handler.AbstractRequestArrayHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.PatchPayload;
import com.lap.zuzuweb.handler.payload.Validable;
import com.lap.zuzuweb.service.CriteriaService;
import com.lap.zuzuweb.util.CommonUtils;

public class CriteriaPatchHandler extends AbstractRequestArrayHandler {
	private CriteriaService service = null;

	public CriteriaPatchHandler(CriteriaService service) {
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
				if (op.equalsIgnoreCase(PatchPayload.OP_REPLACE)) {
					this.handleReplace(urlParams, path, value);
				}
			}
			return Answer.ok();
		} catch (Exception e) {
			return Answer.error(e.getMessage());
		}
	}

	private void handleReplace(Map<String, String> urlParams, String path, String value) {
		
		if (!urlParams.containsKey(":userid") || !urlParams.containsKey(":criteriaid")) {
			throw new IllegalArgumentException();
		}
		
		String userId = urlParams.get(":userid");
		String criteriaId = urlParams.get(":criteriaid");

		if (path.equalsIgnoreCase("/filters")) {
			PGobject filters = new PGobject();
			filters.setType("json");
			try {
				filters.setValue(value);
				this.service.setFilters(criteriaId, userId, filters);
			} catch (final Exception e) {
				throw new IllegalArgumentException();
			}
		} else if (path.equalsIgnoreCase("/enabled")) {
			boolean enabled = Boolean.valueOf(value).booleanValue();
			this.service.setEnable(criteriaId, userId, enabled);
		} else if (path.equalsIgnoreCase("/lastNotifyTime")) {
			try {
				Date lastNotifyTime = CommonUtils.getUTCDateFromString(value);
				this.service.setLastNotifyTime(criteriaId, userId, lastNotifyTime);
			} catch (Exception e) {
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
