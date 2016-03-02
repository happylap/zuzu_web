package com.lap.zuzuweb.model;

import org.apache.commons.lang3.StringUtils;

public enum ProductEnum {
	RADAR30("radar30", 30, 0), RADAR60("radar60", 60, 15), RADAR90("radar90", 90, 30);

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
