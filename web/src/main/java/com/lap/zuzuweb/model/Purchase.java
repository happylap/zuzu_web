package com.lap.zuzuweb.model;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

@Data
public class Purchase {

	private String purchase_id; // This is a UUID 
	private String user_id;
	private String store;  // Maybe is apple, google
	private String product_id;
	private String product_title;
	private String product_locale_id;
	private BigDecimal product_price;
	private Date purchase_time;
	
}
