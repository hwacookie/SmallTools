package de.mbaaba.tool.pw.data;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import junit.framework.Assert;

import org.junit.Test;



public class WorktimeEntryUtilsTest {

	
	@Test
	public void testCalcPlannedBalance() {
		String actual;
		int planned;
		Calendar cal = new GregorianCalendar();
		// make sure we have a non-holiday.
		cal.set(Calendar.DAY_OF_MONTH, 13);
		cal.set(Calendar.MONTH, Calendar.AUGUST);
		cal.set(Calendar.YEAR, 2014);
		Date date = cal.getTime();

		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		long startTime = cal.getTimeInMillis();
	
		
		planned = 120;
		
		cal.set(Calendar.HOUR_OF_DAY, 1);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		actual = WorktimeEntryUtils.calculatePlannedBalance(startTime,  date, planned, cal.getTimeInMillis());
		Assert.assertEquals("-01:00", actual);
		
		cal.set(Calendar.HOUR_OF_DAY, 1);
		cal.set(Calendar.MINUTE, 30);
		cal.set(Calendar.SECOND, 0);
		actual = WorktimeEntryUtils.calculatePlannedBalance(startTime,  date, planned, cal.getTimeInMillis());
		Assert.assertEquals("-00:30", actual);
		
		cal.set(Calendar.HOUR_OF_DAY, 2);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		actual = WorktimeEntryUtils.calculatePlannedBalance(startTime,  date, planned, cal.getTimeInMillis());
		Assert.assertEquals("+00:00", actual);
		
		cal.set(Calendar.HOUR_OF_DAY, 2);
		cal.set(Calendar.MINUTE, 15);
		cal.set(Calendar.SECOND, 0);
		actual = WorktimeEntryUtils.calculatePlannedBalance(startTime,  date, planned, cal.getTimeInMillis());
		Assert.assertEquals("+00:00", actual);
		
		cal.set(Calendar.HOUR_OF_DAY, 2);
		cal.set(Calendar.MINUTE, 16);
		cal.set(Calendar.SECOND, 0);
		actual = WorktimeEntryUtils.calculatePlannedBalance(startTime,  date, planned, cal.getTimeInMillis());
		Assert.assertEquals("+00:01", actual);
		
		cal.set(Calendar.HOUR_OF_DAY, 3);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		actual = WorktimeEntryUtils.calculatePlannedBalance(startTime,  date, planned, cal.getTimeInMillis());
		Assert.assertEquals("+00:45", actual);
		

		cal.set(Calendar.HOUR_OF_DAY, 6);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		actual = WorktimeEntryUtils.calculatePlannedBalance(startTime,  date, planned, cal.getTimeInMillis());
		Assert.assertEquals("+03:45", actual);
		
		cal.set(Calendar.HOUR_OF_DAY, 8);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		actual = WorktimeEntryUtils.calculatePlannedBalance(startTime,  date, planned, cal.getTimeInMillis());
		Assert.assertEquals("+05:15", actual);
		
		
		// now, some tests with a planned time of 5 hours
		
		planned = 5 * 60;
		
		cal.set(Calendar.HOUR_OF_DAY, 1);
		cal.set(Calendar.MINUTE, 30);
		cal.set(Calendar.SECOND, 0);
		actual = WorktimeEntryUtils.calculatePlannedBalance(startTime,  date, planned, cal.getTimeInMillis());
		Assert.assertEquals("-03:45", actual);
		
		cal.set(Calendar.HOUR_OF_DAY, 5);
		cal.set(Calendar.MINUTE, 00);
		cal.set(Calendar.SECOND, 0);
		actual = WorktimeEntryUtils.calculatePlannedBalance(startTime,  date, planned, cal.getTimeInMillis());
		Assert.assertEquals("-00:15", actual);
		
		cal.set(Calendar.HOUR_OF_DAY, 5);
		cal.set(Calendar.MINUTE, 15);
		cal.set(Calendar.SECOND, 0);
		actual = WorktimeEntryUtils.calculatePlannedBalance(startTime,  date, planned, cal.getTimeInMillis());
		Assert.assertEquals("+00:00", actual);
		
		cal.set(Calendar.HOUR_OF_DAY, 5);
		cal.set(Calendar.MINUTE, 20);
		cal.set(Calendar.SECOND, 0);
		actual = WorktimeEntryUtils.calculatePlannedBalance(startTime,  date, planned, cal.getTimeInMillis());
		Assert.assertEquals("+00:05", actual);

		// now, test the long break!
		cal.set(Calendar.HOUR_OF_DAY, 6);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		actual = WorktimeEntryUtils.calculatePlannedBalance(startTime,  date, planned, cal.getTimeInMillis());
		Assert.assertEquals("+00:45", actual);

		
		cal.set(Calendar.HOUR_OF_DAY, 6);
		cal.set(Calendar.MINUTE, 15);
		cal.set(Calendar.SECOND, 0);
		actual = WorktimeEntryUtils.calculatePlannedBalance(startTime,  date, planned, cal.getTimeInMillis());
		Assert.assertEquals("+01:00", actual);			
		
		cal.set(Calendar.HOUR_OF_DAY, 6);
		cal.set(Calendar.MINUTE, 20);
		cal.set(Calendar.SECOND, 0);
		actual = WorktimeEntryUtils.calculatePlannedBalance(startTime,  date, planned, cal.getTimeInMillis());
		Assert.assertEquals("+01:00", actual);			
		
		
		// now test a very long workday
		planned = 9 * 60;
		cal.set(Calendar.HOUR_OF_DAY, 2);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		actual = WorktimeEntryUtils.calculatePlannedBalance(startTime,  date, planned, cal.getTimeInMillis());
		Assert.assertEquals("-07:45", actual);

		// check if small break added
		cal.set(Calendar.HOUR_OF_DAY, 3);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		actual = WorktimeEntryUtils.calculatePlannedBalance(startTime,  date, planned, cal.getTimeInMillis());
		Assert.assertEquals("-06:45", actual);

		// check if long break added
		cal.set(Calendar.HOUR_OF_DAY, 9);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		actual = WorktimeEntryUtils.calculatePlannedBalance(startTime,  date, planned, cal.getTimeInMillis());
		Assert.assertEquals("-00:45", actual);

		cal.set(Calendar.HOUR_OF_DAY, 9);
		cal.set(Calendar.MINUTE, 30);
		cal.set(Calendar.SECOND, 0);
		actual = WorktimeEntryUtils.calculatePlannedBalance(startTime,  date, planned, cal.getTimeInMillis());
		Assert.assertEquals("-00:15", actual);

		cal.set(Calendar.HOUR_OF_DAY, 9);
		cal.set(Calendar.MINUTE, 45);
		cal.set(Calendar.SECOND, 0);
		actual = WorktimeEntryUtils.calculatePlannedBalance(startTime,  date, planned, cal.getTimeInMillis());
		Assert.assertEquals("+00:00", actual);

		
	}
	
	
	@Test
	public void testBreakCalcInMinutes() {
		WorktimeEntry we = new WorktimeEntry();
		we.setDate(new Date());
		Calendar cal = new GregorianCalendar();
		cal.set(Calendar.HOUR, 8);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		we.setStartTime(cal.getTime());

		cal = new GregorianCalendar();
		cal.set(Calendar.HOUR, 9);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		we.setEndTime(cal.getTime());
		
		int breaktimeInMinutes = WorktimeEntryUtils.getBreaktimeMinutes(we);
		Assert.assertEquals(0,  breaktimeInMinutes);
		
		
		// test 1 minutes
		cal = new GregorianCalendar();
		cal.set(Calendar.HOUR, 10);
		cal.set(Calendar.MINUTE, 1);
		cal.set(Calendar.SECOND, 0);
		we.setEndTime(cal.getTime());

		breaktimeInMinutes = WorktimeEntryUtils.getBreaktimeMinutes(we);
		Assert.assertEquals(1,  breaktimeInMinutes);
		
		// test 14 minutes
		cal = new GregorianCalendar();
		cal.set(Calendar.HOUR, 10);
		cal.set(Calendar.MINUTE, 14);
		cal.set(Calendar.SECOND, 0);
		we.setEndTime(cal.getTime());

		breaktimeInMinutes = WorktimeEntryUtils.getBreaktimeMinutes(we);
		Assert.assertEquals(14,  breaktimeInMinutes);
		
		// test 15 minutes
		cal = new GregorianCalendar();
		cal.set(Calendar.HOUR, 10);
		cal.set(Calendar.MINUTE, 15);
		cal.set(Calendar.SECOND, 0);
		we.setEndTime(cal.getTime());

		breaktimeInMinutes = WorktimeEntryUtils.getBreaktimeMinutes(we);
		Assert.assertEquals(15,  breaktimeInMinutes);
		
		// test >02:15
		cal = new GregorianCalendar();
		cal.set(Calendar.HOUR, 10);
		cal.set(Calendar.MINUTE, 16);
		cal.set(Calendar.SECOND, 0);
		we.setEndTime(cal.getTime());

		breaktimeInMinutes = WorktimeEntryUtils.getBreaktimeMinutes(we);
		Assert.assertEquals(15,  breaktimeInMinutes);
		
		// test 06:00 worktime
		cal = new GregorianCalendar();
		cal.set(Calendar.HOUR, 14);
		cal.set(Calendar.MINUTE, 00);
		cal.set(Calendar.SECOND, 0);
		we.setEndTime(cal.getTime());

		breaktimeInMinutes = WorktimeEntryUtils.getBreaktimeMinutes(we);
		Assert.assertEquals(15,  breaktimeInMinutes);
		
		// test 06:15 worktime
		cal = new GregorianCalendar();
		cal.set(Calendar.HOUR, 14);
		cal.set(Calendar.MINUTE, 15);
		cal.set(Calendar.SECOND, 0);
		we.setEndTime(cal.getTime());

		breaktimeInMinutes = WorktimeEntryUtils.getBreaktimeMinutes(we);
		Assert.assertEquals(15,  breaktimeInMinutes);
		
		// test 06:16 worktime
		cal = new GregorianCalendar();
		cal.set(Calendar.HOUR, 14);
		cal.set(Calendar.MINUTE, 16);
		cal.set(Calendar.SECOND, 0);
		we.setEndTime(cal.getTime());

		breaktimeInMinutes = WorktimeEntryUtils.getBreaktimeMinutes(we);
		Assert.assertEquals(16,  breaktimeInMinutes);
		
		
		// test 06:45 worktime
		cal = new GregorianCalendar();
		cal.set(Calendar.HOUR, 14);
		cal.set(Calendar.MINUTE, 45);
		cal.set(Calendar.SECOND, 0);
		we.setEndTime(cal.getTime());

		breaktimeInMinutes = WorktimeEntryUtils.getBreaktimeMinutes(we);
		Assert.assertEquals(45,  breaktimeInMinutes);

		// test 06:46 worktime
		cal = new GregorianCalendar();
		cal.set(Calendar.HOUR, 14);
		cal.set(Calendar.MINUTE, 46);
		cal.set(Calendar.SECOND, 0);
		we.setEndTime(cal.getTime());

		breaktimeInMinutes = WorktimeEntryUtils.getBreaktimeMinutes(we);
		Assert.assertEquals(45,  breaktimeInMinutes);
	}
	
	
	@Test
	public void testNetWorkCalcInMinutes() {
		WorktimeEntry we = new WorktimeEntry();
		we.setDate(new Date());
		Calendar cal = new GregorianCalendar();
		cal.set(Calendar.HOUR, 8);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		we.setStartTime(cal.getTime());

		cal = new GregorianCalendar();
		cal.set(Calendar.HOUR, 9);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		we.setEndTime(cal.getTime());
		
		int worktimeInMinutes = WorktimeEntryUtils.getNetWorktimeInMinutes(we);
		Assert.assertEquals(119,  worktimeInMinutes);
		
		
		// test 1 minutes
		cal = new GregorianCalendar();
		cal.set(Calendar.HOUR, 10);
		cal.set(Calendar.MINUTE, 1);
		cal.set(Calendar.SECOND, 0);
		we.setEndTime(cal.getTime());

		worktimeInMinutes = WorktimeEntryUtils.getNetWorktimeInMinutes(we);
		Assert.assertEquals(120,  worktimeInMinutes);
		
		// test 14 minutes
		cal = new GregorianCalendar();
		cal.set(Calendar.HOUR, 10);
		cal.set(Calendar.MINUTE, 14);
		cal.set(Calendar.SECOND, 0);
		we.setEndTime(cal.getTime());

		worktimeInMinutes = WorktimeEntryUtils.getNetWorktimeInMinutes(we);
		Assert.assertEquals(120,  worktimeInMinutes);
		
		// test 15 minutes
		cal = new GregorianCalendar();
		cal.set(Calendar.HOUR, 10);
		cal.set(Calendar.MINUTE, 15);
		cal.set(Calendar.SECOND, 0);
		we.setEndTime(cal.getTime());

		worktimeInMinutes = WorktimeEntryUtils.getNetWorktimeInMinutes(we);
		Assert.assertEquals(120,  worktimeInMinutes);
		
		// test >02:15
		cal = new GregorianCalendar();
		cal.set(Calendar.HOUR, 10);
		cal.set(Calendar.MINUTE, 16);
		cal.set(Calendar.SECOND, 0);
		we.setEndTime(cal.getTime());

		worktimeInMinutes = WorktimeEntryUtils.getNetWorktimeInMinutes(we);
		Assert.assertEquals(121,  worktimeInMinutes);
		
		// test 06:00 worktime
		cal = new GregorianCalendar();
		cal.set(Calendar.HOUR, 14);
		cal.set(Calendar.MINUTE, 00);
		cal.set(Calendar.SECOND, 0);
		we.setEndTime(cal.getTime());

		worktimeInMinutes = WorktimeEntryUtils.getNetWorktimeInMinutes(we);
		Assert.assertEquals(6*60-15,  worktimeInMinutes);

		
		// test 06:15 worktime
		cal = new GregorianCalendar();
		cal.set(Calendar.HOUR, 14);
		cal.set(Calendar.MINUTE, 15);
		cal.set(Calendar.SECOND, 0);
		we.setEndTime(cal.getTime());

		worktimeInMinutes = WorktimeEntryUtils.getNetWorktimeInMinutes(we);
		Assert.assertEquals(6*60,  worktimeInMinutes);
		
		// test 06:16 worktime
		cal = new GregorianCalendar();
		cal.set(Calendar.HOUR, 14);
		cal.set(Calendar.MINUTE, 16);
		cal.set(Calendar.SECOND, 0);
		we.setEndTime(cal.getTime());

		worktimeInMinutes = WorktimeEntryUtils.getNetWorktimeInMinutes(we);
		Assert.assertEquals(6*60,  worktimeInMinutes);
		
		
		// test 06:45 worktime
		cal = new GregorianCalendar();
		cal.set(Calendar.HOUR, 14);
		cal.set(Calendar.MINUTE, 45);
		cal.set(Calendar.SECOND, 0);
		we.setEndTime(cal.getTime());

		worktimeInMinutes = WorktimeEntryUtils.getNetWorktimeInMinutes(we);
		Assert.assertEquals(6*60,  worktimeInMinutes);

		// test 06:46 worktime
		cal = new GregorianCalendar();
		cal.set(Calendar.HOUR, 14);
		cal.set(Calendar.MINUTE, 46);
		cal.set(Calendar.SECOND, 0);
		we.setEndTime(cal.getTime());

		worktimeInMinutes = WorktimeEntryUtils.getNetWorktimeInMinutes(we);
		Assert.assertEquals(6*60+1,  worktimeInMinutes);
	}
	
	@Test
	public void testFormat() {
		int time = -10;
		String formatMinutes = WorktimeEntryUtils.formatMinutes(time);
		Assert.assertEquals("-00:10", formatMinutes);

		time = -61;
		formatMinutes = WorktimeEntryUtils.formatMinutes(time);
		Assert.assertEquals("-01:01", formatMinutes);

		time = 0;
		formatMinutes = WorktimeEntryUtils.formatMinutes(time);
		Assert.assertEquals("+00:00", formatMinutes);

		time = 1;
		formatMinutes = WorktimeEntryUtils.formatMinutes(time);
		Assert.assertEquals("+00:01", formatMinutes);

		time = 9;
		formatMinutes = WorktimeEntryUtils.formatMinutes(time);
		Assert.assertEquals("+00:09", formatMinutes);

		time = 11;
		formatMinutes = WorktimeEntryUtils.formatMinutes(time);
		Assert.assertEquals("+00:11", formatMinutes);

		time = 59;
		formatMinutes = WorktimeEntryUtils.formatMinutes(time);
		Assert.assertEquals("+00:59", formatMinutes);

		time = 60;
		formatMinutes = WorktimeEntryUtils.formatMinutes(time);
		Assert.assertEquals("+01:00", formatMinutes);

		time = 61;
		formatMinutes = WorktimeEntryUtils.formatMinutes(time);
		Assert.assertEquals("+01:01", formatMinutes);
	}
}
