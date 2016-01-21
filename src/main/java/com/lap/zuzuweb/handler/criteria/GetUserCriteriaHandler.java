package com.lap.zuzuweb.handler.criteria;

import java.util.Map;

import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.EmptyPayload;
import com.lap.zuzuweb.service.CriteriaService;

public class GetUserCriteriaHandler extends AbstractRequestHandler<EmptyPayload>{

	private CriteriaService service = null;
	
	public GetUserCriteriaHandler(CriteriaService service) {
        super(EmptyPayload.class);
        this.service = service;
    }
	
	@Override
	protected Answer processImpl(EmptyPayload value, Map<String, String> urlParams) {
    	if (!urlParams.containsKey(":userid")) {
            throw new IllegalArgumentException();
        }

    	String userID = urlParams.get(":userid");
        String json = dataToJson(this.service.getCriteria(userID));
        return Answer.ok(json); 
	}

}