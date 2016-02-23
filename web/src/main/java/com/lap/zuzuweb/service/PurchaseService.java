package com.lap.zuzuweb.service;

import java.io.InputStream;
import java.util.List;

import com.lap.zuzuweb.model.Purchase;

public interface PurchaseService {
	public List<Purchase> getPurchase(String userID);

	public String createPurchase(Purchase purchase, InputStream purchase_receipt, String criteriaFilters);

}
