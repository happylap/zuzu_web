package com.lap.zuzuweb.service;

import org.junit.Before;
import org.junit.Test;

import com.lap.zuzuweb.dao.UserDao;
import com.lap.zuzuweb.dao.Sql2O.UserDaoBySql2O;

public class AuthServiceTest {
	
	private AuthService authSvc;
	
	@Before
	public void executedBeforeEach() {
		UserDao userDao = new UserDaoBySql2O();
		authSvc = new AuthServiceImpl(userDao);
	}
	
	
	@Test
	public void testValidateLoginRequestByFBToken() throws Exception {
		try {
			String provider = "FB";
			String accessToken = "EAAOxNzGZCZCPQBAOZBRHxRrauAahHbuObawr27NaOgHBjXADZAfM7IiVjIYrvebzrtORec2Bn3c55Mb5jrICO8HK9rdmxoEIrj48STtp5RZCfsGMLIUIHVyUTSGDZB0ZBGMVLvYcDLOgdLiFa8PdmGtrvCZCiblqQIlRGnnd8zojZBExfjPqeIqADjPi8ZAYzzN7BLO0kfUCDx2QZDZD";
			
			String email = authSvc.validateLoginRequest(provider, accessToken);
			System.out.println("email: " + email);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testValidateLoginRequestByFBToken2() throws Exception {
		try {
			String provider = "FB";
			String accessToken = "EAAOxNzGZCZCPQBAOZBRHxRrauAahHbuObawr27NaOgHBjXADZAfM7IiVjIYrvebzrtORec2Bn3c55Mb5jrICO8HK9rdmxoEIrj48STtp5RZCfsGMLIUIHVyUTSGDZB0ZBGMVLvYcDLOgdLiFa8PdmGtrvCZCiblqQIlRGnnd8zojZBExfjPqeIqADjPi8ZAYzzN7BLO0kfUCDx2QZDZD_aaa";
			
			String email = authSvc.validateLoginRequest(provider, accessToken);
			System.out.println("email: " + email);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testValidateLoginRequestByGOOGLEToken() throws Exception {
		try {
			String provider = "GOOGLE";
			String accessToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjNmYjRhZDJhYTNiY2IxZGY3ZjY5ZTRkYTY2MGU4ZWQ2Mzk2NGE0N2EifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhdF9oYXNoIjoiTDdjbnI4YnpUazYyVjdqblhlNHBpdyIsImF1ZCI6Ijg0NjAxMjYwNTQwNi05dG5yaDgwajhrY2JjbWEyOW9taGxzZWtvdDJtbzBnbS5hcHBzLmdvb2dsZXVzZXJjb250ZW50LmNvbSIsInN1YiI6IjEwNTY0ODAyMDMzNTAwODc2MTU1MSIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJhenAiOiI4NDYwMTI2MDU0MDYtOXRucmg4MGo4a2NiY21hMjlvbWhsc2Vrb3QybW8wZ20uYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJlbWFpbCI6ImxvbmVseXZvbGFuQGdtYWlsLmNvbSIsImlhdCI6MTQ2MjUyNjA5MSwiZXhwIjoxNDYyNTI5NjkxLCJuYW1lIjoi57Kf6ZaL5a6PIiwicGljdHVyZSI6Imh0dHBzOi8vbGg2Lmdvb2dsZXVzZXJjb250ZW50LmNvbS8tclplZGdzc2gxSEUvQUFBQUFBQUFBQUkvQUFBQUFBQUFBQlUva3BOUWYtUmcxRW8vczk2LWMvcGhvdG8uanBnIiwiZ2l2ZW5fbmFtZSI6IumWi-WujyIsImZhbWlseV9uYW1lIjoi57KfIiwibG9jYWxlIjoiemgtVFcifQ.Kc4tmhufne0uskXGASoSzzaNoRCp0em-sZi_zlStT0KHk0K2i2WFXOW0ABdiz8KMwdio1rlqHhWxXcnoGLkuAqzNzEug6VtBqZZsDEWwXzcXrMssa7fF9w8SjIb9jQD_oIinQmZQaPRXrEOFskCyO39mgPcG6BzPF7SwPFvQg0uPwLFYK2NrkGACridbO6xObXqvSGaB6w1lKvi3GCbrOW4-1DE-Y0ExoUBRS_o5y6MDjgUXU152-0b9BMczRV8pSpfFe15ww-OxcLaT_LuVeIVEQSons6khbG9Od3_7S9yL3Fbkw6QcB7MffKnwLeGz5aR4nhAC35-IY-3Nh3x3tQ";
			
			String email = authSvc.validateLoginRequest(provider, accessToken);
			System.out.println("email: " + email);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testValidateLoginRequestByGOOGLEToken2() throws Exception {
		try {
			String provider = "GOOGLE";
			String accessToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjNmYjRhZDJhYTNiY2IxZGY3ZjY5ZTRkYTY2MGU4ZWQ2Mzk2NGE0N2EifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhdF9oYXNoIjoiTDdjbnI4YnpUazYyVjdqblhlNHBpdyIsImF1ZCI6Ijg0NjAxMjYwNTQwNi05dG5yaDgwajhrY2JjbWEyOW9taGxzZWtvdDJtbzBnbS5hcHBzLmdvb2dsZXVzZXJjb250ZW50LmNvbSIsInN1YiI6IjEwNTY0ODAyMDMzNTAwODc2MTU1MSIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJhenAiOiI4NDYwMTI2MDU0MDYtOXRucmg4MGo4a2NiY21hMjlvbWhsc2Vrb3QybW8wZ20uYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJlbWFpbCI6ImxvbmVseXZvbGFuQGdtYWlsLmNvbSIsImlhdCI6MTQ2MjUyNjA5MSwiZXhwIjoxNDYyNTI5NjkxLCJuYW1lIjoi57Kf6ZaL5a6PIiwicGljdHVyZSI6Imh0dHBzOi8vbGg2Lmdvb2dsZXVzZXJjb250ZW50LmNvbS8tclplZGdzc2gxSEUvQUFBQUFBQUFBQUkvQUFBQUFBQUFBQlUva3BOUWYtUmcxRW8vczk2LWMvcGhvdG8uanBnIiwiZ2l2ZW5fbmFtZSI6IumWi-WujyIsImZhbWlseV9uYW1lIjoi57KfIiwibG9jYWxlIjoiemgtVFcifQ.Kc4tmhufne0uskXGASoSzzaNoRCp0em-sZi_zlStT0KHk0K2i2WFXOW0ABdiz8KMwdio1rlqHhWxXcnoGLkuAqzNzEug6VtBqZZsDEWwXzcXrMssa7fF9w8SjIb9jQD_oIinQmZQaPRXrEOFskCyO39mgPcG6BzPF7SwPFvQg0uPwLFYK2NrkGACridbO6xObXqvSGaB6w1lKvi3GCbrOW4-1DE-Y0ExoUBRS_o5y6MDjgUXU152-0b9BMczRV8pSpfFe15ww-OxcLaT_LuVeIVEQSons6khbG9Od3_7S9yL3Fbkw6QcB7MffKnwLeGz5aR4nhAC35-IY-3Nh3x3tQ_aaa";
			
			String email = authSvc.validateLoginRequest(provider, accessToken);
			System.out.println("email: " + email);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
