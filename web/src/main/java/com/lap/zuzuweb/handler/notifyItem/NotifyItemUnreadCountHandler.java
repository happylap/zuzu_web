package com.lap.zuzuweb.handler.notifyItem;

import java.util.Map;

import com.lap.zuzuweb.ZuzuLogger;
import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.EmptyPayload;
import com.lap.zuzuweb.service.NotifyItemService;

public class NotifyItemUnreadCountHandler extends AbstractRequestHandler<EmptyPayload> {

	private static final ZuzuLogger logger = ZuzuLogger.getLogger(NotifyItemUnreadCountHandler.class);

	private NotifyItemService service = null;

	public NotifyItemUnreadCountHandler(NotifyItemService service) {
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

		long unreadCount = this.service.getUnreadCount(userId);

		return Answer.ok(unreadCount);
	}

}