package com.lap.zuzuweb.handler.criteria;

import java.util.Map;

import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.EmptyPayload;
import com.lap.zuzuweb.service.CriteriaService;


public class CriteriaRemoveHandler extends AbstractRequestHandler<EmptyPayload>{

	private CriteriaService service = null;
	
	public CriteriaRemoveHandler(CriteriaService service) {
        super(EmptyPayload.class);
        this.service = service;
    }
	@Override
	protected Answer processImpl(EmptyPayload value, Map<String, String> urlParams) {
    	if (!urlParams.containsKey(":criteriaid") && !urlParams.containsKey(":userid")) {
            throw new IllegalArgumentException();
        }
    	
    	if (urlParams.containsKey(":criteriaid")){
        	String deviceId = urlParams.get(":criteriaid");
            service.deleteCriteria(deviceId);
            return new Answer(200, deviceId);   		
    	}
    	else{
        	String userid = urlParams.get(":userid");
            service.deleteCriteriaByUser(userid);
            return new Answer(200, Answer.SUCCESS);       		
    	}
    	

	}

}