package com.lap.zuzuweb.handler.notifyItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.EmptyPayload;
import com.lap.zuzuweb.model.NotifyItem;
import com.lap.zuzuweb.service.NotifyItemService;

public class NotifyItemQueryHandler extends AbstractRequestHandler<EmptyPayload>{

	private static final Logger logger = LoggerFactory.getLogger(NotifyItemQueryHandler.class);
	
	private NotifyItemService service = null;
	
	public NotifyItemQueryHandler(NotifyItemService service) {
        super(EmptyPayload.class);
        this.service = service;
    }
	
	@Override
	protected Answer processImpl(EmptyPayload value, Map<String, String> urlParams) {
		logger.debug("NotifyItemQueryHandler.processImpl: urlParams: " + urlParams);
		
		List<NotifyItem> notifyItems = new ArrayList<NotifyItem>();
				
		if (urlParams.containsKey(":userid") && urlParams.containsKey(":posttime")) {
			String userId = urlParams.get(":userid");
			String secondsOfPostTime = urlParams.get(":posttime");
			
			long seconds = NumberUtils.toLong(secondsOfPostTime);
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(seconds * 1000);
			Date postTime = cal.getTime(); 
			
			notifyItems = this.service.getItemsAfterPostTime(userId, postTime);
		}
		
		else if (urlParams.containsKey(":userid")) {
			String userId = urlParams.get(":userid");
			notifyItems = this.service.getItems(userId);
		}
		
		else {
			return Answer.bad_request();
		}
		
        return Answer.ok(notifyItems); 
	}

}