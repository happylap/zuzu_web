package com.lap.zuzuweb.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.lap.zuzuweb.dao.NotifyItemDao;
import com.lap.zuzuweb.model.NotifyItem;

public class NotifyItemServiceImpl implements NotifyItemService {

	private NotifyItemDao dao = null;

	public NotifyItemServiceImpl(NotifyItemDao dao) {
		this.dao = dao;
	}

	@Override
	public List<NotifyItem> getItems(String userID) {
		return this.dao.getItems(userID);
	}

	@Override
	public boolean addItems(List<NotifyItem> items) {
		if (items.isEmpty()) {
			return true;
		}

		for (NotifyItem item : items) {
			item.setNotify_time(new Date());
			item.set_read(false);
		}

		return this.dao.addItems(items);
	}

	@Override
	public String updateItem(NotifyItem item) {
		return this.dao.updateItem(item);
	}

	@Override
	public void setRead(String itemid, String userID, boolean is_read) {
		Optional<NotifyItem> existItem = this.getItem(itemid, userID);
		if (existItem.isPresent()) {
			NotifyItem item = existItem.get();

			if (item.is_read() != is_read) {
				item.set_read(is_read);
				this.dao.updateItem(item);
			}
		}
	}

	@Override
	public Optional<NotifyItem> getItem(String itemid, String userID) {
		return this.dao.getItem(userID, itemid);
	}
	
	@Override
	public long getUnreadCount(String userID) {
		return this.dao.getUnreadCount(userID);
	}
}
