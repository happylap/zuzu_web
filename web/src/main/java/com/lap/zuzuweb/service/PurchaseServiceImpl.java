package com.lap.zuzuweb.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lap.zuzuweb.dao.PurchaseDao;
import com.lap.zuzuweb.dao.ServiceDao;
import com.lap.zuzuweb.dao.UserDao;
import com.lap.zuzuweb.model.ProductEnum;
import com.lap.zuzuweb.model.Purchase;
import com.lap.zuzuweb.model.Service;
import com.lap.zuzuweb.model.User;
import com.lap.zuzuweb.util.CommonUtils;
import com.lap.zuzuweb.util.HttpUtils;

public class PurchaseServiceImpl implements PurchaseService{
	
	private PurchaseDao purchaseDao = null;
	private UserDao userDao = null;
	private ServiceDao serviceDao = null;
	
	public PurchaseServiceImpl(PurchaseDao purchaseDao, UserDao userDao, ServiceDao serviceDao)
	{
		this.purchaseDao = purchaseDao;
		this.userDao = userDao;
		this.serviceDao = serviceDao;
	}
	
	@Override
	public List<Purchase> getPurchase(String userID) {
		return this.purchaseDao.getPurchase(userID);
	}

	/*
	@Override
	@Deprecated
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
			criteria.setUser_id(user.getUser_id());
		}
		criteria.setEnabled(true);
		criteria.setFilters(new PGobject());
		criteria.getFilters().setType("json");
		criteria.setFiltersValue(criteriaFilters);

		// Calculate expire time
		criteria.setProductAndCalExpireTime(purchase.getProduct_id());
		
		this.purchaseDao.createPurchase(purchase, user, criteria);
		
		return criteria.getCriteria_id();
	}
	*/
	
	public String purchase(Purchase purchase, InputStream purchase_receipt) {
		System.out.println(purchase);
		
		if (StringUtils.isEmpty(purchase.getUser_id())) {
			throw new RuntimeException("Missing required field: user_id");
		}

		if (StringUtils.isEmpty(purchase.getStore())) {
			throw new RuntimeException("Missing required field: store");
		}
		
		if (StringUtils.isEmpty(purchase.getProduct_id())) {
			throw new RuntimeException("Missing required field: product_id");
		}
		
		if (purchase_receipt == null) {
			throw new RuntimeException("Missing required field: purchase_receipt");
		}
		
		if (StringUtils.isEmpty(purchase.getTransaction_id())) {
			throw new RuntimeException("Missing required field: transaction_id");
		}
		
		Optional<Purchase> existPurchase = purchaseDao.getPurchaseByTransactionId(purchase.getTransaction_id(), purchase.getStore());
		if (existPurchase.isPresent()) {
			throw new RuntimeException("Purchase transaction_id already exists: " + purchase.getTransaction_id());
		}
		
		purchase.setPurchase_time(CommonUtils.getUTCNow());
		purchase.set_valid(false);
		
		Optional<User> existUser = userDao.getUserById(purchase.getUser_id());
		
		if (!existUser.isPresent()) {
			throw new RuntimeException("User does not exist: " + purchase.getUser_id());
		}
		
		User user = existUser.get();
		user.setPurchase_receipt(purchase_receipt);
				
		return this.purchaseDao.createPurchase(purchase, user);
	}
	
	public void verify(String userID) {
		try {
			List<Purchase> purchases = this.purchaseDao.getPurchase(userID);
			if (CollectionUtils.isEmpty(purchases)) {
				return;
			}
			
			List<Purchase> invalid_purchases = purchases.stream().filter(p -> !p.is_valid()).collect(Collectors.toList());
			if (CollectionUtils.isEmpty(invalid_purchases)) {
				return;
			}
			
			Optional<User> existUser = this.userDao.getUserById(userID);
			if (!existUser.isPresent()) {
				return;
			}
			
			User user = existUser.get();
			if (user.getPurchase_receipt() == null) {
				return;
			}
			
			boolean isReceiptValid = false;
			JsonNode receiptVerifyResult = null;
			
			String jsonString = this.verifyReceipt(user.getPurchase_receipt());
			ObjectMapper mapper = new ObjectMapper();
			JsonNode actualObj = mapper.readTree(jsonString);
			
			if (actualObj != null) {
				JsonNode jsonNode_status = actualObj.get("status");
				// 0 if the receipt is valid
				if (jsonNode_status != null && jsonNode_status.intValue() == 0) {
					isReceiptValid = true;
					receiptVerifyResult = actualObj;
				}
			}
			
			if (!isReceiptValid || receiptVerifyResult == null) {
				return;
			}
			
			JsonNode jsonNode_receipt = receiptVerifyResult.get("receipt");
			if (jsonNode_receipt == null) {
				return;
			}
			
			JsonNode jsonArrNode_inapp = jsonNode_receipt.get("in_app");
			if (jsonArrNode_inapp == null) {
				return;
			}
			
			List<String> receiptTransactionIds = new ArrayList<String>();
			if (jsonArrNode_inapp.isArray()) {
				for (final JsonNode jsonNode_inapp : jsonArrNode_inapp) {
					if (jsonNode_inapp != null) {
						JsonNode jsonNode_transactionid = jsonNode_inapp.get("transaction_id");
						if (jsonNode_transactionid != null) {
							String transactionId = jsonNode_transactionid.textValue();
							if (StringUtils.isNotBlank(transactionId)) {
								receiptTransactionIds.add(transactionId);
							}
						}
					}
			    }
			}
			
			int toAddServiceDays = 0;
			
			for (Purchase invalid_purchase: invalid_purchases) {
				if (CollectionUtils.containsAny(receiptTransactionIds, Arrays.asList(invalid_purchase.getTransaction_id()))) {
					ProductEnum product = ProductEnum.getEnum(invalid_purchase.getProduct_id());
					if (product != null) {
						invalid_purchase.set_valid(true);
						toAddServiceDays += product.getStandardDays();
						toAddServiceDays += product.getExtraDays();
					}
				}
			}
			
			Optional<Service> existService = this.serviceDao.getService(userID);
			Service service = null;
			if (existService.isPresent()) {
				service = existService.get();
			} else {
				service = new Service();
				service.setUser_id(userID);
			}
			
			Date baseTime = null;
			if (service.getExpire_time() != null && service.getExpire_time().after(CommonUtils.getUTCNow())) {
				baseTime = service.getExpire_time();
			} else {
				baseTime = CommonUtils.getUTCNow();
			}

			Calendar c = Calendar.getInstance();
			c.setTime(baseTime);
			c.add(Calendar.DATE, toAddServiceDays);
			
			service.setExpire_time(c.getTime());
			service.setUpdate_time(CommonUtils.getUTCNow());
			
			if (existService.isPresent()) {
				this.serviceDao.updateService(service, invalid_purchases);
			} else {
				this.serviceDao.createService(service, invalid_purchases);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String verifyReceipt(InputStream purchaseReceipt) throws Exception {
		String url_prod = "https://buy.itunes.apple.com/verifyReceipt";
		String url_sandbox = "https://sandbox.itunes.apple.com/verifyReceipt";

		StringEntity se = new StringEntity(IOUtils.toString(purchaseReceipt));
		se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
		
		String jsonString = HttpUtils.post(url_prod, se);
		System.out.println("jsonString: " + jsonString);
		
		ObjectMapper mapper = new ObjectMapper();
		JsonNode actualObj = mapper.readTree(jsonString);

		if (actualObj != null) {
			JsonNode jsonNode_status = actualObj.get("status");

			// This receipt is from the test environment, but it was sent to the
			// production environment for verification.
			if (jsonNode_status != null && jsonNode_status.intValue() == 21007) {
				jsonString = HttpUtils.post(url_sandbox, se);
				System.out.println("jsonString: " + jsonString);
				actualObj = mapper.readTree(jsonString);
			}
		}
		
		return jsonString;
	}
}
