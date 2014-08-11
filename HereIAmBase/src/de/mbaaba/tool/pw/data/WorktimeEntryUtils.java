package de.mbaaba.tool.pw.data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.jollyday.HolidayCalendar;
import de.jollyday.HolidayManager;
import de.mbaaba.util.Units;

public class WorktimeEntryUtils {
	
	private static HolidayManager holidayManager = HolidayManager.getInstance(HolidayCalendar.GERMANY);


	public static final DateFormat DATE_ONLY = new SimpleDateFormat(
			"dd.MM.yyyy (E)");
	public static final DateFormat TIME_ONLY = new SimpleDateFormat("HH:mm");	
	
	
	private static final long SHORT_BREAK_LENGTH = 15;
	private static final long SHORT_BRAKE_START = 60 * 2;
	private static final long LONG_BREAK_START = 60 * 6 + SHORT_BREAK_LENGTH;
	private static final long LONG_BREAK_LENGTH = 30;

	public static int getNetWorktimeInMinutes(WorktimeEntry we) {
		if ((((we.getStartTime() == null) || we.getEndTime() == null))
				|| (we.getEndTime().before(we.getStartTime()))) {
			return 0;
		}
		int breaktime = getBreaktimeMinutes(we);

		long workTime = we.getEndTime().getTime() - we.getStartTime().getTime();
		workTime = workTime / Units.MINUTE;

		return (int) workTime - breaktime;
	}

	public static int getBreaktimeMinutes(WorktimeEntry we) {
		if ((((we.getStartTime() == null) || we.getEndTime() == null))
				|| (we.getEndTime().before(we.getStartTime()))) {
			return 0;
		}
		long workTime = we.getEndTime().getTime() - we.getStartTime().getTime();
		workTime = workTime / Units.MINUTE;
		long breaktime = 0;
		// draw off 15 minutes break after 2 hours
		if (workTime > SHORT_BRAKE_START) {
			if (workTime < SHORT_BRAKE_START + SHORT_BREAK_LENGTH) {
				breaktime = workTime - SHORT_BRAKE_START;
			} else {
				breaktime = SHORT_BREAK_LENGTH;
			}
		}

		if (workTime > LONG_BREAK_START) {
			if (workTime < LONG_BREAK_START + LONG_BREAK_LENGTH) {
				breaktime = workTime - LONG_BREAK_START + SHORT_BREAK_LENGTH;
			} else {
				breaktime = LONG_BREAK_LENGTH + SHORT_BREAK_LENGTH;
			}
		}
		return (int) breaktime;
	}

	public static String formatMinutes(int aMinutes) {
		StringBuilder res = new StringBuilder();
		if (aMinutes < 0) {
			res.append("-");
			aMinutes = -aMinutes;
		}
		int hours = aMinutes / 60;
		int minutes = aMinutes % 60;
		res.append(hours);
		res.append(":");
		if (minutes < 10) {
			res.append("0");
		}
		res.append(minutes);
		return res.toString();
	}

	public static boolean isHoliday(Date date) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		if ((cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY) || (cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY)) {
			return true;
		}
		return holidayManager.isHoliday(cal);
	}

}
