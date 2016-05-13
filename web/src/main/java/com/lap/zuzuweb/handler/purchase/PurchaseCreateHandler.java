package com.lap.zuzuweb.handler.purchase;

import java.io.InputStream;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;

import org.apache.commons.lang3.StringUtils;

import com.lap.zuzuweb.ZuzuLogger;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.model.ProductEnum;
import com.lap.zuzuweb.model.Purchase;
import com.lap.zuzuweb.service.PurchaseService;
import com.lap.zuzuweb.util.CommonUtils;

import spark.Request;
import spark.Response;
import spark.Route;

public class PurchaseCreateHandler implements Route {

	private static final ZuzuLogger logger = ZuzuLogger.getLogger(PurchaseCreateHandler.class);
	
	private PurchaseService service = null;

	public PurchaseCreateHandler(PurchaseService service) {
		this.service = service;
	}
	
	@Override
    public Object handle(Request request, Response response) throws Exception {
		logger.entering("handle", "{request: %s, response: %s}", request, response);
		
		logger.entering();
		
		Answer answer = null;
		
		try {
			String location = "temp";          // the directory location where files will be stored
	    	long maxFileSize = 100000000;       // the maximum size allowed for uploaded files
	    	long maxRequestSize = 100000000;    // the maximum size allowed for multipart/form-data requests
	    	int fileSizeThreshold = 1024;       // the size threshold after which files will be written to disk
	
	    	MultipartConfigElement multipartConfigElement = new MultipartConfigElement(location, maxFileSize, maxRequestSize, fileSizeThreshold);
	    	request.raw().setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);
	    	
	    	logger.info("UserId: " + request.raw().getParameter("user_id"));
	    	logger.info("Store: " + request.raw().getParameter("store"));
	    	logger.info("ProductId: " + request.raw().getParameter("product_id"));
	    	logger.info("ProductTitle: " + request.raw().getParameter("product_title"));
	    	logger.info("ProductLocaleId: " + request.raw().getParameter("product_locale_id"));
	    	logger.info("ProductPrice: " + request.raw().getParameter("product_price"));
	    	logger.info("TransactionId: " + request.raw().getParameter("transaction_id"));
	    	
	    	if (StringUtils.isBlank(request.raw().getParameter("user_id"))) {
	    		logger.error("Missing required field: user_id");
	    		throw new IllegalArgumentException("Missing required field: user_id");  
	    	}
	    	
	    	String userId = request.raw().getParameter("user_id");
	    	String store = request.raw().getParameter("store");
	    	String productId = request.raw().getParameter("product_id");
	    	String productTitle = request.raw().getParameter("product_title");
	    	String productLocaleId = request.raw().getParameter("product_locale_id");
	    	String productPrice = request.raw().getParameter("product_price");
	    	String transactionId = request.raw().getParameter("transaction_id");
	    	Part purchaseReceiptFile = request.raw().getPart("purchase_receipt");
	    	
	    	Purchase purchase = new Purchase();
	    	purchase.setUser_id(userId);
	    	purchase.setStore(store);
	    	purchase.setProduct_id(productId);
	    	purchase.setProduct_title(productTitle);
	    	purchase.setProduct_locale_id(productLocaleId);
	    	if (StringUtils.isNoneEmpty(productPrice)) {
	    		purchase.setProduct_price(Double.valueOf(productPrice));
	    	}
	    	purchase.setTransaction_id(transactionId);

	    	logger.info("service purchase: " + purchase.toString());
	    	
	    	ProductEnum product = ProductEnum.getEnum(purchase.getProduct_id());
	    	
			if (product.isNeedVerifyReceipt()) {
				if (purchaseReceiptFile == null) {
		    		logger.error("Missing required field: purchase_receipt");
		    		throw new IllegalArgumentException("Missing required field: purchase_receipt");  
		    	}
				InputStream purchase_receipt = purchaseReceiptFile.getInputStream();
				String purchase_id = this.service.purchase(purchase, purchase_receipt);
				answer = Answer.ok(purchase_id);
			} else {
				String purchase_id = this.service.purchaseForFree(purchase);
				answer = Answer.ok(purchase_id);
			}
			
	    	// cleanup
	    	multipartConfigElement = null;
	    	purchaseReceiptFile = null;
	    	
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			answer = Answer.error(e.getMessage());
		}
		
		response.status(200);
        response.type("application/json");
		String json = CommonUtils.toJson(answer);
		logger.info(String.format("Route Path: (%s) %s, Answer: %s", request.requestMethod(), request.uri().toString(), json));
		return json;
		
	}

}