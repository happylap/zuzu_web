package com.lap.zuzuweb.dao.Sql2O;

import java.util.List;

import org.sql2o.Connection;

import com.lap.zuzuweb.dao.NotifyItemDao;
import com.lap.zuzuweb.model.NotifyItem;

public class NotifyItemDaoBySql2O extends AbstratcDaoBySql2O implements NotifyItemDao{

	static private String SQL_GET_ITEM_BY_USER = "SELECT item_id, user_id, criteria_id, is_read, notify_time, "
			+ " price, size, first_img_url, house_type, purpose_type, title, addr"
			+ " FROM \"Notify_item\" WHERE user_id=:user_id";

	static private String SQL_CREATE_ITEM = "INSERT INTO \"Notify_item\"(item_id, user_id, criteria_id, is_read, "
			+ " notify_time, price, size, first_img_url, house_type, purpose_type, title, addr) "
			+ " VALUES (:item_id, :user_id, :criteria_id, :is_read, :notify_time, :price, :size, "
			+ " :first_img_url, :house_type, :purpose_type, :title, :addr)";
	
	@Override
	public List<NotifyItem> getItems(String userID) {
        try (Connection conn = sql2o.open()) {
            List<NotifyItem> items = conn.createQuery(SQL_GET_ITEM_BY_USER)
                    .addParameter("user_id", userID)
                    .executeAndFetch(NotifyItem.class);
            return items;
        }
	}

	@Override
	public boolean addItems(List<NotifyItem> items) {
        try (Connection conn = sql2o.beginTransaction()) {
        	
        	for (NotifyItem item: items){
                conn.createQuery(SQL_CREATE_ITEM)
        		.addParameter("item_id", item.getItem_id())
        		.addParameter("user_id", item.getUser_id())
        		.addParameter("criteria_id", item.getCriteria_id())
        		.addParameter("is_read", item.is_read())
                .addParameter("notify_time", item.getNotify_time())
                .addParameter("price", item.getPrice())
                .addParameter("size", item.getSize())
                .addParameter("first_img_url", item.getFirst_img_url())
                .addParameter("house_type", item.getHouse_type())
                .addParameter("purpose_type", item.getPurpose_type())
                .addParameter("title", item.getTitle())
                .addParameter("addr", item.getAddr())
                .executeUpdate();       		
        	}
        	
 
            conn.commit();
            return true;
        }
	}

}
