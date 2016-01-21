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
        String criteriaId = service.updateCriteria(value);
        return new Answer(201, criteriaId);
	}

}