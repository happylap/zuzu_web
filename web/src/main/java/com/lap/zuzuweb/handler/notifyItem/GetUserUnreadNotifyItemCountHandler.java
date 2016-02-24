package com.lap.zuzuweb.handler.notifyItem;

import java.util.Map;

import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.EmptyPayload;
import com.lap.zuzuweb.service.NotifyItemService;
import com.lap.zuzuweb.util.CommonUtils;

public class GetUserUnreadNotifyItemCountHandler extends AbstractRequestHandler<EmptyPayload>{

	private NotifyItemService service = null;
	
	public GetUserUnreadNotifyItemCountHandler(NotifyItemService service) {
        super(EmptyPayload.class);
        this.service = service;
    }
	
	@Override
	protected Answer processImpl(EmptyPayload value, Map<String, String> urlParams) {
    	if (!urlParams.containsKey(":userid")) {
            throw new IllegalArgumentException();
        }

    		
		String userID = urlParams.get(":userid");
        String json = CommonUtils.toJson(this.service.getUnreadCount(userID));
        return Answer.ok(json);

    	 
	}

}