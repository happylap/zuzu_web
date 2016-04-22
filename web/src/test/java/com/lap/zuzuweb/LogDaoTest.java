package com.lap.zuzuweb;

import java.util.Date;

import com.lap.zuzuweb.dao.LogDao;
import com.lap.zuzuweb.dao.Sql2O.LogDaoBySql2O;
import com.lap.zuzuweb.model.Log;

import junit.framework.TestCase;

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
	 * Rigourous Test :-)
	 */
	public void testApp() {
		try {
			LogDao logDao = new LogDaoBySql2O();

			Log insertLog = new Log();
			insertLog.setDevice_id("d1");
			insertLog.setUser_id("u1");
			insertLog.setLog_type(Log.Type.RECEIVE_NOTIFY_TIME.toString());
			insertLog.setLog_time(new Date());
			logDao.createLog(insertLog);

			assertTrue(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
