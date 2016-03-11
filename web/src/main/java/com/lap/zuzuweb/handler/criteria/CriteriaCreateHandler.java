package com.lap.zuzuweb.handler.criteria;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.CriteriaCreatePayload;
import com.lap.zuzuweb.service.CriteriaService;

public class CriteriaCreateHandler extends AbstractRequestHandler<CriteriaCreatePayload> {

	private static final Logger logger = LoggerFactory.getLogger(CriteriaCreateHandler.class);
	
	private CriteriaService service = null;

	public CriteriaCreateHandler(CriteriaService service) {
		super(CriteriaCreatePayload.class);
		this.service = service;
	}

	@Override
	protected Answer processImpl(CriteriaCreatePayload value, Map<String, String> urlParams) {
		logger.info("processImpl enter:");
		logger.info(String.format("Parameter - value: %s", value));
		logger.info(String.format("Parameter - urlParams: %s", urlParams));
		String criteriaId = service.createCriteria(value.toCriteria());
		logger.info("processImpl exit.");
		return Answer.ok(criteriaId);
	}

}