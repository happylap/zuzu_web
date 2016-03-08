package com.lap.zuzuweb;

import java.util.Date;
import java.util.Optional;

import com.lap.zuzuweb.dao.UserDao;
import com.lap.zuzuweb.dao.Sql2O.UserDaoBySql2O;
import com.lap.zuzuweb.model.Criteria;
import com.lap.zuzuweb.model.User;
import com.lap.zuzuweb.util.CommonUtils;

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
		return new TestSuite(AppTest.class);
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
	public void test2() throws Exception {
		
		Date utc1 = CommonUtils.getUTCDateFromString("2016-02-02T10:10:10Z");
		Date utc2 = CommonUtils.getUTCDateFromString("2016-02-02T10:10:09Z");
		
		Criteria criteria = new Criteria();
		//criteria.setExpire_time(utc1);
		
		//assertTrue((criteria.getExpire_time() != null && criteria.getExpire_time().after(utc2)));
		
	}
}
