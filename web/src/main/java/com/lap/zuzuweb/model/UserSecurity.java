package com.lap.zuzuweb.model;

import java.util.Date;

import lombok.Data;

@Data
public class UserSecurity {
	
	private String user_id;
	private String hashed_password;
	private Date password_update_time;
	private String zuzu_token;
	private Date zuzu_token_update_time;
}