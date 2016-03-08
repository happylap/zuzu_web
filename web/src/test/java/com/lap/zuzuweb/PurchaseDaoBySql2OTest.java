package com.lap.zuzuweb;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Optional;

import com.lap.zuzuweb.dao.Sql2O.CriteriaDaoBySql2O;
import com.lap.zuzuweb.dao.Sql2O.UserDaoBySql2O;
import com.lap.zuzuweb.model.Criteria;
import com.lap.zuzuweb.model.Purchase;
import com.lap.zuzuweb.model.User;
import com.lap.zuzuweb.util.CommonUtils;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class PurchaseDaoBySql2OTest extends TestCase {
	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public PurchaseDaoBySql2OTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(AppTest.class);
	}

	public void testCreatePurchase() throws Exception {
		File file = new File("/Users/eechih/zuzu.png");
		InputStream fis = new FileInputStream(file);
		
		File file2 = new File("/Users/eechih/zuzu_2.png");
		InputStream fis2 = new FileInputStream(file2);
		
		Purchase purchase = new Purchase();
		purchase.setUser_id("test2");
		purchase.setStore("apple");
		purchase.setProduct_id("radar30");
		purchase.setPurchase_time(CommonUtils.getUTCNow());
		
		Optional<Criteria> existCriteria = new CriteriaDaoBySql2O().getSingleCriteria(purchase.getUser_id());
		Optional<User> existUser = null;// new UserDaoBySql2O().getUser(purchase.getUser_id());
		
		Criteria criteria = null;
		if (existCriteria.isPresent()) {
			criteria = existCriteria.get();
			criteria.setEnabled(true);
			//criteria.setExpire_time(CommonUtils.getUTCNow());
			//criteria.setApple_product_id(purchase.getProduct_id());
		}
		
		User user = existUser.get();
		user.setPurchase_receipt(fis2);
		user.setBirthday(CommonUtils.getUTCNow());
		
		//new PurchaseDaoBySql2O().createPurchase(purchase, user, criteria);

		assertTrue(true);
	}
}
