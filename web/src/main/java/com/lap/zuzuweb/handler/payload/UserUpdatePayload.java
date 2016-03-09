package com.lap.zuzuweb.handler.payload;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lap.zuzuweb.model.User;

import lombok.Data;

@Data
public class UserUpdatePayload implements Validable {
	private String user_id;
	private String name;
	private String gender;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
	private Date birthday;
	private String picture_url;

	@Override
	public boolean isValid() {
		return StringUtils.isNotBlank(user_id);
	}
	
	public User toUser() {
		User user = new User();
		user.setUser_id(user_id);
		user.setName(name);
		user.setGender(gender);
		user.setBirthday(birthday);
		user.setPicture_url(picture_url);
		return user;
	}
}
