package com.lap.zuzuweb.handler.payload;

import com.lap.zuzuweb.model.User;

import lombok.Data;

@Data
public class UserCheckPayload {
	
	private String email;
	private String provider;

	public void fromUser(User user) {
		this.email = user.getEmail();
		this.provider = user.getProvider();
	}

}
