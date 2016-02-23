package com.lap.zuzuweb.service;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.postgresql.util.PGobject;

import com.lap.zuzuweb.dao.CriteriaDao;
import com.lap.zuzuweb.dao.PurchaseDao;
import com.lap.zuzuweb.dao.UserDao;
import com.lap.zuzuweb.model.Criteria;
import com.lap.zuzuweb.model.Purchase;
import com.lap.zuzuweb.model.User;
import com.lap.zuzuweb.util.CommonUtils;

public class PurchaseServiceImpl implements PurchaseService{
	
	private PurchaseDao purchaseDao = null;
	private UserDao userDao = null;
	private CriteriaDao criteriaDao = null;
	
	public PurchaseServiceImpl(PurchaseDao purchaseDao, UserDao userDao, CriteriaDao criteriaDao)
	{
		this.purchaseDao = purchaseDao;
		this.userDao = userDao;
		this.criteriaDao = criteriaDao;
	}
	
	@Override
	public List<Purchase> getPurchase(String userID) {
		return this.purchaseDao.getPurchase(userID);
	}

	@Override
	public String purchaseCriteria(Purchase purchase, InputStream purchase_receipt, String criteriaFilters) {
		System.out.println(purchase);
		
		if (StringUtils.isBlank(purchase.getUser_id())) {
			throw new RuntimeException("Purchase user_id is required.");
		}

		if (StringUtils.isBlank(purchase.getStore())) {
			throw new RuntimeException("Purchase store is required.");
		}
		
		if (StringUtils.isBlank(purchase.getProduct_id())) {
			throw new RuntimeException("Purchase product_id is required.");
		}
		
		if (purchase_receipt == null) {
			throw new RuntimeException("Purchase receipt file is required.");
		}
		
		// TODO: 驗證 Purchase Receipt
//		if (false) {
//			throw new RuntimeException("Purchase receipt is invalid.");
//		}
		
		purchase.setPurchase_time(CommonUtils.getUTCNow());
		
		Optional<User> existUser = userDao.getUser(purchase.getUser_id());
		
		if (!existUser.isPresent()) {
			throw new RuntimeException("User does not exist. [" + purchase.getUser_id() + "]");
		}
		
		User user = existUser.get();
		user.setPurchase_receipt(purchase_receipt);
		
		Optional<Criteria> existCriteria = criteriaDao.getSingleCriteria(purchase.getUser_id());
		
		Criteria criteria = null;
		
		if (existCriteria.isPresent()) {
			criteria = existCriteria.get();
		} else {
			criteria = new Criteria();
			criteria.setCriteria_id(System.currentTimeMillis()+"");
			criteria.setUser_id(user.getUser_id());
		}
		
		criteria.setEnabled(true);
		criteria.setApple_product_id(purchase.getProduct_id());
		// TODO: Calculate expire_time and set to criteria
		criteria.setFilters(new PGobject());
		criteria.getFilters().setType("json");
		criteria.setFiltersValue(criteriaFilters);
		
		this.purchaseDao.createPurchase(purchase, user, criteria);
		
		return criteria.getCriteria_id();
	}
	
}
