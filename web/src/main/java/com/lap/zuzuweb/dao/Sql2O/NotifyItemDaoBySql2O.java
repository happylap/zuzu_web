package com.lap.zuzuweb.dao.Sql2O;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.sql2o.Connection;

import com.lap.zuzuweb.ZuzuLogger;
import com.lap.zuzuweb.dao.NotifyItemDao;
import com.lap.zuzuweb.model.NotifyItem;

public class NotifyItemDaoBySql2O extends AbstratcDaoBySql2O implements NotifyItemDao{

	private static final ZuzuLogger logger = ZuzuLogger.getLogger(NotifyItemDaoBySql2O.class);
	
	static private String SQL_GET_ITEM_BY_USER = "SELECT item_id, user_id, criteria_id, is_read, notify_time, "
			+ " post_time, price, size, first_img_url, house_type, purpose_type, title, addr"
			+ " FROM \"Notify_item\" WHERE user_id=:user_id";
	
	static private String SQL_GET_ITEM_BY_USER_ORDER_BY_POST_TIME = "SELECT item_id, user_id, criteria_id, is_read, notify_time, "
			+ " post_time, price, size, first_img_url, house_type, purpose_type, title, addr"
			+ " FROM \"Notify_item\" WHERE user_id=:user_id order by post_time";
	
	static private String SQL_COUNT_OF_ITEMS_BY_USER_AFTER_NOTIFY_TIME = "SELECT count(item_id) FROM \"Notify_item\"" 
			+ " WHERE user_id=:user_id AND notify_time>=:notify_time";
	
	static private String SQL_GET_ITEM_BY_USER_AFTER_POSTTIME = "SELECT item_id, user_id, criteria_id, is_read, notify_time, "
			+ " post_time, price, size, first_img_url, house_type, purpose_type, title, addr"
			+ " FROM \"Notify_item\" WHERE user_id=:user_id AND post_time>:post_time";

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

	static private String SQL_REMOVE_ITEM = "DELETE FROM \"Notify_item\" Where item_id=:item_id AND user_id=:user_id";
	
	@Override
	public List<NotifyItem> getItems(String userID) {
		logger.entering("getItems", "{userId: %s}", userID);
		
        try (Connection conn = sql2o.open()) {
            List<NotifyItem> items = conn.createQuery(SQL_GET_ITEM_BY_USER)
                    .addParameter("user_id", userID)
                    .executeAndFetch(NotifyItem.class);
            
            return items;
        }
	}

	@Override
	public List<NotifyItem> getItemsAfterPostTime(String userID, Date postTime) {
		logger.entering("getItemsAfterPostTime", "{userId: %s, postTime: %s}", userID, postTime);
		
		try (Connection conn = sql2o.open()) {
            List<NotifyItem> items = conn.createQuery(SQL_GET_ITEM_BY_USER_AFTER_POSTTIME)
                    .addParameter("user_id", userID)
                    .addParameter("post_time", postTime)
                    .executeAndFetch(NotifyItem.class);
            return items;
        }
	}

	@Override
	public int getCountOfItemsAfterNotifyTime(String userID, Date notifyTime) {
		logger.entering("getCountOfItemsAfterNotifyTime", "{userId: %s, notifyTime: %s}", userID, notifyTime);
		
		try (Connection conn = sql2o.open()) {
			return conn.createQuery(SQL_COUNT_OF_ITEMS_BY_USER_AFTER_NOTIFY_TIME)
					.addParameter("user_id", userID)
                    .addParameter("notify_time", notifyTime)
                    .executeScalar(Integer.class);
        }
	}
	
	@Override
	public Optional<NotifyItem> getItem(String userID, String item_id) {
		logger.entering("getItem", "{userId: %s, item_id: %s}", userID, item_id);
		
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
	public boolean addItem(NotifyItem toAddItem) {
		logger.entering("addItem", "{toAddItem: %s}", toAddItem);
		
		try (Connection conn = sql2o.beginTransaction()) {
			conn.createQuery(SQL_CREATE_ITEM)
	    		.addParameter("item_id", toAddItem.getItem_id())
	    		.addParameter("user_id", toAddItem.getUser_id())
	    		.addParameter("criteria_id", toAddItem.getCriteria_id())
	    		.addParameter("is_read", toAddItem.is_read())
	            .addParameter("notify_time", toAddItem.getNotify_time())
	            .addParameter("post_time", toAddItem.getPost_time())
	            .addParameter("price", toAddItem.getPrice())
	            .addParameter("size", toAddItem.getSize())
	            .addParameter("first_img_url", toAddItem.getFirst_img_url())
	            .addParameter("house_type", toAddItem.getHouse_type())
	            .addParameter("purpose_type", toAddItem.getPurpose_type())
	            .addParameter("title", toAddItem.getTitle())
	            .addParameter("addr", toAddItem.getAddr())
	            .executeUpdate();
			conn.commit();
            return true;
		}
	}
	
	@Override
	public boolean addItems(List<NotifyItem> toAddItems) {
		logger.entering("addItems", "{toAddItems: %s}", toAddItems);
		
        try (Connection conn = sql2o.beginTransaction()) {
        	
        	if (CollectionUtils.isNotEmpty(toAddItems)) {
        		
        		// 刪除較舊的Items，只保留最新的200個 Start:
        		String userId = toAddItems.get(0).getUser_id();
        		List<NotifyItem> existedItems = conn.createQuery(SQL_GET_ITEM_BY_USER_ORDER_BY_POST_TIME)
                        .addParameter("user_id", userId)
                        .executeAndFetch(NotifyItem.class);
        		
        		if (CollectionUtils.isNotEmpty(existedItems)) {
	        		int existedCount = existedItems.size();
	        		int toRemoveCount = Math.max((existedCount + toAddItems.size()), 200) - 200;
	        		
	        		for (int i=0; i<toRemoveCount; i++) {
	        			NotifyItem toRemvoeItem = existedItems.get(i);
	        			conn.createQuery(SQL_REMOVE_ITEM)
		        			.addParameter("item_id", toRemvoeItem.getItem_id())
		            		.addParameter("user_id", toRemvoeItem.getUser_id())
		                    .executeUpdate();       		
	        		}
        		}
        		// 刪除較舊的Item，只保留最新的200個 end.
        		
            	for (NotifyItem toAddItem: toAddItems){
                    conn.createQuery(SQL_CREATE_ITEM)
            		.addParameter("item_id", toAddItem.getItem_id())
            		.addParameter("user_id", toAddItem.getUser_id())
            		.addParameter("criteria_id", toAddItem.getCriteria_id())
            		.addParameter("is_read", toAddItem.is_read())
                    .addParameter("notify_time", toAddItem.getNotify_time())
                    .addParameter("post_time", toAddItem.getPost_time())
                    .addParameter("price", toAddItem.getPrice())
                    .addParameter("size", toAddItem.getSize())
                    .addParameter("first_img_url", toAddItem.getFirst_img_url())
                    .addParameter("house_type", toAddItem.getHouse_type())
                    .addParameter("purpose_type", toAddItem.getPurpose_type())
                    .addParameter("title", toAddItem.getTitle())
                    .addParameter("addr", toAddItem.getAddr())
                    .executeUpdate();       		
            	}
        	}
        	
            conn.commit();
            return true;
        }
	}

	@Override
	public String updateItem(NotifyItem item) {
		logger.entering("updateItem", "{item: %s}", item);
		
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
		logger.entering("getUnreadCount", "{userID: %s}", userID);
		
		try (Connection conn = sql2o.open()) {
			Long count = 0L;
			
            count = conn.createQuery(SQL_GET_UNREAD_COUNT_BY_USER)
                    .addParameter("user_id", userID)
                    .executeScalar(Long.class);
               
    		return count;
        }
	}
	
	@Override
	public void purgeOldItems(String userID, int retainItemCount) {
		logger.entering("purgeOldItems", "{userID: %s, retainItemCount: %s}", userID, retainItemCount);
		
		try (Connection conn = sql2o.beginTransaction()) {
    		List<NotifyItem> existedItems = conn.createQuery(SQL_GET_ITEM_BY_USER_ORDER_BY_POST_TIME)
                    .addParameter("user_id", userID)
                    .executeAndFetch(NotifyItem.class);
    		
    		if (CollectionUtils.isNotEmpty(existedItems)) {
        		int toRemoveCount = Math.max(existedItems.size(), retainItemCount) - retainItemCount;
        		
        		for (int i=0; i<toRemoveCount; i++) {
        			NotifyItem toRemvoeItem = existedItems.get(i);
        			conn.createQuery(SQL_REMOVE_ITEM)
	        			.addParameter("item_id", toRemvoeItem.getItem_id())
	            		.addParameter("user_id", toRemvoeItem.getUser_id())
	                    .executeUpdate();       		
        		}
    		}
        	
            conn.commit();
        }
	}
	
}
