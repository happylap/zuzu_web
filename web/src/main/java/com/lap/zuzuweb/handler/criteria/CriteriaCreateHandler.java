package com.lap.zuzuweb.handler.criteria;

import java.util.Map;

import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.CriteriaCreatePayload;
import com.lap.zuzuweb.service.CriteriaService;
import com.lap.zuzuweb.util.CommonUtils;

public class CriteriaCreateHandler extends AbstractRequestHandler<CriteriaCreatePayload> {

	private CriteriaService service = null;

	public CriteriaCreateHandler(CriteriaService service) {
		super(CriteriaCreatePayload.class);
		this.service = service;
	}

	@Override
	protected Answer processImpl(CriteriaCreatePayload value, Map<String, String> urlParams) {
		
		if (!urlParams.containsKey(":provider") || !urlParams.containsKey(":userid")) {
			return Answer.bad_request();
		}
		
		String userId = CommonUtils.combineUserID(urlParams.get(":provider"), urlParams.get(":userid"));
		
		String criteriaId = service.createCriteria(value.toCriteria(userId));
		return Answer.ok(criteriaId);
	}

}