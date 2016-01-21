package com.lap.zuzuweb.handler.notifyItem;

import java.util.Map;

import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.EmptyPayload;
import com.lap.zuzuweb.service.NotifyItemService;

public class GetUserNotifyItemHandler extends AbstractRequestHandler<EmptyPayload>{

	private NotifyItemService service = null;
	
	public GetUserNotifyItemHandler(NotifyItemService service) {
        super(EmptyPayload.class);
        this.service = service;
    }
	
	@Override
	protected Answer processImpl(EmptyPayload value, Map<String, String> urlParams) {
    	if (!urlParams.containsKey(":userid")) {
            throw new IllegalArgumentException();
        }

    	String userID = urlParams.get(":userid");
        String json = dataToJson(this.service.getItems(userID));
        return Answer.ok(json); 
	}

}