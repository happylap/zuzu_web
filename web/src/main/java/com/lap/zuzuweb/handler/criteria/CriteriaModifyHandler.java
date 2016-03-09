package com.lap.zuzuweb.handler.criteria;

import java.util.Map;

import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.CriteriaUpdatePayload;
import com.lap.zuzuweb.service.CriteriaService;

public class CriteriaModifyHandler extends AbstractRequestHandler<CriteriaUpdatePayload>{

	private CriteriaService service = null;
	
	public CriteriaModifyHandler(CriteriaService service) {
        super(CriteriaUpdatePayload.class);
        this.service = service;
    }
	@Override
	protected Answer processImpl(CriteriaUpdatePayload value, Map<String, String> urlParams) {
		
		if (!urlParams.containsKey(":userid") || !urlParams.containsKey(":criteriaid")) {
			return Answer.bad_request();
		}
		
		String userId = urlParams.get(":userid");
    	String criteriaId = urlParams.get(":criteriaid");
    	
        String updateId = service.updateCriteria(value.toCriteria(criteriaId, userId));
        return Answer.ok(updateId);
	}

}