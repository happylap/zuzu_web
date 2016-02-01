package com.lap.zuzuweb.service;

import java.util.List;
import java.util.Optional;

import com.lap.zuzuweb.model.NotifyItem;

public interface NotifyItemService {
	public List<NotifyItem> getItems(String userID);

	public boolean addItems(List<NotifyItem> items);

	public String updateItem(NotifyItem item);

	public void setRead(String itemid, String userID, boolean read);

	public Optional<NotifyItem> getItem(String itemid, String userID);
}
