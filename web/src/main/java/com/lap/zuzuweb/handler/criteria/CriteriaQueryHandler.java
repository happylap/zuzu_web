package com.lap.zuzuweb.handler.criteria;

import java.util.Map;
import java.util.Optional;

import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.EmptyPayload;
import com.lap.zuzuweb.model.Criteria;
import com.lap.zuzuweb.service.CriteriaService;

public class CriteriaQueryHandler extends AbstractRequestHandler<EmptyPayload>{

	private CriteriaService service = null;
	
	public CriteriaQueryHandler(CriteriaService service) {
        super(EmptyPayload.class);
        this.service = service;
    }
	
	@Override
	protected Answer processImpl(EmptyPayload value, Map<String, String> urlParams) {
    	if(urlParams.containsKey(":userid")){
        	String userID = urlParams.get(":userid");
            
            Optional<Criteria> existCriteria = this.service.getSingleCriteria(userID);
            
            if (!existCriteria.isPresent()) {
            	return Answer.no_data();
            }

    		return Answer.ok(existCriteria.get());
            
    	} else {
    		return Answer.bad_request();
    	}
	}

}