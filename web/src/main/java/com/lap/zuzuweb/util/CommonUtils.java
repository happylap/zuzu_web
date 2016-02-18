package com.lap.zuzuweb.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class CommonUtils {

	public static Date getUTCNow() {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		return cal.getTime();
	}
	
	//
	public static Date getUTCDateFromString(String utcTime) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		return sdf.parse(utcTime);
	}
	
	public static String getUTCStringFromDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		return sdf.format(date);
	}
}
