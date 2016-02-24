package com.lap.zuzuweb.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class Purchase {

	private String purchase_id; // This is a UUID 
	private String user_id;
	private String store;  // Maybe is apple, google
	private String product_id;
	private String product_title;
	private String product_locale_id;
	private Double product_price;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss'Z'", timezone="UTC")
	private Date purchase_time;
	
}
