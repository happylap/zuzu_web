package com.lap.zuzuweb;

import java.util.Calendar;
import java.util.Date;

import com.lap.zuzuweb.model.Criteria;
import com.lap.zuzuweb.util.CommonUtils;

import junit.framework.TestCase;

public class CriteriaTest extends TestCase {
			
	public void testSetProductAndCalExpireTime_radar30() {
		Criteria criteria = new Criteria();
		this.setProductAndCalExpireTime(criteria, "radar30", 30);
	}
	
	public void testSetProductAndCalExpireTime_radar60() {
		Criteria criteria = new Criteria();
		this.setProductAndCalExpireTime(criteria, "radar60", 75);
	}
	
	public void testSetProductAndCalExpireTime_radar90() {
		Criteria criteria = new Criteria();
		this.setProductAndCalExpireTime(criteria, "radar90", 120);
	}
	
	public void testSetProductAndCalExpireTime_radar30_assignExpireTime() {
		Date now = CommonUtils.getUTCNow();
		Calendar c = Calendar.getInstance();
		c.setTime(now);
		c.add(Calendar.DATE, 7);
		Date dateOf10dyasAgo = c.getTime();
				
		Criteria criteria = new Criteria();
		//criteria.setExpire_time(dateOf10dyasAgo);
		
		this.setProductAndCalExpireTime(criteria, "radar30", 37);
	}
	
	private void setProductAndCalExpireTime(Criteria criteria, String productId, int validDays) {

		Date now = CommonUtils.getUTCNow();
		
		//criteria.setProductAndCalExpireTime(productId);

		System.out.println("now: " + CommonUtils.getUTCStringFromDate(now));

		//System.out.println("expireTime: " + CommonUtils.getUTCStringFromDate(criteria.getExpire_time()));
		
		// Get msec from each, and subtract.
	    //long diff = criteria.getExpire_time().getTime() - now.getTime();
	    //long diffDays = diff / (1000 * 60 * 60 * 24);
	    
	    //System.out.println("Difference between now and expire_time is " + diffDays + " days.");
	    
	    //assertEquals(diffDays, validDays);
	}
	
}
