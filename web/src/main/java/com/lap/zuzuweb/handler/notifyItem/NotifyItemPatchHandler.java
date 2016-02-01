package com.lap.zuzuweb.handler.notifyItem;

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
import com.lap.zuzuweb.service.NotifyItemService;

public class NotifyItemPatchHandler extends AbstractRequestArrayHandler{
	private NotifyItemService service = null;
	
	public NotifyItemPatchHandler(NotifyItemService service) {
        this.service = service;
    }

	@Override
	protected Answer processImpl(Validable[] values, Map<String, String> urlParams) {
		PatchPayload[] patches = (PatchPayload[])values;
		for (PatchPayload patch: patches) {
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
    	if (!urlParams.containsKey(":itemid") || !urlParams.containsKey(":userid")) {
            throw new IllegalArgumentException();
        }

    	String itemid = urlParams.get(":itemid");
    	String userid = urlParams.get(":userid");
    	
		if (path.equalsIgnoreCase("/_read"))
		{
			boolean _read = Boolean.valueOf(value).booleanValue();
			this.service.setRead(itemid, userid, _read);
		}
	}

	@Override
	protected Validable[] parsePayloas(String reqBody) throws JsonParseException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        PatchPayload[] values = objectMapper.readValue(reqBody, PatchPayload[].class);
        return values;
	}


}
