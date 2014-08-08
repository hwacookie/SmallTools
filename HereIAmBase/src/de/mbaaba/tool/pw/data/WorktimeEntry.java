package de.mbaaba.tool.pw.data;

import java.util.Date;

public class WorktimeEntry {

	private Date date;
	private Date startTime;
	private Date endTime;
	private String comment;
	
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

}
