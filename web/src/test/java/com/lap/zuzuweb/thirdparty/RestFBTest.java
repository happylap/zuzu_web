package com.lap.zuzuweb.thirdparty;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.lap.zuzuweb.FacebookTokenManagement;
import com.lap.zuzuweb.GoogleTokenManagement;
import com.lap.zuzuweb.Secrets;
import com.lap.zuzuweb.common.Provider;
import com.lap.zuzuweb.dao.Sql2O.UserDaoBySql2O;
import com.lap.zuzuweb.exception.UnauthorizedException;
import com.lap.zuzuweb.util.CommonUtils;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.FacebookClient.AccessToken;
import com.restfb.FacebookClient.DebugTokenInfo;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.types.User;

public class RestFBTest {
	private static String FBToken = "EAAOxNzGZCZCPQBAOZBRHxRrauAahHbuObawr27NaOgHBjXADZAfM7IiVjIYrvebzrtORec2Bn3c55Mb5jrICO8HK9rdmxoEIrj48STtp5RZCfsGMLIUIHVyUTSGDZB0ZBGMVLvYcDLOgdLiFa8PdmGtrvCZCiblqQIlRGnnd8zojZBExfjPqeIqADjPi8ZAYzzN7BLO0kfUCDx2QZDZD";
	private static String GoogleToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjNmYjRhZDJhYTNiY2IxZGY3ZjY5ZTRkYTY2MGU4ZWQ2Mzk2NGE0N2EifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhdF9oYXNoIjoiRFR3R2lFV0lEYm1VOFFqYW5HYlpMUSIsImF1ZCI6Ijg0NjAxMjYwNTQwNi05dG5yaDgwajhrY2JjbWEyOW9taGxzZWtvdDJtbzBnbS5hcHBzLmdvb2dsZXVzZXJjb250ZW50LmNvbSIsInN1YiI6IjEwNDU1NDE5MDEzNzI2MDAyMjIwOCIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJhenAiOiI4NDYwMTI2MDU0MDYtOXRucmg4MGo4a2NiY21hMjlvbWhsc2Vrb3QybW8wZ20uYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJlbWFpbCI6Inl5cGFuMjQyMUBnbWFpbC5jb20iLCJpYXQiOjE0NjI1MjEzNjAsImV4cCI6MTQ2MjUyNDk2MCwibmFtZSI6Iua9mOaAoeWQmyIsInBpY3R1cmUiOiJodHRwczovL2xoNC5nb29nbGV1c2VyY29udGVudC5jb20vLVNyLTVHSXp4bjhvL0FBQUFBQUFBQUFJL0FBQUFBQUFBRkxzL2N5MmlnLTVrV2U0L3M5Ni1jL3Bob3RvLmpwZyIsImdpdmVuX25hbWUiOiLmgKHlkJsiLCJmYW1pbHlfbmFtZSI6Iua9mCIsImxvY2FsZSI6InpoLVRXIn0.kRGc7SqjPnzvvQBpwCCL-MdB4hqaoJSDiHihB7G2zDekBjEJmE1-v917MDqwSjOWvnvm6unwsSy4lMtM5H1oA6QX_fPzSe-V_PZeyib_0gZEQDx0HuRE__3cFx_KgimoN29CQ6WZMbt6vthSXGejU1QZT3Cj-0Y8VPF66oWxOksZjC6ZCytWyXbSX-A4FirYIn4azkCRIudpOK_WaGIAFZkSS4xb8ylSAv5R3e5jbASou6c5C-IFMUCMFG-U8QxaAeZ3h5UjcwVl9EHcS62bsHhOgu4dmWPfk7Au1kHI1iXgUBurU4Kv6yqqfxRoojux-K80vJVRjx0rjAmX5H0pog";

	private static String getAppAccessToken() {
		AccessToken accessToken = new DefaultFacebookClient(Version.LATEST)
				.obtainAppAccessToken(Secrets.FACEBOOK_APP_ID, Secrets.FACEBOOK_APP_SECRET);
		System.out.println(accessToken);
		return accessToken.getAccessToken();
	}

	@Test
	public void testObtainAppAccessToken() {
		assertTrue(StringUtils.startsWith(getAppAccessToken(), "1039275546115316"));
	}

	@Test
	public void testDebugToken() {

		FacebookClient facebookClient = new DefaultFacebookClient(getAppAccessToken(), Version.LATEST);
		DebugTokenInfo info = facebookClient.debugToken(FBToken);
		System.out.println(info.isValid());
		if (info.isValid()) {
			System.out.println(CommonUtils.getUTCStringFromDate(info.getExpiresAt()));
		}

	}

	@Test
	public void testGetEmailByAccessToken() {

		try {
			FacebookClient facebookClient = new DefaultFacebookClient("_" + FBToken, Version.LATEST);

			// fetch user type with id, name and email prefilled
			User user = facebookClient.fetchObject("me", User.class, Parameter.with("fields", "id, name, email"));
			System.out.println(user.getId());
			System.out.println(user.getName());
			System.out.println(user.getEmail());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testFBLoginSuccess() throws Exception {
		com.lap.zuzuweb.model.User loginUser = login("FB", FBToken);
		assertNotNull(loginUser);
		assertEquals("f4798469-92b3-48f8-8a7c-53636f27d14c", loginUser.getUser_id());
	}

	@Test
	public void testFBLoginFailure() throws Exception {
		com.lap.zuzuweb.model.User loginUser = login("FB", FBToken + "__");
		assertNull(loginUser);
	}

	@Test
	public void testGoogleLoginSuccess() throws Exception {
		com.lap.zuzuweb.model.User loginUser = login("GOOGLE", GoogleToken);
		assertNotNull(loginUser);
		assertEquals("f4798469-92b3-48f8-8a7c-53636f27d14c", loginUser.getUser_id());
	}

	@Test
	public void testGoogleLoginFailure() throws Exception {
		com.lap.zuzuweb.model.User loginUser = login("GOOGLE", GoogleToken + "__");
		assertNull(loginUser);
	}

	private com.lap.zuzuweb.model.User login(String provider, String accessToken) throws Exception {
		if (StringUtils.isBlank(provider) || StringUtils.isBlank(accessToken)) {
			throw new Exception("Required field");
		}

		String email = null;

		if (StringUtils.equals(Provider.FB.toString(), provider)) {
			// boolean isTokenValid = false;
			// FacebookClient facebookClient = new
			// DefaultFacebookClient(getAppAccessToken(), Version.LATEST);
			// DebugTokenInfo info = facebookClient.debugToken(accessToken);
			// isTokenValid = info.isValid();
			//
			// if (isTokenValid) {
			// facebookClient = new DefaultFacebookClient(accessToken ,
			// Version.LATEST);
			// // fetch user type with id, name and email prefilled
			// User loginUser = facebookClient.fetchObject("me", User.class,
			// Parameter.with("fields", "id, name, email"));
			// email = loginUser.getEmail();
			// }

			FacebookTokenManagement ftm = new FacebookTokenManagement();
			email = ftm.getEmailByToken(accessToken);
			
		} else if (StringUtils.equals(Provider.GOOGLE.toString(), provider)) {
			/*
			// Set up the HTTP transport and JSON factory
			HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

			GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(httpTransport, jsonFactory)
					.setIssuer("https://accounts.google.com").build();

			GoogleIdToken idToken = verifier.verify(GoogleToken);

			if (idToken != null) {
				Payload payload = idToken.getPayload();
				System.out.println("email: " + payload.getEmail());
				email = payload.getEmail();
			}
			*/
			
			GoogleTokenManagement gtm = new GoogleTokenManagement();
			email = gtm.getEmailByToken(accessToken);
		}
		
		System.out.println("email: "+ email);

		if (StringUtils.isNotBlank(email)) {
			Optional<com.lap.zuzuweb.model.User> existUser = (new UserDaoBySql2O()).getUserByEmail(email);
			if (!existUser.isPresent()) {
				throw new UnauthorizedException("Couldn't find user by email: " + email);
			}

			return existUser.get();
		}

		return null;
	}

}
