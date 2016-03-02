package com.lap.zuzuweb.util;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lap.zuzuweb.Secrets;

public class AuthUtils {
	
	public static boolean isSuperTokenValid(String token)  {
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
	
	public static boolean isGoogleTokenValid(String token, String sub) throws Exception {
		
		String url = String.format("https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=%s", token);
		
		try {
			String jsonString = HttpUtils.get(url);
			
			/**
			 * Here's an example response:
			 * 
			 * {
			 *   // These six fields are included in all Google ID Tokens.
			 *   "iss": "https://accounts.google.com",
			 *   "sub": "110169484474386276334",
			 *   "azp": "1008719970978-hb24n2dstb40o45d4feuo2ukqmcc6381.apps.googleusercontent.com",
			 *   "aud": "1008719970978-hb24n2dstb40o45d4feuo2ukqmcc6381.apps.googleusercontent.com",
			 *   "iat": "1433978353",
			 *   "exp": "1433981953",
			 *   // These seven fields are only included when the user has granted the "profile" and
			 *   // "email" OAuth scopes to the application.
			 *   "email": "testuser@gmail.com",
			 *   "email_verified": "true",
			 *   "name" : "Test User",
			 *   "picture": "https://lh4.googleusercontent.com/-kYgzyAWpZzJ/ABCDEFGHI/AAAJKLMNOP/tIXL9Ir44LE/s99-c/photo.jpg",
			 *   "given_name": "Test",
			 *   "family_name": "User",
			 *   "locale": "en"
			 * }
			 */
			
			ObjectMapper mapper = new ObjectMapper();
			JsonNode actualObj = mapper.readTree(jsonString);

			if (!actualObj.isNull()) {
				
				JsonNode jsonNode_error = actualObj.get("error_description");
				if (jsonNode_error != null) {
					throw new RuntimeException("Valid Google Token Error: "+ jsonNode_error.textValue());
				}
				
				JsonNode jsonNode_sub = actualObj.get("sub");
				JsonNode jsonNode_exp = actualObj.get("exp");

				String _sub = null;
				if (jsonNode_sub != null) {
					_sub = jsonNode_sub.textValue();
				}
				
				Date _exp_time = null;
				
				if (jsonNode_exp != null) {
					
					String _exp = jsonNode_exp.textValue();
					Calendar c = Calendar.getInstance();
					c.setTimeInMillis(Long.valueOf(_exp) * 1000);
					_exp_time = c.getTime();
				}
				
				if (!StringUtils.equals(sub, _sub)) {
					throw new RuntimeException("Valid Google Token Error: The 'sub' is not matched. [" + sub + "]");
				}
				else if (_exp_time == null) {
					throw new RuntimeException("Valid Google Token Error: Parsing 'exp' has error.");
				}
				else if (CommonUtils.getUTCNow().after(_exp_time)) {
					throw new RuntimeException("Valid Google Token Error: The token has expired. [" + CommonUtils.getUTCStringFromDate(_exp_time) + "]");
				}
				else {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return false;
	}

	public static boolean isFacebookTokenValid(String token, String userId) throws Exception {

		String url = String.format("https://graph.facebook.com/debug_token?input_token=%s&access_token=%s", token,
				token);
		try {
			String jsonString = HttpUtils.get(url);
			
			/** 
			 *  Here's an example response:
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
				JsonNode jsonNode_error = actualObj.get("error");
				if (jsonNode_error != null) {
					JsonNode jsonNode_message = jsonNode_error.get("message");
					if (jsonNode_message != null) {
						throw new RuntimeException("Valid FB Token Error: " + jsonNode_message.textValue());
					}
				}
				
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
					
					if (!StringUtils.equals(userId, _userId)) {
						throw new RuntimeException("Valid FB Token Error: " + "The 'user_id' is not matched. [" + userId + "]");
					} 
					else if (_isValid == false) {
						throw new RuntimeException("Valid FB Token Error: " + "The token is invalid.");
					} 
					else {
						return true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return false;
	}

	public static void main(String[] args) throws Exception {
		try {
			String fbToken = "CAAOxNzGZCZCPQBAEAZByeSmHZBlMJLRzZBuZBr1StKlJ8fYl8upL1ZCBhm8ouYy6T81SnZBrRZBFLZCb3Xgt0NQxDOD212Yo34WoMFs6bEUHFQKslR9clweUqEFURwhka84AhOcGwsoHPpgrQGMH2gStSHAfaBHWFVaSyznhnZBJguFalInWPanP4zvI9tLZAPoOGDqspZCO2nFHtTcPgIgsrDXBjxRSd3JLox2QZD";
			String fbUserId = "919778441391523";
			boolean isValidByFacebookToken = AuthUtils.isFacebookTokenValid(fbToken, fbUserId);
	
			System.out.println(isValidByFacebookToken);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			String googleToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6ImQyMTI3Y2Y5ZDc5YjRiODcwOTU2ODAzYmJjNzFmMTc0NmFhM2I4MDAifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhdF9oYXNoIjoicmk2WHN3QXYydWQ0dHhHb0hLcnpHQSIsImF1ZCI6Ijg0NjAxMjYwNTQwNi05dG5yaDgwajhrY2JjbWEyOW9taGxzZWtvdDJtbzBnbS5hcHBzLmdvb2dsZXVzZXJjb250ZW50LmNvbSIsInN1YiI6IjExNjY0Njc2NjI5ODczOTM1ODcyOSIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJhenAiOiI4NDYwMTI2MDU0MDYtOXRucmg4MGo4a2NiY21hMjlvbWhsc2Vrb3QybW8wZ20uYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJlbWFpbCI6ImVlY2hpaEBnbWFpbC5jb20iLCJpYXQiOjE0NTY5MTk2MTAsImV4cCI6MTQ1NjkyMzIxMCwibmFtZSI6IkhhcnJ5IFllaCIsInBpY3R1cmUiOiJodHRwczovL2xoNi5nb29nbGV1c2VyY29udGVudC5jb20vLTJGalpDMGJobEh3L0FBQUFBQUFBQUFJL0FBQUFBQUFBQVBjLzJra1dSYkQ0Wng4L3M5Ni1jL3Bob3RvLmpwZyIsImdpdmVuX25hbWUiOiJIYXJyeSIsImZhbWlseV9uYW1lIjoiWWVoIiwibG9jYWxlIjoiemgtVFcifQ.Bi146z0BqFM9-myb9_Gv-_d05eOHsWCVFF-ilRwF5Lf02rJVDVEXRHe8phk7EDPyRJRn4dWeezZ_MVtsjoGf84S_-RP1oQRV8SmBOMjpCKwurxY4Gc00hEU3TMXWkCsBx6zYag5dfYpYJkEQZCpcBU-cSq3GvvelQDtXqBOvXdmJwYe9xv_EKItL59p162TWpnWWsaKfVcyoovRVNE5iCz54_aP3MJC05UOFwW7AsiC9u_K1PjZY6cEEict_rT4J6AJPZnpvFpYeILwrxf6Tj45zLl0zgbdjb2xCdORKYiJHoNsNOlyMxul5Ik68z6Z-ryt_S9lxWeun2PaQdHfzlQ";
			String googleSub = "116646766298739358729";
			boolean isValidByGoogleToken = AuthUtils.isGoogleTokenValid(googleToken, googleSub);

			System.out.println(isValidByGoogleToken);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
