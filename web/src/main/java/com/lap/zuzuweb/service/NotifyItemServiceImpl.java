package com.lap.zuzuweb.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Sql2oException;

import com.lap.zuzuweb.dao.LogDao;
import com.lap.zuzuweb.dao.NotifyItemDao;
import com.lap.zuzuweb.dao.UserDao;
import com.lap.zuzuweb.handler.payload.NotifyItemErrorMessagePayload;
import com.lap.zuzuweb.model.NotifyItem;
import com.lap.zuzuweb.model.User;

public class NotifyItemServiceImpl implements NotifyItemService {

	private static final Logger logger = LoggerFactory.getLogger(NotifyItemService.class);
	
	private NotifyItemDao dao = null;
	private LogDao logDao = null;
	private UserDao userDao = null;

	public NotifyItemServiceImpl(NotifyItemDao dao, LogDao logDao, UserDao userDao) {
		this.dao = dao;
		this.logDao = logDao;
		this.userDao = userDao;
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

	
	class NotifyItemErrorMessage {
		private String item_id;
		private String user_id;
		private String message;
	}
	
	@Override
	public Map<String, Object> addItemsForFaultTolerance(List<NotifyItem> items) {
		
		List<NotifyItemErrorMessagePayload> failures = new ArrayList<NotifyItemErrorMessagePayload>();
		int success = 0;
		
		if (CollectionUtils.isNotEmpty(items)) {
			for (NotifyItem item : items) {
				try {
					item.setNotify_time(new Date());
					item.set_read(false);
					this.dao.addItem(item);
					success++;
				} catch (Sql2oException e) {
					logger.error(e.getMessage());
					
					NotifyItemErrorMessagePayload failure = new NotifyItemErrorMessagePayload();
					failure.setItem_id(item.getItem_id());
					failure.setUser_id(item.getUser_id());
					failure.setMessage(e.getMessage());
					failures.add(failure);
				}
			}
			
			try {
				String userID = items.get(0).getUser_id();
				int retainItemCount = 200;
				this.dao.purgeOldItems(userID, retainItemCount);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("success", success);
		result.put("failures", failures);
		return result;
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
		
		Optional<User> existUser = this.userDao.getUserById(userID);
		if (existUser.isPresent()) {
			User user = existUser.get();
			if (user.getRegister_time() != null) {
				return this.dao.getCountOfItemsAfterNotifyTime(user.getUser_id(), user.getRegister_time());
			}
		}
		
		return 0;
	}
}
