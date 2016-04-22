package com.lap.zuzuweb.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lap.zuzuweb.dao.LogDao;
import com.lap.zuzuweb.dao.NotifyItemDao;
import com.lap.zuzuweb.model.NotifyItem;

public class NotifyItemServiceImpl implements NotifyItemService {

	private static final Logger logger = LoggerFactory.getLogger(NotifyItemService.class);
	
	private NotifyItemDao dao = null;
	private LogDao logDao = null;

	public NotifyItemServiceImpl(NotifyItemDao dao, LogDao logDao) {
		this.dao = dao;
		this.logDao = logDao;
	}

	@Override
	public List<NotifyItem> getItems(String userID) {
		logger.info("NotifyItemService.getItems: " + userID);
		return this.dao.getItems(userID);
	}

	@Override
	public List<NotifyItem> getItemsAfterPostTime(String userID, Date postTime) {
		logger.info("NotifyItemService.getItemsAfterPostTime: " + userID + ", " + postTime);
		return this.dao.getItemsAfterPostTime(userID, postTime);
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

	@Override
	public long getLatestReceiveCount(String userID) {
		Optional<Date> latestNotifyTime = logDao.getLatestNotifyTime(userID);
		if (latestNotifyTime.isPresent()) {
			return this.dao.getCountOfItemsAfterNotifyTime(userID, latestNotifyTime.get());
		} 
		return 0;
	}
}
