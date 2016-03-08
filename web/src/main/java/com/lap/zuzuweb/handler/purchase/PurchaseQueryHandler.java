package com.lap.zuzuweb.handler.purchase;

import java.util.List;
import java.util.Map;

import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.EmptyPayload;
import com.lap.zuzuweb.model.Purchase;
import com.lap.zuzuweb.service.PurchaseService;
import com.lap.zuzuweb.util.CommonUtils;

public class PurchaseQueryHandler extends AbstractRequestHandler<EmptyPayload> {

	private PurchaseService service = null;

	public PurchaseQueryHandler(PurchaseService service) {
		super(EmptyPayload.class);
		this.service = service;
	}

	@Override
	protected Answer processImpl(EmptyPayload value, Map<String, String> urlParams) {
		
		if (!urlParams.containsKey(":provider") || !urlParams.containsKey(":userid")) {
			return Answer.bad_request();
		}
		
		String userId = CommonUtils.combineUserID(urlParams.get(":provider"), urlParams.get(":userid"));
		
		List<Purchase> purchases = this.service.getPurchase(userId);
		
		return Answer.ok(purchases);
	}
}