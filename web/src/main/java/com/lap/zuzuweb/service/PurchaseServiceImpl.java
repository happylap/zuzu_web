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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
	private static final Logger logger = LoggerFactory.getLogger(PurchaseServiceImpl.class);
	
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

	@Override
	public String purchaseForFree(Purchase purchase) {
		logger.debug("purchaseForFree enter:");
		
		if (StringUtils.isEmpty(purchase.getUser_id())) {
			throw new IllegalArgumentException("Missing required field: user_id");
		}

		if (StringUtils.isEmpty(purchase.getStore())) {
			throw new IllegalArgumentException("Missing required field: store");
		}
		
		if (StringUtils.isEmpty(purchase.getProduct_id())) {
			throw new IllegalArgumentException("Missing required field: product_id");
		}
		
		ProductEnum product = ProductEnum.getEnum(purchase.getProduct_id());
		if (product.isNeedVerifyReceipt()) {
			throw new IllegalArgumentException("RadarFree product already exists");
		}
		
		List<Purchase> purchases = purchaseDao.getPurchase(purchase.getUser_id());
		if (CollectionUtils.isNotEmpty(purchases)) {
			
			for (Purchase purchaseInDB: purchases) {
				if (StringUtils.equalsIgnoreCase(purchase.getProduct_id(), purchaseInDB.getProduct_id())) {
					throw new IllegalArgumentException(String.format("Product %s already exists", product.getProductId()));
				}
			}
		}
		
		Purchase newPurchase = new Purchase();
		newPurchase.setUser_id(purchase.getUser_id());
		newPurchase.setStore(purchase.getStore());
		newPurchase.setProduct_id(purchase.getProduct_id());
		newPurchase.setProduct_title(purchase.getProduct_title());
		newPurchase.setProduct_price(purchase.getProduct_price());
		newPurchase.setProduct_locale_id(purchase.getProduct_locale_id());
		newPurchase.setPurchase_time(CommonUtils.getUTCNow());
		newPurchase.setTransaction_id(CommonUtils.getRandomUUID());
		newPurchase.set_valid(false);
		
		String purchaseId = this.purchaseDao.createPurchase(newPurchase);
		
		logger.debug("purchaseForFree exit.");
		return purchaseId;
	}

	@Override
	public String purchase(Purchase purchase, InputStream purchase_receipt) {
		logger.debug("purchase enter:");
		
		if (StringUtils.isEmpty(purchase.getUser_id())) {
			throw new IllegalArgumentException("Missing required field: user_id");
		}

		if (StringUtils.isEmpty(purchase.getStore())) {
			throw new IllegalArgumentException("Missing required field: store");
		}
		
		if (StringUtils.isEmpty(purchase.getProduct_id())) {
			throw new IllegalArgumentException("Missing required field: product_id");
		}
		
		if (purchase_receipt == null) {
			throw new IllegalArgumentException("Missing required field: purchase_receipt");
		}
		
		if (StringUtils.isEmpty(purchase.getTransaction_id())) {
			throw new IllegalArgumentException("Missing required field: transaction_id");
		}
		
		Optional<Purchase> existPurchase = purchaseDao.getPurchaseByTransactionId(purchase.getTransaction_id(), purchase.getStore());
		if (existPurchase.isPresent()) {
			throw new IllegalArgumentException("Purchase transaction_id already exists: " + purchase.getTransaction_id());
		}
		
		//ProductEnum product = ProductEnum.getEnum(purchase.getProduct_id());
		
		Purchase newPurchase = new Purchase();
		newPurchase.setUser_id(purchase.getUser_id());
		newPurchase.setStore(purchase.getStore());
		newPurchase.setProduct_id(purchase.getProduct_id());
		newPurchase.setProduct_title(purchase.getProduct_title());
		newPurchase.setProduct_price(purchase.getProduct_price());
		newPurchase.setProduct_locale_id(purchase.getProduct_locale_id());
		newPurchase.setPurchase_time(CommonUtils.getUTCNow());
		newPurchase.setTransaction_id(purchase.getTransaction_id());
		newPurchase.set_valid(false);
		
		Optional<User> existUser = userDao.getUserById(newPurchase.getUser_id());
		
		if (!existUser.isPresent()) {
			logger.error("purchase exit.");
			throw new IllegalArgumentException("User does not exist: " + newPurchase.getUser_id());
		}
		
		User user = existUser.get();
		user.setPurchase_receipt(purchase_receipt);
		
		String purchaseId = this.purchaseDao.createPurchase(newPurchase, user);
		
		logger.debug("purchase exit.");
		return purchaseId;
	}
	
	@Deprecated
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
					invalid_purchase.set_valid(true);
					toAddServiceDays += product.getStandardDays();
					toAddServiceDays += product.getExtraDays();
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

	@Override
	public void processService(String userID) {
		logger.info("processService enter:");
		
		logger.info("userID: " + userID);
		
		List<Purchase> purchases = this.purchaseDao.getPurchase(userID);
		if (CollectionUtils.isEmpty(purchases)) {
			logger.info("processService exit.");
			return;
		}
		
		List<Purchase> invalidPurchases = purchases.stream().filter(p -> !p.is_valid()).collect(Collectors.toList());
		if (CollectionUtils.isEmpty(invalidPurchases)) {
			logger.info("processService exit.");
			return;
		}
		
		List<Purchase> validPurchases = new ArrayList<Purchase>();
		List<Purchase> needVerifyPurchases = new ArrayList<Purchase>();
		
		for (Purchase invalidPurchase: invalidPurchases) {
			ProductEnum product = ProductEnum.getEnum(invalidPurchase.getProduct_id());
			// 不需要驗證的Purchase，直接認定為有效Purchase
			if (product != null && !product.isNeedVerifyReceipt()) {
				validPurchases.add(invalidPurchase);
			}
			// 加入到"待驗證"清單中
			if (product != null && product.isNeedVerifyReceipt()) {
				needVerifyPurchases.add(invalidPurchase);
			}
		}
		
		// 驗證Purchase
		if (CollectionUtils.isNotEmpty(needVerifyPurchases)) {
			List<String> validTransactionIds = this.getTransactionIdsFormReceipt(userID);
			
			if (CollectionUtils.isNotEmpty(validTransactionIds)) {
				for (Purchase needVerifyPurchase: needVerifyPurchases) {
					if (validTransactionIds.contains(needVerifyPurchase.getTransaction_id())) {
						validPurchases.add(needVerifyPurchase);
					}
				}
			}
		}
		
		// 進行 Service expire_time 的計算
		int toaddDays = 0;
		for (Purchase validPurchase: validPurchases) {			
			ProductEnum product = ProductEnum.getEnum(validPurchase.getProduct_id());
			if (product != null) {
				validPurchase.set_valid(true);
				toaddDays += product.getStandardDays();
				toaddDays += product.getExtraDays();
			}
		}
		logger.info("toaddDays: " + toaddDays);
		
		Optional<Service> existService = this.serviceDao.getService(userID);
		
		Service service = null;
		if (existService.isPresent()) {
			service = existService.get();
		} else {
			service = new Service();
			service.setUser_id(userID);
		}
		
		Date newStartTime = this.calStartTime(service.getStart_time(), service.getExpire_time());
		service.setStart_time(newStartTime);
		
		Date newExpireTime = this.calExpireTime(service.getExpire_time(), toaddDays);
		service.setExpire_time(newExpireTime);
		
		service.setUpdate_time(CommonUtils.getUTCNow());
		
		if (existService.isPresent()) {
			this.serviceDao.updateService(service, validPurchases);
		} else {
			this.serviceDao.createService(service, validPurchases);
		}
		logger.info("processService exit.");
	}
	
	private Date calStartTime(Date origStartTime, Date origExpireTime) {
		Date newStartTime = null;
		
		if (origStartTime != null) {
			newStartTime = origStartTime;
		} else {
			newStartTime = CommonUtils.getUTCNow();
		}
		
		if (origExpireTime == null || origExpireTime.before(CommonUtils.getUTCNow())) {
			newStartTime = CommonUtils.getUTCNow();
		}
		
		return newStartTime;
		
	}
	
	private Date calExpireTime(Date origExpireTime, int toaddDays) {
		logger.info("calExpireTime enter:");
		
		Date baseTime = null;
		if (origExpireTime != null && origExpireTime.after(CommonUtils.getUTCNow())) {
			baseTime = origExpireTime;
		} else {
			baseTime = CommonUtils.getUTCNow();
		}

		Calendar c = Calendar.getInstance();
		c.setTime(baseTime);
		c.add(Calendar.DATE, toaddDays);
		
		logger.info("calExpireTime exit.");
		return c.getTime();
	}

	private List<String> getTransactionIdsFormReceipt(String userID) {
		logger.info("getTransactionIdsFormReceipt enter:");
		
		List<String> emptyCollection = new ArrayList<String>();
		
		Optional<User> existUser = this.userDao.getUserById(userID);
		if (!existUser.isPresent()) {
			return emptyCollection;
		}
		
		User user = existUser.get();
		if (user.getPurchase_receipt() == null) {
			return emptyCollection;
		}
		
		String jsonString = this.verifyReceipt(user.getPurchase_receipt());
		
		JsonNode actualObj = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			actualObj = mapper.readTree(jsonString);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		boolean isReceiptValid = false;
		JsonNode receiptVerifyResult = null;
		
		if (actualObj != null) {
			JsonNode jsonNode_status = actualObj.get("status");
			// 0 if the receipt is valid
			if (jsonNode_status != null && jsonNode_status.intValue() == 0) {
				isReceiptValid = true;
				receiptVerifyResult = actualObj;
			}
		}
		
		if (!isReceiptValid || receiptVerifyResult == null) {
			return emptyCollection;
		}
		
		JsonNode jsonNode_receipt = receiptVerifyResult.get("receipt");
		if (jsonNode_receipt == null) {
			return emptyCollection;
		}
		
		JsonNode jsonArrNode_inapp = jsonNode_receipt.get("in_app");
		if (jsonArrNode_inapp == null) {
			return emptyCollection;
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

		logger.info("getTransactionIdsFormReceipt exit.");
		return receiptTransactionIds;
	}
	
	private String verifyReceipt(InputStream purchaseReceipt) {
		logger.info("verifyReceipt enter:");
		
		String url_prod = "https://buy.itunes.apple.com/verifyReceipt";
		String url_sandbox = "https://sandbox.itunes.apple.com/verifyReceipt";
		String jsonString = null;
		
		try {
			logger.info(String.format("Verify Receipt URL: %s", url_prod));
			StringEntity se = new StringEntity(IOUtils.toString(purchaseReceipt));
			se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			jsonString = HttpUtils.post(url_prod, se);
			
			ObjectMapper mapper = new ObjectMapper();
			JsonNode actualObj = mapper.readTree(jsonString);
	
			if (actualObj != null) {
				JsonNode jsonNode_status = actualObj.get("status");
				// This receipt is from the test environment, but it was sent to the
				// production environment for verification.
				if (jsonNode_status != null && jsonNode_status.intValue() == 21007) {
					logger.info(String.format("Verify Receipt URL: %s", url_sandbox));
					jsonString = HttpUtils.post(url_sandbox, se);
				}
			}
		} catch (Exception e) {
			logger.error(String.format("Verify Receipt Error: %s", e.getMessage()), e);
		}

		logger.info(String.format("Verify Receipt Result: %s", jsonString));
		logger.info("verifyReceipt exit.");
		return jsonString;
	}
}
