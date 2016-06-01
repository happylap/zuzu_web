package com.lap.zuzuweb.handler.payload;

import lombok.Data;

@Data
public class SNSSenderPayload  implements Validable {

	private String userId;
	private String targetARN;
	private String message;	
	
	@Override
	public boolean isValid() {
		return true;
	}
}
