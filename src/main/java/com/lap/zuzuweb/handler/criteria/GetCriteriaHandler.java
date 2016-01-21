package com.lap.zuzuweb.handler.criteria;

import java.util.Map;

import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.EmptyPayload;
import com.lap.zuzuweb.service.CriteriaService;

public class GetCriteriaHandler extends AbstractRequestHandler<EmptyPayload>{

	private CriteriaService service = null;
	
	public GetCriteriaHandler(CriteriaService service) {
        super(EmptyPayload.class);
        this.service = service;
    }
	
	@Override
	protected Answer processImpl(EmptyPayload value, Map<String, String> urlParams) {
        String json = dataToJson(this.service.getAllCriteria());
        return Answer.ok(json); 
	}

}