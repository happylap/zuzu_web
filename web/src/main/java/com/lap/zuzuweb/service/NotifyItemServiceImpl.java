package com.lap.zuzuweb.service;

import java.util.Date;
import java.util.List;

import com.lap.zuzuweb.dao.NotifyItemDao;
import com.lap.zuzuweb.model.NotifyItem;

public class NotifyItemServiceImpl implements NotifyItemService{

	private NotifyItemDao dao = null;
	
	public NotifyItemServiceImpl(NotifyItemDao dao)
	{
		this.dao = dao;
	}
	@Override
	public List<NotifyItem> getItems(String userID) {
		return this.dao.getItems(userID);
	}
	
	@Override
	public boolean addItems(List<NotifyItem> items){
		if (items.isEmpty()){
			return true;
		}
		
		for (NotifyItem item : items){
			item.setNotify_time(new Date());
			item.set_read(false);
		}
		
		return this.dao.addItems(items);
	}

}
