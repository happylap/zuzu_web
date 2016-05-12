package com.lap.zuzuweb.handler.notifyItem;

import java.util.List;
import java.util.Map;

import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.NotifyItemBatchCreatePayload;
import com.lap.zuzuweb.model.NotifyItem;
import com.lap.zuzuweb.service.NotifyItemService;

public class NotifyItemBatchCreateHandler extends AbstractRequestHandler<NotifyItemBatchCreatePayload>{

	private NotifyItemService service = null;
	
	public NotifyItemBatchCreateHandler(NotifyItemService service) {
        super(NotifyItemBatchCreatePayload.class);
        this.service = service;
    }

	@Override
	protected Answer processImpl(NotifyItemBatchCreatePayload value, Map<String, String> urlParams) {
		try {
			List<NotifyItem> toAddItems = value.getItems();
			return Answer.ok(service.addItemsForFaultTolerance(toAddItems));
		} catch (Exception e) {
			return Answer.error(e.getMessage());
		}
	}
}
