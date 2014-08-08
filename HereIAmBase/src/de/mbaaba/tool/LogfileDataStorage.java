package de.mbaaba.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.mbaaba.tool.pw.data.WorktimeEntry;
import de.mbaaba.util.Units;

public class LogfileDataStorage implements DataStorage {

	private static final DateFormat DATE_ONLY = new SimpleDateFormat(
			"yyyy-MM-dd");
	private static final DateFormat DATE_AND_TIME = new SimpleDateFormat(
			"dd.MM.yyyy HH:mm:ss");
	private static final String MANUAL_START_OF_WORKDAY = "MANUAL-START-OF-WORKDAY: ";
	private static final String END_OF_WORKDAY = "END-OF-WORKDAY: ";
	private File directory;

	public LogfileDataStorage(File aLogfileDirectory) {
		directory = aLogfileDirectory;
	}

	public static boolean isSameDate(Date date1, Date date2) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(date1);

		Calendar calToday = new GregorianCalendar();
		calToday.setTime(date2);

		// if we are looking for the startDate of tody, we have to look in the
		// current logfile!
		return ((cal.get(Calendar.DATE) == calToday.get(Calendar.DATE))
				&& (cal.get(Calendar.YEAR) == calToday.get(Calendar.YEAR)) && (cal
					.get(Calendar.MONTH) == calToday.get(Calendar.MONTH)));

	}

	private Date getStartTime(Date aWhatDate) {
		File logfile;
		// if we are looking for the startDate of tody, we have to look in the
		// current logfile!
		if (isSameDate(aWhatDate, new Date())) {
			logfile = new File(directory, "PresenceWatcher.log");
		} else {
			String filename = "PresenceWatcher.log."
					+ DATE_ONLY.format(aWhatDate);
			logfile = new File(directory, filename);
		}

		// if we have no logfile for that day, try the current logfile
		if (!logfile.exists()) {
			logfile = new File(directory, "PresenceWatcher.log");
		}
		try (BufferedReader fr = new BufferedReader(new FileReader(logfile))) {
			String line = fr.readLine();
			while (line != null) {
				int pos = line.lastIndexOf(MANUAL_START_OF_WORKDAY);
				if (pos > 0) {
					// found line containing marker for start of work
					line = line.substring(pos
							+ MANUAL_START_OF_WORKDAY.length());
					try {
						Date parsed = DATE_AND_TIME.parse(line);
						if (isSameDate(aWhatDate, parsed)) {
							return parsed;
						}
					} catch (ParseException e) {
						// try again with next line if parse failed
					}
				}
				line = fr.readLine();
			}

		} catch (FileNotFoundException e) {
			// No start date because we have no logfile for that day.
			return null;
		} catch (IOException e) {
			// No start date because we cannot access the logfile for that day.
			return null;
		}
		return null;
	}

	private Date getEndTime(Date aWhatDate) {
		// first, try with the logfile of the date we are looking for.
		String filename = "PresenceWatcher.log." + DATE_ONLY.format(aWhatDate);
		Date res = getEOWfromFile(aWhatDate, filename);
		if (res == null) {
			// if no EOW found, try the logfile of the next day
			filename = "PresenceWatcher.log."
					+ DATE_ONLY
							.format(new Date(aWhatDate.getTime() + Units.DAY));
			res = getEOWfromFile(aWhatDate, filename);
		}
		if (res == null) {
			// if no EOW found, try the current logfile
			filename = "PresenceWatcher.log";
			res = getEOWfromFile(aWhatDate, filename);
		}
		return res;
	}

	private Date getEOWfromFile(Date aWhatDate, String filename) {
		File logfile = new File(directory, filename);
		try (BufferedReader fr = new BufferedReader(new FileReader(logfile))) {
			String line = fr.readLine();
			while (line != null) {
				int pos = line.lastIndexOf(END_OF_WORKDAY);
				if (pos > 0) {
					// found line containing marker for start of work
					line = line.substring(pos + END_OF_WORKDAY.length());
					try {
						Date parsed = DATE_AND_TIME.parse(line);
						if (isSameDate(parsed, aWhatDate)) {
							// if this EOW-line is the the one for the same
							// day-of-week as the one we are looking for, and
							// NOT for the day before ...
							return parsed;
						}
					} catch (ParseException e) {
						// try again with next line if parse failed
					}
				}
				line = fr.readLine();
			}
		} catch (FileNotFoundException e) {
			// No start date because we have no logfile for that day.
			return null;
		} catch (IOException e) {
			// No start date because we cannot access the logfile for that day.
			return null;
		}
		return null;
	}


	@Override
	public WorktimeEntry getWorktimeEntry(Date aDate)  {
		WorktimeEntry res = new WorktimeEntry();
		res.setDate(aDate);
		res.setStartTime(getStartTime(aDate));
		res.setEndTime(getEndTime(aDate));
		return res;
	}



	@Override
	public void saveWorktimeEntry(WorktimeEntry aWorktimeEntry) {
		// TODO Auto-generated method stub
		
	}

}
