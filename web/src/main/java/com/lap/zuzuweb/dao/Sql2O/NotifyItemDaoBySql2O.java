package com.lap.zuzuweb.dao.Sql2O;

import java.util.List;
import java.util.Optional;

import org.sql2o.Connection;

import com.lap.zuzuweb.dao.NotifyItemDao;
import com.lap.zuzuweb.model.NotifyItem;

public class NotifyItemDaoBySql2O extends AbstratcDaoBySql2O implements NotifyItemDao{

	static private String SQL_GET_ITEM_BY_USER = "SELECT item_id, user_id, criteria_id, is_read, notify_time, "
			+ " post_time, price, size, first_img_url, house_type, purpose_type, title, addr"
			+ " FROM \"Notify_item\" WHERE user_id=:user_id";

	static private String SQL_GET_SINGLE_ITEM = "SELECT item_id, user_id, criteria_id, is_read, notify_time, "
			+ " post_time, price, size, first_img_url, house_type, purpose_type, title, addr"
			+ " FROM \"Notify_item\" WHERE user_id=:user_id and item_id=:item_id";

	
	static private String SQL_CREATE_ITEM = "INSERT INTO \"Notify_item\"(item_id, user_id, criteria_id, is_read, "
			+ " notify_time, post_time, price, size, first_img_url, house_type, purpose_type, title, addr) "
			+ " VALUES (:item_id, :user_id, :criteria_id, :is_read, :notify_time, :post_time, "
			+ " :price, :size, :first_img_url, :house_type, :purpose_type, :title, :addr)";
	
	static private String SQL_UPDATE_ITEM = "UPDATE \"Notify_item\" SET criteria_id=:criteria_id, is_read=:is_read, notify_time=:notify_time,"
			+ " post_time=:post_time, price=:price, size=:size, first_img_url=:first_img_url, house_type=:house_type,"
			+ " purpose_type=:purpose_type, title=:title, addr=:addr"
			+ " WHERE item_id=:item_id AND user_id=:user_id";
	
	static private String SQL_GET_UNREAD_COUNT_BY_USER = "SELECT COUNT(*) FROM \"Notify_item\" WHERE is_read = false and user_id=:user_id";

	
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
	public Optional<NotifyItem> getItem(String userID, String item_id) {
        try (Connection conn = sql2o.open()) {
            List<NotifyItem> item = conn.createQuery(SQL_GET_SINGLE_ITEM)
                    .addParameter("user_id", userID)
                    .addParameter("item_id", item_id)
                    .executeAndFetch(NotifyItem.class);
            if (item.size() == 0) {
                return Optional.empty();
            } else if (item.size() == 1) {
                return Optional.of(item.get(0));
            } else {
                throw new RuntimeException();
            }
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
                .addParameter("post_time", item.getPost_time())
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

	@Override
	public String updateItem(NotifyItem item) {
        try (Connection conn = sql2o.beginTransaction()) {
        	
            conn.createQuery(SQL_UPDATE_ITEM)
    				.addParameter("item_id", item.getItem_id())
    				.addParameter("user_id", item.getUser_id())
            		.addParameter("criteria_id", item.getCriteria_id())
            		.addParameter("is_read", item.is_read())
                    .addParameter("notify_time", item.getNotify_time())
                    .addParameter("post_time", item.getPost_time())
                    .addParameter("price", item.getPrice())
                    .addParameter("size", item.getSize())
                    .addParameter("first_img_url", item.getFirst_img_url())
                    .addParameter("house_type", item.getHouse_type())
                    .addParameter("purpose_type", item.getPurpose_type())
                    .addParameter("title", item.getTitle())
                    .addParameter("addr", item.getAddr())
                    .executeUpdate();
            conn.commit();
            return item.getItem_id();
        }
	}

	@Override
	public long getUnreadCount(String userID) {
		try (Connection conn = sql2o.open()) {
			Long count = 0L;
			
            count = conn.createQuery(SQL_GET_UNREAD_COUNT_BY_USER)
                    .addParameter("user_id", userID)
                    .executeScalar(Long.class);
               
    		return count;
        }
	}
}
