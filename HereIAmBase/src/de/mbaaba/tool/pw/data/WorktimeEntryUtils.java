package de.mbaaba.tool.pw.data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;

import com.rits.cloning.Cloner;

import de.jollyday.Holiday;
import de.jollyday.HolidayCalendar;
import de.jollyday.HolidayManager;
import de.mbaaba.util.Units;

public class WorktimeEntryUtils {

	private static Cloner cloner = new Cloner();

	private static HolidayManager holidayManager = HolidayManager
			.getInstance(HolidayCalendar.GERMANY);

	public static final DateFormat DATE_ONLY = new SimpleDateFormat(
			"dd.MM.yyyy (E)");
	public static final DateFormat TIME_ONLY = new SimpleDateFormat("HH:mm");

	public static final int SHORT_BREAK_LENGTH = 15;
	public static final int SHORT_BRAKE_START = 60 * 4;
	public static final int LONG_BREAK_START = 60 * 6 + SHORT_BREAK_LENGTH;
	public static final int LONG_BREAK_LENGTH = 30;

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
		return getBreaktimeMinutes(we.getDate(), (int) workTime);
	}

	public static int getBreaktimeMinutes(Date aDate, int aWorktime) {
		// no breaks on holidays
		if (isHoliday(aDate)) {
			return 0;
		}

		long breaktime = 0;
		// draw off 15 minutes break after 2 hours
		if (aWorktime > SHORT_BRAKE_START) {
			if (aWorktime < SHORT_BRAKE_START + SHORT_BREAK_LENGTH) {
				breaktime = aWorktime - SHORT_BRAKE_START;
			} else {
				breaktime = SHORT_BREAK_LENGTH;
			}
		}

		if (aWorktime > LONG_BREAK_START) {
			if (aWorktime < LONG_BREAK_START + LONG_BREAK_LENGTH) {
				breaktime = aWorktime - LONG_BREAK_START + SHORT_BREAK_LENGTH;
			} else {
				breaktime = LONG_BREAK_LENGTH + SHORT_BREAK_LENGTH;
			}
		}
		return (int) breaktime;
	}

	public static String calculateBalanceString(WorktimeEntry we) {
		int time = WorktimeEntryUtils.getNetWorktimeInMinutes(we);
		if (time > 0) {
			return WorktimeEntryUtils.formatMinutes(time - we.getPlanned());
		} else {
			return "";
		}
	}

	public static String calculatePlannedBalance(long aStart, Date aDate,
			int aPlanned, long aNow) {
		int balance;
		// calculate the time-difference between now and the start time (in
		// minutes)
		int actualWorkTime = (int) ((aNow - aStart) / Units.MINUTE);

		// from that time, subtract the break we need to make for that workTime
		actualWorkTime = actualWorkTime
				- getBreaktimeMinutes(aDate, actualWorkTime);

		// what was the planned workTime?
		int plannedWorkTime = aPlanned;

		// is the current worktime already longer than the planned?
		if (actualWorkTime > plannedWorkTime) {
			// yes, then the balance is positive for that day.
			balance = actualWorkTime - plannedWorkTime;
		} else {
			// no, we have not reached the planned time yet!
			// add the breaktime back again (it is already contained in the
			// breaktime for the planned time, see below!)
			actualWorkTime = actualWorkTime
					+ getBreaktimeMinutes(aDate, actualWorkTime);

			int breakTime = getBreaktimeMinutes(aDate, plannedWorkTime);
			plannedWorkTime += breakTime;

			balance = actualWorkTime - plannedWorkTime;
		}

		return WorktimeEntryUtils.formatMinutes(balance);

	}

	public static String formatMinutes(int aMinutes) {
		StringBuilder res = new StringBuilder();
		if (aMinutes < 0) {
			res.append("-");
			aMinutes = -aMinutes;
		}
		int hours = aMinutes / 60;
		int minutes = aMinutes % 60;
		if (hours < 10) {
			res.append("0");
		}
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
		if ((cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
				|| (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)) {
			return true;
		}
		return holidayManager.isHoliday(cal);
	}

	public static String whatHoliday(Date date) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);

		if ((cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
				|| (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)) {
			return "";
		}

		Set<Holiday> holidays = holidayManager.getHolidays(cal
				.get(Calendar.YEAR));
		for (Holiday holiday : holidays) {
			if ((holiday.getDate().getDayOfMonth() == cal
					.get(Calendar.DAY_OF_MONTH))
					&& (holiday.getDate().getMonthOfYear() == (cal
							.get(Calendar.MONTH) + 1))) {
				return holiday.getDescription();
			}
		}
		return "Feiertag";
	}

	public static WorktimeEntry clone(WorktimeEntry aSource) {
		return cloner.deepClone(aSource);
	}

	public static String calculatePlannedBalance(WorktimeEntry aWorkEntry,
			long aTimestamp) {
		return calculatePlannedBalance(aWorkEntry.getStartTime().getTime(),
				aWorkEntry.getDate(), aWorkEntry.getPlanned(), aTimestamp);
	}

	public static boolean isInShortBreak(WorktimeEntry todaysWorktimeEntry,
			long aCurrentTime) {
		long worktime = (System.currentTimeMillis() - todaysWorktimeEntry
				.getStartTime().getTime()) / Units.MINUTE;
		if ((worktime > SHORT_BRAKE_START)
				&& (worktime < SHORT_BRAKE_START + SHORT_BREAK_LENGTH)) {
			return true;
		}
		return false;
	}

	public static boolean isInLongBreak(WorktimeEntry todaysWorktimeEntry,
			long aCurrentTime) {
		long worktime = (System.currentTimeMillis() - todaysWorktimeEntry
				.getStartTime().getTime()) / Units.MINUTE;
		if ((worktime > LONG_BREAK_START)
				&& (worktime < LONG_BREAK_START + LONG_BREAK_LENGTH)) {
			return true;
		}
		return false;
	}

}
