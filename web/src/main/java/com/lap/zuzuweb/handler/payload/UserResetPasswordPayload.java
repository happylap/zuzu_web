package com.lap.zuzuweb.handler.payload;

import lombok.Data;

@Data
public class UserResetPasswordPayload implements Validable {
	private String email;
	private String password;
	private String verification_code;
	
	@Override
	public boolean isValid() {
		return this.getEmail() != null && this.verification_code != null;
	}
}
