package com.lap.zuzuweb.dao.Sql2O;

import java.util.List;
import java.util.UUID;

import org.sql2o.Connection;

import com.lap.zuzuweb.dao.PurchaseDao;
import com.lap.zuzuweb.model.Purchase;
import com.lap.zuzuweb.model.User;

public class PurchaseDaoBySql2O extends AbstratcDaoBySql2O implements PurchaseDao {

	static private String SQL_GET_PURCHASE = "SELECT purchase_id, user_id, store, product_id, product_title, product_locale_id, product_price, purchase_time, transaction_id, is_valid" 
			+ " FROM \"ZuzuPurchase\" " 
			+ " WHERE user_id = :user_id";
	
	static private String SQL_CREATE_PURCHASE = "INSERT INTO \"ZuzuPurchase\"(purchase_id, user_id, store, product_id, product_title, product_locale_id, product_price, purchase_time, transaction_id, is_valid) "
			+ " VALUES (:purchase_id, :user_id, :store, :product_id, :product_title, :product_locale_id, :product_price, :purchase_time, :transaction_id, :is_valid)";
	
	static private String SQL_UPDATE_USER_PURCHASE_RECEIPT = "UPDATE \"ZuZuUser\" SET purchase_receipt=:purchase_receipt"
			+ " WHERE user_id = :user_id";
	
//	static private String SQL_CREATE_CRITERIA = "INSERT INTO \"Criteria\"(criteria_id, user_id, enabled, expire_time, "
//			+ " apple_product_id, last_notify_time, filters) "
//			+ " VALUES (:criteria_id, :user_id, :enabled, :expire_time, :apple_product_id, :last_notify_time, :filters)";
//	
//	
//	static private String SQL_UPDATE_CRITERIA = "UPDATE \"Criteria\" SET enabled=:enabled, expire_time=:expire_time,"
//			+ " apple_product_id=:apple_product_id, last_notify_time=:last_notify_time, filters=:filters"
//			+ " WHERE criteria_id=:criteria_id AND user_id=:user_id";
//	
//	static private String SQL_CREATE_LOG = "INSERT INTO \"ZuzuLog\"(device_id, user_id, log_type, log_comment, log_time) "
//			+ " VALUES (:device_id, :user_id, :log_type, :log_comment, :log_time)";
	
	@Override
	public List<Purchase> getPurchase(String userID) {
		try (Connection conn = sql2o.open()) {
            return conn.createQuery(SQL_GET_PURCHASE)
                    .addParameter("user_id", userID)
                    .executeAndFetch(Purchase.class);
        }
	}
	
	/*
	@Override
	public String createPurchase(Purchase purchase, User user, Criteria criteria) {
		try (Connection conn = sql2o.beginTransaction()) {
			conn.createQuery(SQL_CREATE_PURCHASE)
					.addParameter("purchase_id", UUID.randomUUID())
					.addParameter("user_id", purchase.getUser_id())
					.addParameter("store", purchase.getStore())
					.addParameter("product_id", purchase.getProduct_id())
					.addParameter("product_title", purchase.getProduct_title())
					.addParameter("product_locale_id", purchase.getProduct_locale_id())
					.addParameter("product_price", purchase.getProduct_price())
					.addParameter("purchase_time", purchase.getPurchase_time())
					.executeUpdate();
			
			if (criteria != null) {
				
				Query query = null;
				
				if (criteria.getCriteria_id() == null) {
					criteria.setCriteria_id(System.currentTimeMillis()+"");
					query = conn.createQuery(SQL_CREATE_CRITERIA);
				} else {
					query = conn.createQuery(SQL_UPDATE_CRITERIA);
				}
				
				query.addParameter("enabled", criteria.isEnabled())
			            .addParameter("expire_time", criteria.getExpire_time())
			            .addParameter("apple_product_id", criteria.getApple_product_id())
			            .addParameter("last_notify_time", criteria.getLast_notify_time())
			            .addParameter("filters", criteria.getFilters())
			            .addParameter("criteria_id", criteria.getCriteria_id())
			            .addParameter("user_id", criteria.getUser_id())
			            .executeUpdate();
				
				if (criteria.getCriteria_id() != null && criteria.getExpire_time() != null) {
					String comment = String.format("%s (%s)", CommonUtils.getUTCStringFromDate(criteria.getExpire_time()), criteria.getApple_product_id());
					
					conn.createQuery(SQL_CREATE_LOG)
						.addParameter("device_id", "")
			            .addParameter("user_id", criteria.getUser_id())
			            .addParameter("log_type", Log.Type.EXPIRE_TIME)
			            .addParameter("log_comment", comment)
			            .addParameter("log_time", CommonUtils.getUTCNow())
			            .executeUpdate();
				}
			}
			
			conn.createQuery(SQL_UPDATE_USER_PURCHASE_RECEIPT)
		    		.addParameter("purchase_receipt", user.getPurchase_receipt())
		            .addParameter("user_id", user.getUser_id())
		            .executeUpdate();
			
			
			
			conn.commit();
			return purchase.getPurchase_id();
		}
	}
	*/
	

	public String createPurchase(Purchase purchase, User user) {
		try (Connection conn = sql2o.beginTransaction()) {
			conn.createQuery(SQL_CREATE_PURCHASE)
					.addParameter("purchase_id", UUID.randomUUID())
					.addParameter("user_id", purchase.getUser_id())
					.addParameter("store", purchase.getStore())
					.addParameter("product_id", purchase.getProduct_id())
					.addParameter("product_title", purchase.getProduct_title())
					.addParameter("product_locale_id", purchase.getProduct_locale_id())
					.addParameter("product_price", purchase.getProduct_price())
					.addParameter("purchase_time", purchase.getPurchase_time())
					.addParameter("transaction_id", purchase.getTransaction_id())
					.addParameter("is_valid", purchase.is_valid())
					.executeUpdate();
			
			conn.createQuery(SQL_UPDATE_USER_PURCHASE_RECEIPT)
		    		.addParameter("purchase_receipt", user.getPurchase_receipt())
		            .addParameter("user_id", user.getUser_id())
		            .executeUpdate();
			
			conn.commit();
			return purchase.getPurchase_id();
		}
	}
	
}
