package com.lap.zuzuweb;

import java.util.Optional;

import com.lap.zuzuweb.dao.UserDao;
import com.lap.zuzuweb.dao.Sql2O.UserDaoBySql2O;
import com.lap.zuzuweb.model.User;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class UserDaoBySql2OTest extends TestCase {
	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public UserDaoBySql2OTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return null;//new TestSuite(AppTest.class);
	}

	/**
	 * Rigourous Test :-)
	 */
	public void testApp() {
		UserDao userDao = new UserDaoBySql2O();
		
		Optional<User> existUser = userDao.getUserById("eechih@gmail.com");
        if (existUser.isPresent()) {
            User user = existUser.get();
            System.out.println(user);
        }
        
		assertTrue(true);
	}
}
