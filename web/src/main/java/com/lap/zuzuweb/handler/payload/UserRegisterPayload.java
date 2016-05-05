package com.lap.zuzuweb.handler.payload;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lap.zuzuweb.common.Provider;
import com.lap.zuzuweb.model.User;

import lombok.Data;

@Data
public class UserRegisterPayload implements Validable {
	private String provider;
	private String user_id;
	private String email;
	private String name;
	private String gender;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
	private Date birthday;
	private String picture_url;
	private String password;

	
	@Override
	public boolean isValid() {
		return /*this.getProvider() != null && */this.getEmail() != null;
	}
	
	public User toUser() {
		User user = new User();
		user.setUser_id(this.user_id);
		user.setProvider(this.provider);
		user.setEmail(this.email);
		user.setName(name);
		user.setGender(gender);
		user.setBirthday(birthday);
		user.setPicture_url(picture_url);
		
		return null;
	}
}
