package com.lap.zuzuweb.model;

import org.apache.commons.lang3.StringUtils;

public enum ProductEnum {
	RADAR1("com.lap.zuzurentals.radar1", true, 30, 0), 
	RADAR2("com.lap.zuzurentals.radar2", true, 60, 6), 
	RADAR3("com.lap.zuzurentals.radar3", true, 90, 18),
	RADARFREE1("com.lap.zuzurentals.radarfree1", false, 14, 0);
	
	private String productId;
	private boolean needVerifyReceipt;
	private int standardDays;
	private int extraDays;

	private ProductEnum(String productId, boolean needVerifyReceipt, int standardDays, int extraDays) {
		this.productId = productId;
		this.needVerifyReceipt = needVerifyReceipt;
		this.standardDays = standardDays;
		this.extraDays = extraDays;
	}

	public String getProductId() {
		return productId;
	}
	
	public boolean isNeedVerifyReceipt() {
		return needVerifyReceipt;
	}

	public int getStandardDays() {
		return standardDays;
	}

	public int getExtraDays() {
		return extraDays;
	}

	public static ProductEnum getEnum(String productId) {
		for (ProductEnum v : values()) {
			if (StringUtils.equalsIgnoreCase(productId, v.getProductId())) {
				return v;
			}
		}
		throw new IllegalArgumentException("The productId is invalid. [" + productId + "]");
	}
}
