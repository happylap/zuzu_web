package com.lap.zuzuweb.model;

import java.io.InputStream;
import java.util.Date;

import lombok.Data;

@Data
public class User {
	
	private String user_id;
	private String email;
	private Date register_time;
	private String name;
	private String gender;
	private Date birthday;
	private String picture_url;
	private InputStream purchase_receipt;
	private String provider;
	private String hashed_password;
	private String zuzu_token;
	
	private Date update_time;
}