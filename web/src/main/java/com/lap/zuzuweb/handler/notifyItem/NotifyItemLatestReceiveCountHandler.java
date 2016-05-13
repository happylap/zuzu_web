package com.lap.zuzuweb.handler.notifyItem;

import java.util.Map;

import com.lap.zuzuweb.ZuzuLogger;
import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.EmptyPayload;
import com.lap.zuzuweb.service.NotifyItemService;

public class NotifyItemLatestReceiveCountHandler extends AbstractRequestHandler<EmptyPayload> {

	private static final ZuzuLogger logger = ZuzuLogger.getLogger(NotifyItemLatestReceiveCountHandler.class);

	private NotifyItemService service = null;

	public NotifyItemLatestReceiveCountHandler(NotifyItemService service) {
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

		return Answer.ok(this.service.getLatestReceiveCount(userId));
	}

}