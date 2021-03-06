package com.lap.zuzuweb.handler.criteria;

import java.util.Map;
import java.util.Optional;

import com.lap.zuzuweb.ZuzuLogger;
import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.EmptyPayload;
import com.lap.zuzuweb.model.Criteria;
import com.lap.zuzuweb.service.CriteriaService;

public class CriteriaQueryHandler extends AbstractRequestHandler<EmptyPayload> {

	private static final ZuzuLogger logger = ZuzuLogger.getLogger(CriteriaQueryHandler.class);
	
	private CriteriaService service = null;

	public CriteriaQueryHandler(CriteriaService service) {
		super(EmptyPayload.class);
		this.service = service;
	}

	@Override
	protected Answer processImpl(EmptyPayload value, Map<String, String> urlParams) {
		logger.entering("processImpl", "{value: %s, urlParams: %s}", value, urlParams);
		
		if (urlParams.containsKey(":userid")) {
			String userId = urlParams.get(":userid");

			Optional<Criteria> existCriteria = this.service.getSingleCriteria(userId);

			if (existCriteria.isPresent()) {
				return Answer.ok(existCriteria.get());
			}

			return Answer.no_data();
		} else {
			return Answer.ok(this.service.getAllCriteria());
		}
		
	}

}