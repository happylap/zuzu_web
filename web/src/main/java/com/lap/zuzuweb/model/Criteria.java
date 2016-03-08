package com.lap.zuzuweb.model;

import java.util.Date;

import org.postgresql.util.PGobject;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class Criteria {
	public Criteria() {
		this.filters = new PGobject();
		this.filters.setType("json");
	}

	public void setFiltersValue(String str) {
		try {
			this.filters.setValue(str);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	/*
	public void setProductAndCalExpireTime(String productId) {
		
		ProductEnum product = ProductEnum.getEnum(productId);

		if (product == null) {
			throw new NullPointerException("Calculate expire time error: product is require.");
		}
		
		Date baseTime = null;
		if (this.expire_time != null && this.expire_time.after(CommonUtils.getUTCNow())) {
			baseTime = this.expire_time;
		} else {
			baseTime = CommonUtils.getUTCNow();
		}

		Calendar c = Calendar.getInstance();
		c.setTime(baseTime);
		c.add(Calendar.DATE, product.getStandardDays());
		c.add(Calendar.DATE, product.getExtraDays());
		
//		this.expire_time = c.getTime();
//		this.apple_product_id = productId;
	}
	*/

	private String criteria_id;
	private String user_id;
	private boolean enabled;
//	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
//	private Date expire_time;
//	private String apple_product_id;
	private PGobject filters;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
	private Date last_notify_time;
}
