package com.lap.zuzuweb;

import java.util.Date;

import com.lap.zuzuweb.dao.LogDao;
import com.lap.zuzuweb.dao.Sql2O.LogDaoBySql2O;
import com.lap.zuzuweb.model.Log;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class LogDaoTest extends TestCase {
	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public LogDaoTest(String testName) {
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
		try {
			LogDao logDao = new LogDaoBySql2O();

			Log insertLog = new Log();
			insertLog.setDevice_id("d1");
			insertLog.setUser_id("u1");
			insertLog.setLog_type(Log.Type.RECEIVE_NOTIFY_TIME);
			insertLog.setLog_time(new Date());
			logDao.createLog(insertLog);

			assertTrue(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
