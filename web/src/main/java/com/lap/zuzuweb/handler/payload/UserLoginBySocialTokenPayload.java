package com.lap.zuzuweb.handler.payload;

import lombok.Data;

@Data
public class UserLoginBySocialTokenPayload implements Validable {
	
	private String provider;
	private String access_token;
	
	@Override
	public boolean isValid() {
		return this.getProvider() != null && this.getAccess_token() != null;
	}
	
}
