package com.lap.zuzuweb.dao;

import java.util.List;
import java.util.Optional;

import com.lap.zuzuweb.model.Purchase;
import com.lap.zuzuweb.model.User;

public interface PurchaseDao {

	public Optional<Purchase> getPurchaseByTransactionId(String transactionId, String store);
	
	public List<Purchase> getPurchase(String userID);

	//public String createPurchase(Purchase purchase, User user, Criteria criteria);
	
	public String createPurchase(Purchase purchase, User user);
}
