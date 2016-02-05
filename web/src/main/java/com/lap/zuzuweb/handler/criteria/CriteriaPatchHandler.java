package com.lap.zuzuweb.handler.criteria;

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
import com.lap.zuzuweb.service.CriteriaService;

public class CriteriaPatchHandler extends AbstractRequestArrayHandler{
	private CriteriaService service = null;
	
	public CriteriaPatchHandler(CriteriaService service) {
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
    	if (!urlParams.containsKey(":criteriaid") || !urlParams.containsKey(":userid")) {
            throw new IllegalArgumentException();
        }
    	
    	String criteriaId = urlParams.get(":criteriaid");
    	String userId = urlParams.get(":userid");
    	
		if (path.equalsIgnoreCase("/enabled"))
		{
			boolean enabled = Boolean.valueOf(value).booleanValue();
			this.service.setEnable(criteriaId, userId, enabled);
		} else if (path.equalsIgnoreCase("/lastNotifyTime")){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			try
			{
				Date lastNotifyTime = sdf.parse(value);
				this.service.setLastNotifyTime(criteriaId, userId, lastNotifyTime);
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
