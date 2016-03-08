package com.lap.zuzuweb.handler.notifyItem;

import java.util.List;
import java.util.Map;

import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.EmptyPayload;
import com.lap.zuzuweb.model.NotifyItem;
import com.lap.zuzuweb.service.NotifyItemService;
import com.lap.zuzuweb.util.CommonUtils;

public class NotifyItemQueryHandler extends AbstractRequestHandler<EmptyPayload>{

	private NotifyItemService service = null;
	
	public NotifyItemQueryHandler(NotifyItemService service) {
        super(EmptyPayload.class);
        this.service = service;
    }
	
	@Override
	protected Answer processImpl(EmptyPayload value, Map<String, String> urlParams) {
		
		if (!urlParams.containsKey(":provider") || !urlParams.containsKey(":userid")) {
			return Answer.bad_request();
		}
		
		String userId = CommonUtils.combineUserID(urlParams.get(":provider"), urlParams.get(":userid"));
		
		List<NotifyItem> notifyItems = this.service.getItems(userId);
		
        return Answer.ok(notifyItems); 
	}

}