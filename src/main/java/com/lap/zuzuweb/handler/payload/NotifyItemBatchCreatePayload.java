package com.lap.zuzuweb.handler.payload;

import java.util.LinkedList;
import java.util.List;

import com.lap.zuzuweb.model.NotifyItem;

import lombok.Data;

@Data
public class NotifyItemBatchCreatePayload implements Validable{

	private List<NotifyItem> items = new LinkedList<>();
	
	@Override
	public boolean isValid() {
		for (NotifyItem item: this.items){
			if(item.getItem_id() == null || item.getUser_id() == null){
				return false;
			}
		}
		
		return true;
	}

}
