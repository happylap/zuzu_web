package com.lap.zuzuweb.service;

import org.junit.Test;

public class AuthServiceTest {

	AuthService service = new AuthServiceImpl();
	
	@Test
	public void testIsFacebookTokenValid() {
		
		try {
			String token = "__CAAOxNzGZCZCPQBAOHewIcsf6umcZBNZBpGPRtpavOhp6LMsTT39ZBh2pZCZC7SX66FzDTPQGeorE9xqtHSC9whfcAQ6ZCTVnrXbiPDjLGUZBE2Ic8lAxfracuqxa6SZB5KHc6sWYQzM84KCKDDDWzXB292uRGZBtQxwzysjJUfk6wzHqQIyvAgjIuOWngUqHLWTqQ2DOTprSZCG1AEZB2BNTPbLFZAdYPAcPFWmrEg59lAos6cNQZDZD";
			boolean valid = service.isFacebookTokenValid(token);
			System.out.println("valid = " + valid);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}