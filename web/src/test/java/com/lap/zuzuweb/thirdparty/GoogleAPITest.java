package com.lap.zuzuweb.thirdparty;

import org.junit.Test;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

public class GoogleAPITest {

	private static String idTokenString = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjNmYjRhZDJhYTNiY2IxZGY3ZjY5ZTRkYTY2MGU4ZWQ2Mzk2NGE0N2EifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhdF9oYXNoIjoiSldkVmFMcFBrYksyUVJ0Sjd2dEp2USIsImF1ZCI6Ijg0NjAxMjYwNTQwNi05dG5yaDgwajhrY2JjbWEyOW9taGxzZWtvdDJtbzBnbS5hcHBzLmdvb2dsZXVzZXJjb250ZW50LmNvbSIsInN1YiI6IjEwNDU1NDE5MDEzNzI2MDAyMjIwOCIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJhenAiOiI4NDYwMTI2MDU0MDYtOXRucmg4MGo4a2NiY21hMjlvbWhsc2Vrb3QybW8wZ20uYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJlbWFpbCI6Inl5cGFuMjQyMUBnbWFpbC5jb20iLCJpYXQiOjE0NjI1MTczMzUsImV4cCI6MTQ2MjUyMDkzNX0.L46Ii18Xg7zWTgGTTmFmkMZCHcmcGf4ON1D26BRlGVre-CKctLABUU1iMGmzze0QHsaEmcoCw0FUlx41_CJwk_w08ff1EB2onHy_IqIbvx2ksM5xIky6eAdWUsNQdz8xctczHN2CaeYM7YsfaDM62goed2qow__l62KYx_iGIl_pxF6EpJm97-XGfQk1rlN4uoIT92oz15uH1dcsDD4ZnrUfl1uxPT1foZ3AEIA1MdE05Vd1-qY1bgKghjeMWaFNgbybv5hLevZOwfvCi78LRZL1FvYCwlOJuEvdYZRHDAlwoKxzGwCKyaMA3vEZokVFO12L-UYVqWbRzXcjnNGQww";
	
	@Test
	public void test1() throws Exception {
		// Set up the HTTP transport and JSON factory
		HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

		GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(httpTransport, jsonFactory)
				.setIssuer("https://accounts.google.com").build();
		
		GoogleIdToken idToken = verifier.verify(idTokenString);
		
		if (idToken != null) {
			Payload payload = idToken.getPayload();
			System.out.println("email: " + payload.getEmail());
		}
		
	}
}
