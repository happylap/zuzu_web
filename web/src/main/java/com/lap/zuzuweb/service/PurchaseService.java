package com.lap.zuzuweb.service;

import java.io.InputStream;
import java.util.List;

import com.lap.zuzuweb.model.Purchase;

public interface PurchaseService {
	/**
	 * 
	 * @param userID
	 * @return a liet of Purchase
	 */
	public List<Purchase> getPurchase(String userID);

	/**
	 * 
	 * @param purchase
	 * @param purchase_receipt
	 * @param criteriaFilters
	 * @return criteria id
	 */
	//@Deprecated
	//public String purchaseCriteria(Purchase purchase, InputStream purchase_receipt, String criteriaFilters);

	public String purchase(Purchase purchase, InputStream purchase_receipt);
	
	public String purchaseForFree(Purchase purchase);

	//@Deprecated
	//public void verify(String userID);
	
	public void processService(String userID);
}
