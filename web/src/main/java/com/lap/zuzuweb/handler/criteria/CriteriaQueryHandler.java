package com.lap.zuzuweb.handler.criteria;

import java.util.Map;

import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.EmptyPayload;
import com.lap.zuzuweb.service.CriteriaService;

public class CriteriaQueryHandler extends AbstractRequestHandler<EmptyPayload>{

	private CriteriaService service = null;
	
	public CriteriaQueryHandler(CriteriaService service) {
        super(EmptyPayload.class);
        this.service = service;
    }
	
	@Override
	protected Answer processImpl(EmptyPayload value, Map<String, String> urlParams) {
    	if (!urlParams.containsKey(":criteriaid") && !urlParams.containsKey(":userid")) {
            String json = dataToJson(this.service.getAllCriteria());
            return Answer.ok(json);
        }
    	else if (urlParams.containsKey(":criteriaid") && urlParams.containsKey(":userid")){
    		String criteriaId = urlParams.get(":criteriaid");
    		String userID = urlParams.get(":userid");
    		String json = dataToJson(this.service.getCriteria(criteriaId, userID));
    		return Answer.ok(json); 
    	}
    	else if(urlParams.containsKey(":userid")){
        	String userID = urlParams.get(":userid");
            String json = dataToJson(this.service.getCriteria(userID));
            return Answer.ok(json);    		
    	}else{
    		throw new IllegalArgumentException();
    	}

	}

}