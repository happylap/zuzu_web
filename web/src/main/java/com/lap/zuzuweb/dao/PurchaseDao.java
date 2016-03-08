package com.lap.zuzuweb.dao;

import java.util.List;

import com.lap.zuzuweb.model.Purchase;
import com.lap.zuzuweb.model.User;

public interface PurchaseDao {

	public List<Purchase> getPurchase(String userID);

	//public String createPurchase(Purchase purchase, User user, Criteria criteria);
	
	public String createPurchase(Purchase purchase, User user);
}
