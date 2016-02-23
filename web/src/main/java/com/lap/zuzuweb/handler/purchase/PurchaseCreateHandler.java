package com.lap.zuzuweb.handler.purchase;

import java.math.BigDecimal;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;

import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.model.Purchase;
import com.lap.zuzuweb.service.PurchaseService;

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
    	System.out.println("criteriaFilters: " + request.raw().getParameter("criteria_filters"));
    	
    	String userId = request.raw().getParameter("user_id");
    	String store = request.raw().getParameter("store");
    	String productId = request.raw().getParameter("product_id");
    	String productTitle = request.raw().getParameter("product_title");
    	String productLocaleId = request.raw().getParameter("product_locale_id");
    	String productPrice = request.raw().getParameter("product_price");
    	String criteriaFilters = request.raw().getParameter("criteria_filters");
    	Part purchaseReceiptFile = request.raw().getPart("purchase_receipt");
    	
    	Purchase purchase = new Purchase();
    	purchase.setUser_id(userId);
    	purchase.setStore(store);
    	purchase.setProduct_id(productId);
    	purchase.setProduct_title(productTitle);
    	purchase.setProduct_locale_id(productLocaleId);
    	purchase.setProduct_price(BigDecimal.valueOf(Double.valueOf(productPrice)));
    	
    	this.service.createPurchase(purchase, purchaseReceiptFile.getInputStream(), criteriaFilters);
    	
    	// cleanup
    	multipartConfigElement = null;
    	purchaseReceiptFile = null;
    	
		return new Answer(201, "");
	}

}