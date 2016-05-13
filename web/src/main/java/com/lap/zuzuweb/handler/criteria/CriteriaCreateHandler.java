package com.lap.zuzuweb.handler.criteria;

import java.util.Map;

import com.lap.zuzuweb.ZuzuLogger;
import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.CriteriaCreatePayload;
import com.lap.zuzuweb.service.CriteriaService;

public class CriteriaCreateHandler extends AbstractRequestHandler<CriteriaCreatePayload> {

	private static final ZuzuLogger logger = ZuzuLogger.getLogger(CriteriaCreateHandler.class);
	
	private CriteriaService service = null;

	public CriteriaCreateHandler(CriteriaService service) {
		super(CriteriaCreatePayload.class);
		this.service = service;
	}

	@Override
	protected Answer processImpl(CriteriaCreatePayload value, Map<String, String> urlParams) {
		logger.entering("processImpl", "{value: %s, urlParams: %s}", value, urlParams);
		
		String criteriaId = service.createCriteria(value.toCriteria());
		return Answer.ok(criteriaId);
	}

}