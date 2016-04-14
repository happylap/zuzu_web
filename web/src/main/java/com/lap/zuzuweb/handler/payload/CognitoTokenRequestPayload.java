package com.lap.zuzuweb.handler.payload;

import java.util.Map;

import com.lap.zuzuweb.Configuration;
import com.lap.zuzuweb.Utilities;

import lombok.Data;

@Data
public class CognitoTokenRequestPayload implements Validable {
	
	private String userId;
	private String signature;
	private String timestamp;
    private String identityId;
    private Map<String, String>	logins;
    
    @Override
	public boolean isValid() {
    	
    	// start
		StringBuilder stringToSign = new StringBuilder();
		stringToSign.append(timestamp);
		if (logins != null && logins.containsKey(Configuration.DEVELOPER_PROVIDER_NAME)) {
			stringToSign.append(Configuration.DEVELOPER_PROVIDER_NAME);
			stringToSign.append(logins.get(Configuration.DEVELOPER_PROVIDER_NAME));
		}

		if (identityId != null) {
			stringToSign.append(identityId);
		}
		
		String computedSignature = Utilities.sign(stringToSign.toString(), signature);
    	this.signature = computedSignature;
    	// end
    	
		return true;
	}
    
}
