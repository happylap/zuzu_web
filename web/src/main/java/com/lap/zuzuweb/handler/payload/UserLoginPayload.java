package com.lap.zuzuweb.handler.payload;

import com.lap.zuzuweb.Utilities;

import lombok.Data;

@Data
public class UserLoginPayload implements Validable {
	
	private String email;
	private String timestamp;
	private String signature;
	
	private String password;
	
	@Override
	public boolean isValid() {
		String hashedPassword = Utilities.getSaltedPassword(email, password);
		this.signature = Utilities.sign(hashedPassword, timestamp);
		
		return this.getEmail() != null;
		
	}
	
}
