package com.lap.zuzuweb.dao;

import java.util.List;
import java.util.Optional;

import com.lap.zuzuweb.model.NotifyItem;

public interface NotifyItemDao 
{
	public List<NotifyItem> getItems(String userID);
	
	public boolean addItems(List<NotifyItem> items);
	
	public String updateItem(NotifyItem item);
	
	public Optional<NotifyItem> getItem(String userID, String itemid);
}
