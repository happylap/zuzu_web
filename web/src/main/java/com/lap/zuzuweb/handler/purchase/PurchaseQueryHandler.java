package com.lap.zuzuweb.handler.purchase;

import java.util.List;
import java.util.Map;

import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.EmptyPayload;
import com.lap.zuzuweb.model.Purchase;
import com.lap.zuzuweb.service.PurchaseService;

import spark.utils.CollectionUtils;

public class PurchaseQueryHandler extends AbstractRequestHandler<EmptyPayload> {

	private PurchaseService service = null;

	public PurchaseQueryHandler(PurchaseService service) {
		super(EmptyPayload.class);
		this.service = service;
	}

	@Override
	protected Answer processImpl(EmptyPayload value, Map<String, String> urlParams) {
			
		if (urlParams.containsKey(":userid")) {
			String userID = urlParams.get(":userid");
			List<Purchase> purchases = this.service.getPurchase(userID);
			return Answer.ok(purchases);
			
		} else {
			return Answer.bad_request();
		}

	}

}