package com.lap.zuzuweb.util;

import java.io.FileInputStream;
import java.io.InputStream;

import junit.framework.TestCase;

public class AuthUtilsTest extends TestCase {

	public void testIsPurchaseReceiptValid() throws Exception {
		
		ClassLoader classLoader = getClass().getClassLoader();
		InputStream is = new FileInputStream(classLoader.getResource("AppleReceiptForTest.json").getFile());

		Boolean isValid = AuthUtils.isPurchaseReceiptValid(is);
		
		System.out.println("testIsPurchaseReceiptValid result: " + isValid);
	}

}
