package com.lap.zuzuweb.handler.purchase;

import java.util.Map;
import java.util.Optional;

import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.EmptyPayload;
import com.lap.zuzuweb.model.Criteria;
import com.lap.zuzuweb.model.Purchase;
import com.lap.zuzuweb.service.PurchaseService;

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
			String json = dataToJson(this.service.getPurchase(userID));
			return Answer.ok(json);

		} else {
			throw new IllegalArgumentException();
		}

	}

}