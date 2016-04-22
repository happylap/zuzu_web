package com.lap.zuzuweb.dao;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.lap.zuzuweb.model.NotifyItem;

public interface NotifyItemDao 
{
	public List<NotifyItem> getItems(String userID);
	
	public List<NotifyItem> getItemsAfterPostTime(String userID, Date postTime);
	
	public int getCountOfItemsAfterNotifyTime(String userID, Date notifyTime);
	
	public boolean addItems(List<NotifyItem> items);
	
	public String updateItem(NotifyItem item);
	
	public Optional<NotifyItem> getItem(String userID, String itemid);
	
	public long getUnreadCount(String userID);
}
