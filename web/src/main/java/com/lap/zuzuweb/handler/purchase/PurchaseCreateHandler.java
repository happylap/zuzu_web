package com.lap.zuzuweb.handler.purchase;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;

import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.model.Purchase;
import com.lap.zuzuweb.service.PurchaseService;
import com.lap.zuzuweb.util.CommonUtils;

import spark.Request;
import spark.Response;
import spark.Route;

public class PurchaseCreateHandler implements Route {

	private PurchaseService service = null;

	public PurchaseCreateHandler(PurchaseService service) {
		this.service = service;
	}
	
	@Override
    public Object handle(Request request, Response response) throws Exception {
		
		try {
			String location = "temp";          // the directory location where files will be stored
	    	long maxFileSize = 100000000;       // the maximum size allowed for uploaded files
	    	long maxRequestSize = 100000000;    // the maximum size allowed for multipart/form-data requests
	    	int fileSizeThreshold = 1024;       // the size threshold after which files will be written to disk
	
	    	MultipartConfigElement multipartConfigElement = new MultipartConfigElement(location, maxFileSize, maxRequestSize, fileSizeThreshold);
	    	request.raw().setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);
	    	
	    	System.out.println("UserId: " + request.raw().getParameter("user_id"));
	    	System.out.println("Store: " + request.raw().getParameter("user_id"));
	    	System.out.println("ProductId: " + request.raw().getParameter("product_id"));
	    	System.out.println("ProductTitle: " + request.raw().getParameter("product_title"));
	    	System.out.println("ProductLocaleId: " + request.raw().getParameter("product_locale_id"));
	    	System.out.println("ProductPrice: " + request.raw().getParameter("product_price"));
	    	//System.out.println("criteriaFilters: " + request.raw().getParameter("criteria_filters"));
	    	System.out.println("TransactionId: " + request.raw().getParameter("transaction_id"));
	    	
	    	String userId = request.raw().getParameter("user_id");
	    	String store = request.raw().getParameter("store");
	    	String productId = request.raw().getParameter("product_id");
	    	String productTitle = request.raw().getParameter("product_title");
	    	String productLocaleId = request.raw().getParameter("product_locale_id");
	    	String productPrice = request.raw().getParameter("product_price");
	    	String transactionId = request.raw().getParameter("transaction_id");
	    	//String criteriaFilters = request.raw().getParameter("criteria_filters");
	    	Part purchaseReceiptFile = request.raw().getPart("purchase_receipt");
	    	
	    	Purchase purchase = new Purchase();
	    	purchase.setUser_id(userId);
	    	purchase.setStore(store);
	    	purchase.setProduct_id(productId);
	    	purchase.setProduct_title(productTitle);
	    	purchase.setProduct_locale_id(productLocaleId);
	    	purchase.setProduct_price(Double.valueOf(productPrice));
	    	purchase.setTransaction_id(transactionId);
	    	
	    	if (purchaseReceiptFile == null) {
	    		throw new RuntimeException("Purchase receipt file is required."); 
	    	}
	    	
	    	//String criteriaId = this.service.purchaseCriteria(purchase, purchaseReceiptFile.getInputStream(), criteriaFilters);
	    	String purchase_id = this.service.purchase(purchase, purchaseReceiptFile.getInputStream());
	    	
	    	// cleanup
	    	multipartConfigElement = null;
	    	purchaseReceiptFile = null;
	    	
	    	Answer answer = Answer.ok(purchase_id);
	    	return CommonUtils.toJson(answer);
            
		} catch (Exception e) {
			
			e.printStackTrace();
			return CommonUtils.toJson(Answer.error(e.getMessage()));
		}
	}

}