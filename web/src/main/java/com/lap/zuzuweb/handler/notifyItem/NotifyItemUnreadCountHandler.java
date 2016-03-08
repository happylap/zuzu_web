package com.lap.zuzuweb.handler.notifyItem;

import java.util.Map;

import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.EmptyPayload;
import com.lap.zuzuweb.service.NotifyItemService;
import com.lap.zuzuweb.util.CommonUtils;

public class NotifyItemUnreadCountHandler extends AbstractRequestHandler<EmptyPayload>{

	private NotifyItemService service = null;
	
	public NotifyItemUnreadCountHandler(NotifyItemService service) {
        super(EmptyPayload.class);
        this.service = service;
    }
	
	@Override
	protected Answer processImpl(EmptyPayload value, Map<String, String> urlParams) {
		
		if (!urlParams.containsKey(":provider") || !urlParams.containsKey(":userid")) {
			return Answer.bad_request();
		}
		
		String userId = CommonUtils.combineUserID(urlParams.get(":provider"), urlParams.get(":userid"));
		
		long unreadCount = this.service.getUnreadCount(userId);
		
        return Answer.ok(unreadCount);    	 
	}

}