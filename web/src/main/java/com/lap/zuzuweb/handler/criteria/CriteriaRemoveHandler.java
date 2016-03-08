package com.lap.zuzuweb.handler.criteria;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.EmptyPayload;
import com.lap.zuzuweb.service.CriteriaService;
import com.lap.zuzuweb.util.CommonUtils;

public class CriteriaRemoveHandler extends AbstractRequestHandler<EmptyPayload> {

	private CriteriaService service = null;

	public CriteriaRemoveHandler(CriteriaService service) {
		super(EmptyPayload.class);
		this.service = service;
	}

	@Override
	protected Answer processImpl(EmptyPayload value, Map<String, String> urlParams) {

		if (!urlParams.containsKey(":provider") || !urlParams.containsKey(":userid")) {
			return Answer.bad_request();
		}

		String userId = CommonUtils.combineUserID(urlParams.get(":provider"), urlParams.get(":userid"));
		String criteriaId = urlParams.get(":criteriaid");

		if (StringUtils.isNotEmpty(userId) && StringUtils.isNotEmpty(criteriaId)) {
			service.deleteCriteria(criteriaId, userId);
		} else if (StringUtils.isNotEmpty(userId)) {
			service.deleteCriteriaByUser(userId);
		}

		return Answer.ok();
	}

}