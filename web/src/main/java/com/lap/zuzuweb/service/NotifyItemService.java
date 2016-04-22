package com.lap.zuzuweb.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.lap.zuzuweb.model.NotifyItem;

public interface NotifyItemService {
	public List<NotifyItem> getItems(String userID);
	
	public List<NotifyItem> getItemsAfterPostTime(String userID, Date postTime);

	public boolean addItems(List<NotifyItem> items);

	public String updateItem(NotifyItem item);

	public void setRead(String itemid, String userID, boolean read);

	public Optional<NotifyItem> getItem(String itemid, String userID);
	
	public long getUnreadCount(String userID);
	
	public long getLatestReceiveCount(String userID);
}
