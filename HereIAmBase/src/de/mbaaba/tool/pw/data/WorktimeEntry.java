package de.mbaaba.tool.pw.data;

import java.util.Date;

public class WorktimeEntry {

	public static final int DEFAULT_PLAN_WORKTIME = 480;

	public static final int NO_PLAN = -1;

	private Date date;
	private Date startTime;
	private Date endTime;
	private String comment;
	private int planned = NO_PLAN;

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date aStartTime) {
		this.startTime = aStartTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date aEndTime) {
		endTime = aEndTime;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String aComment) {
		comment = aComment;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date aDate) {
		date = aDate;
	}

	public int getPlanned() {
		if (planned == NO_PLAN) {
			if (WorktimeEntryUtils.isHoliday(getDate())) {
				return 0;
			} else {
				return DEFAULT_PLAN_WORKTIME;
			}
		}
		return planned;
	}

	public void setPlanned(int planned) {
		this.planned = planned;
	}

}
