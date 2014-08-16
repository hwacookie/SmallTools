package de.mbaaba.tool.pw.data;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.mbaaba.util.ConfigManager;

public class WorktimeEntry {


	public static final int NO_PLAN = -1;

	private Date date;
	private Date startTime;
	private Date endTime;
	private String comment;
	private int planned = NO_PLAN;

	private int activityIndicator;

	public Date getStartTime() {
		return startTime;
	}

	private void fixDate(Date aDate) {
		Calendar dateCal = new GregorianCalendar();
		dateCal.setTime(getDate());

		Calendar cal = new GregorianCalendar();
		cal.setTime(aDate);
		cal.set(Calendar.SECOND, 0);

		cal.set(Calendar.YEAR, dateCal.get(Calendar.YEAR));
		cal.set(Calendar.MONTH, dateCal.get(Calendar.MONTH));
		cal.set(Calendar.DAY_OF_MONTH, dateCal.get(Calendar.DAY_OF_MONTH));

		aDate.setTime(cal.getTime().getTime());
	}

	public void setStartTime(Date aStartTime) {
		if (aStartTime != null) {
			startTime = aStartTime;
			fixDate(startTime);
		} else {
			startTime = null;
		}
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date aEndTime) {
		if (aEndTime != null) {
			endTime = aEndTime;
			fixDate(aEndTime);
		} else {
			endTime = null;
		}
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String aComment) {
		comment = aComment;
	}

	public Date getDate() {
		if (date == null) {
			throw new RuntimeException("Big Problem: The date of a worktime-entry must never be \"null\"!");
		}
		return date;
	}

	public void setDate(Date aDate) {
		if (aDate == null) {
			throw new RuntimeException("The date of a worktime-entry must never be set to \"null\"!");
		}
		date = aDate;
	}

	public int getPlanned() {
		if (planned == NO_PLAN) {
			if (WorktimeEntryUtils.isHoliday(getDate())) {
				return 0;
			} else {
				return ConfigManager.getInstance().getProperty(ConfigManager.CFG_DEFAULT_MINUTES, 480);
			}
		}
		return planned;
	}

	public void setPlanned(int planned) {
		this.planned = planned;
	}

	public int getActivityIndicator() {
		return activityIndicator;
	}

	public void addActivity(int aActivityIndicator) {
		activityIndicator += aActivityIndicator;
	}

	public void fixEntries() {
		setDate(getDate());
		setStartTime(getStartTime());
		setEndTime(getEndTime());
	}

}
