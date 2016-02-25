package com.lap.zuzuweb.util;

import java.util.Base64;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lap.zuzuweb.Secrets;

public class AuthUtils {
	
	public static boolean isSuperTokenValid(String token) {
		if (StringUtils.equals(Secrets.SUPER_TOKEN, token)) {
    		return true;
    	}
    	
		return false;
	}
	
	public static boolean isBasicTokenValid(String token) {
    	if (StringUtils.equals(Secrets.BASIC_TOKEN, token)) {
    		return true;
    	}
    	
		return false;
	}
	
	public static boolean isGoogleTokenValid(String token, String userId) {
		// TODO
		return false;
	}

	public static boolean isFacebookTokenValid(String token, String userId) {

		String url = String.format("https://graph.facebook.com/debug_token?input_token=%s&access_token=%s", token,
				token);
		try {
			String jsonString = HttpUtils.get(url);
			
			/** 
			 *  Return data seems like:
			 *  
			 *  {
			 *      "data": {
			 *          "app_id": "1039275546115316",
			 *          "application": "\u8c6c\u8c6c\u5feb\u79df",
			 *          "expires_at": 1461486439,
			 *          "is_valid": true,
			 *          "issued_at": 1456302439,
			 *          "metadata": {
			 *              "sso": "iphone-safari"
			 *          },
			 *          "scopes": [
			 *              "user_friends",
			 *              "email",
			 *              "public_profile"
			 *          ],
			 *          "user_id": "919778441391523"
			 *      }
			 *  }
			 */
			
			ObjectMapper mapper = new ObjectMapper();
			JsonNode actualObj = mapper.readTree(jsonString);

			if (!actualObj.isNull()) {
				JsonNode jsonNode_data = actualObj.get("data");

				if (jsonNode_data != null) {
					JsonNode jsonNode_userId = jsonNode_data.get("user_id");
					JsonNode jsonNode_isValid = jsonNode_data.get("is_valid");

					String _userId = null;
					if (jsonNode_userId != null) {
						_userId = jsonNode_userId.textValue();

					}

					boolean _isValid = false;
					if (jsonNode_isValid != null) {
						_isValid = jsonNode_isValid.booleanValue();
					}

					if (StringUtils.equals(userId, _userId) && _isValid == true) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void main(String[] args) throws Exception {
		String fbToken = "CAAOxNzGZCZCPQBAAw2GW2W9gESJ2DYk8Rmb0OH9C9QIKehuVUk4yuZABZBC6qZBjIYV8JTqMttbcmi4TFAYjGmVbZA7dslSC5GePUAena33krpInMxXrPcgju2ZAftdf3VWuEAwEpxsgMhPeVtLXW41J6BkDGcZAJxhQa3Auyy994MAekqr0aAYFZCpfQYfFlKZCvflrU7Ibnm5vgXC2Arb8IZA8H3zDSWp6HIdEavawtMgwwZDZD";
		String fbUserId = "919778441391523";
		boolean isValidByFacebookToken = AuthUtils.isFacebookTokenValid(fbToken, fbUserId);

		System.out.println(isValidByFacebookToken);

	}

}
