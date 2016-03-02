package com.lap.zuzuweb.model;

import org.apache.commons.lang3.StringUtils;

public enum ProductEnum {
	RADAR30("com.lap.zuzurentals.radar1", 30, 0), 
	RADAR60("com.lap.zuzurentals.radar2", 60, 0), 
	RADAR90("com.lap.zuzurentals.radar3", 90, 0),
	RADARFREE("com.lap.zuzurentals.radar4", 30, 0);

	private String productId;
	private int standardDays;
	private int extraDays;

	private ProductEnum(String productId, int standardDays, int extraDays) {
		this.productId = productId;
		this.standardDays = standardDays;
		this.extraDays = extraDays;
	}

	public String getProductId() {
		return productId;
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
