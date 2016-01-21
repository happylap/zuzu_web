package com.lap.zuzuweb.service;

import java.util.List;

import com.lap.zuzuweb.model.NotifyItem;

public interface NotifyItemService
{
	public List<NotifyItem> getItems(String userID);
	
	public boolean addItems(List<NotifyItem> items);
}
