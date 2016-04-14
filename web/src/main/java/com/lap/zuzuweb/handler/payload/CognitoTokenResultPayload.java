package com.lap.zuzuweb.handler.payload;

import lombok.Data;

@Data
public class CognitoTokenResultPayload implements Validable {

	/**
	 * A unique identifier in the format REGION:GUID.
	 */
	private String identityId;

	/**
	 * An OpenID token.
	 */
	private String token;

	@Override
	public boolean isValid() {
		return true;
	}

}
