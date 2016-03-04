package com.lap.zuzuweb.util;

import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

import junit.framework.TestCase;

public class HttpUtilsTest extends TestCase {

	public void testPost() throws Exception {

		String url_sandbox = "https://sandbox.itunes.apple.com/verifyReceipt";

		ClassLoader classLoader = getClass().getClassLoader();
		InputStream is = new FileInputStream(classLoader.getResource("com/lap/zuzuweb/util/receipt.json").getFile());
		String receiptString = IOUtils.toString(is);

		StringEntity se = new StringEntity(receiptString);
		se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

		String result = HttpUtils.post(url_sandbox, se);
		System.out.println("result: " + result);

	}
}
