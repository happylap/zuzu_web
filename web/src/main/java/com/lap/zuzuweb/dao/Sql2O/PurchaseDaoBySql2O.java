package com.lap.zuzuweb.dao.Sql2O;

import java.util.List;
import java.util.Optional;

import org.sql2o.Connection;

import com.lap.zuzuweb.ZuzuLogger;
import com.lap.zuzuweb.dao.PurchaseDao;
import com.lap.zuzuweb.model.Purchase;
import com.lap.zuzuweb.model.User;
import com.lap.zuzuweb.util.CommonUtils;

public class PurchaseDaoBySql2O extends AbstratcDaoBySql2O implements PurchaseDao {

	private static final ZuzuLogger logger = ZuzuLogger.getLogger(PurchaseDaoBySql2O.class);
	
	static private String SQL_GET_PURCHASE = "SELECT purchase_id, user_id, store, product_id, product_title, product_locale_id, product_price, purchase_time, transaction_id, is_valid" 
			+ " FROM \"ZuzuPurchase\" " 
			+ " WHERE user_id = :user_id ORDER BY purchase_time desc";
	
	static private String SQL_GET_PURCHASE_BY_TRANSACTION_ID = "SELECT purchase_id, user_id, store, product_id, product_title, product_locale_id, product_price, purchase_time, transaction_id, is_valid" 
			+ " FROM \"ZuzuPurchase\" " 
			+ " WHERE transaction_id = :transaction_id AND store=:store";
	
	static private String SQL_CREATE_PURCHASE = "INSERT INTO \"ZuzuPurchase\"(purchase_id, user_id, store, product_id, product_title, product_locale_id, product_price, purchase_time, transaction_id, is_valid) "
			+ " VALUES (:purchase_id, :user_id, :store, :product_id, :product_title, :product_locale_id, :product_price, :purchase_time, :transaction_id, :is_valid)";
	
	static private String SQL_UPDATE_USER_PURCHASE_RECEIPT = "UPDATE \"ZuZuUser\" SET purchase_receipt=:purchase_receipt"
			+ " WHERE user_id = :user_id";
	
	@Override
	public Optional<Purchase> getPurchaseByTransactionId(String transactionId, String store) {
		logger.entering("getPurchaseByTransactionId", "{transactionId: %s, store: %s}", transactionId, store);
		
		try (Connection conn = sql2o.open()) {
			Purchase purchase = conn.createQuery(SQL_GET_PURCHASE_BY_TRANSACTION_ID)
	                .addParameter("transaction_id", transactionId)
	                .addParameter("store", store)
	                .executeAndFetchFirst(Purchase.class);
            return purchase != null ? Optional.of(purchase) : Optional.empty();
        }
	}
	
	@Override
	public List<Purchase> getPurchase(String userID) {
		logger.entering("getPurchase", "{userID: %s}", userID);
		
		try (Connection conn = sql2o.open()) {
            return conn.createQuery(SQL_GET_PURCHASE)
                    .addParameter("user_id", userID)
                    .executeAndFetch(Purchase.class);
        }
	}

	@Override
	public String createPurchase(Purchase purchase) {
		logger.entering("createPurchase", "{purchase: %s}", purchase);
		
		purchase.setPurchase_id(CommonUtils.getRandomUUID());
		
		try (Connection conn = sql2o.beginTransaction()) {
			
			conn.createQuery(SQL_CREATE_PURCHASE)
					.addParameter("purchase_id", purchase.getPurchase_id())
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
			
			conn.commit();
			
			logger.exit("createPurchase", purchase.getPurchase_id());
			return purchase.getPurchase_id();
		}
	}

	@Override
	public String createPurchase(Purchase purchase, User user) {

		logger.entering("createPurchase", "{purchase: %s, user: %s}", purchase, user);
		
		purchase.setPurchase_id(CommonUtils.getRandomUUID());
		
		try (Connection conn = sql2o.beginTransaction()) {
			
			conn.createQuery(SQL_CREATE_PURCHASE)
					.addParameter("purchase_id", purchase.getPurchase_id())
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

			logger.exit("createPurchase", purchase.getPurchase_id());
			return purchase.getPurchase_id();
		}
	}
	
}
