package com.lap.zuzuweb.handler.purchase;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;

import com.lap.zuzuweb.ZuzuLogger;
import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.EmptyPayload;
import com.lap.zuzuweb.model.Purchase;
import com.lap.zuzuweb.service.PurchaseService;

public class PurchaseQueryHandler extends AbstractRequestHandler<EmptyPayload> {

	private static final ZuzuLogger logger = ZuzuLogger.getLogger(PurchaseQueryHandler.class);
	
	private PurchaseService service = null;

	public PurchaseQueryHandler(PurchaseService service) {
		super(EmptyPayload.class);
		this.service = service;
	}

	@Override
	protected Answer processImpl(EmptyPayload value, Map<String, String> urlParams) {
		logger.entering("processImpl", "{value: %s, urlParams: %s}", value, urlParams);
		
		if (!urlParams.containsKey(":userid")) {
			return Answer.bad_request();
		}
		
		String userId = urlParams.get(":userid");
		
		List<Purchase> purchases = this.service.getPurchase(userId);
		
		Answer answer = Answer.ok(purchases);
		answer.setMessage("Total count: " + CollectionUtils.size(purchases));
		
		return answer;
	}
}