package de.mbaaba.tool.pw.data;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import junit.framework.Assert;

import org.junit.Test;



public class WorktimeEntryUtilsTest {

	
	
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
		int time = 0;
		String formatMinutes = WorktimeEntryUtils.formatMinutes(time);
		Assert.assertEquals("0:00", formatMinutes);

		time = 1;
		formatMinutes = WorktimeEntryUtils.formatMinutes(time);
		Assert.assertEquals("0:01", formatMinutes);

		time = 9;
		formatMinutes = WorktimeEntryUtils.formatMinutes(time);
		Assert.assertEquals("0:09", formatMinutes);

		time = 11;
		formatMinutes = WorktimeEntryUtils.formatMinutes(time);
		Assert.assertEquals("0:11", formatMinutes);

		time = 59;
		formatMinutes = WorktimeEntryUtils.formatMinutes(time);
		Assert.assertEquals("0:59", formatMinutes);

		time = 60;
		formatMinutes = WorktimeEntryUtils.formatMinutes(time);
		Assert.assertEquals("1:00", formatMinutes);

		time = 61;
		formatMinutes = WorktimeEntryUtils.formatMinutes(time);
		Assert.assertEquals("1:01", formatMinutes);
	}
}
