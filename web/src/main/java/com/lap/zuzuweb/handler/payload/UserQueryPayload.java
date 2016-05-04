package com.lap.zuzuweb.handler.payload;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lap.zuzuweb.model.User;

import lombok.Data;

@Data
public class UserQueryPayload {
	
	private String provider;
	private String user_id;
	private String email;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
	private Date register_time;
	private String name;
	private String gender;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
	private Date birthday;
	private String picture_url;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
	private Date update_time;

	public void fromUser(User user) {
		this.provider = user.getProvider();
		this.user_id = user.getUser_id();
		this.email = user.getEmail();
		this.register_time = user.getRegister_time();
		this.name = user.getName();
		this.gender = user.getGender();
		this.birthday = user.getBirthday();
		this.picture_url = user.getPicture_url();
		this.update_time = user.getUpdate_time();
	}

}
