package de.mbaaba.tool;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import junit.framework.Assert;

import org.junit.Test;

import de.mbaaba.tool.pw.data.WorktimeEntry;

public class LogfileDataStorageTest {
	private static final DateFormat DATE_ONLY = new SimpleDateFormat(
			"yyyy-MM-dd");
	private static final DateFormat DATE_AND_TIME = new SimpleDateFormat(
			"dd.MM.yyyy HH:mm:ss");

	@Test
	public void testUnknownStart() throws ParseException {
		// 25.7. has both start and end in the same file (manual shutdown)
		LogfileDataStorage logfileDataStorage = new LogfileDataStorage(
				new File("testdata"));
		WorktimeEntry worktimeEntry = logfileDataStorage
				.getWorktimeEntry(DATE_ONLY.parse("2014-07-24"));
		Assert.assertNull(worktimeEntry.getStartTime());
		Assert.assertEquals("24.07.2014 20:29:28",
				DATE_AND_TIME.format(worktimeEntry.getEndTime()));

	}

	@Test
	public void testGetStartAndEndSameFile() throws ParseException {
		// 25.7. has both start and end in the same file (manual shutdown)
		LogfileDataStorage logfileDataStorage = new LogfileDataStorage(
				new File("testdata"));
		WorktimeEntry worktimeEntry = logfileDataStorage
				.getWorktimeEntry(DATE_ONLY.parse("2014-07-25"));
		Assert.assertEquals("25.07.2014 10:02:25",
				DATE_AND_TIME.format(worktimeEntry.getStartTime()));
		Assert.assertEquals("25.07.2014 20:43:15",
				DATE_AND_TIME.format(worktimeEntry.getEndTime()));

	}

	@Test
	public void testGetStartAndEndTwoFiles() throws ParseException {
		// 28.7. has the start in one file and the end in the logfile of the
		// next day
		LogfileDataStorage logfileDataStorage = new LogfileDataStorage(
				new File("testdata"));
		WorktimeEntry worktimeEntry = logfileDataStorage
				.getWorktimeEntry(DATE_ONLY.parse("2014-07-28"));
		Assert.assertEquals("28.07.2014 09:24:23",
				DATE_AND_TIME.format(worktimeEntry.getStartTime()));
		Assert.assertEquals("28.07.2014 18:13:49",
				DATE_AND_TIME.format(worktimeEntry.getEndTime()));
	}

	@Test
	public void testGetStartAndEndTwoFilesCurrent() throws ParseException {
		// 28.7. has the start in one file and the end in the logfile of the
		// next day
		LogfileDataStorage logfileDataStorage = new LogfileDataStorage(
				new File("testdata"));
		WorktimeEntry worktimeEntry = logfileDataStorage
				.getWorktimeEntry(DATE_ONLY.parse("2014-08-07"));
		Assert.assertEquals("07.08.2014 09:00:00",
				DATE_AND_TIME.format(worktimeEntry.getStartTime()));
		Assert.assertEquals("07.08.2014 18:00:00",
				DATE_AND_TIME.format(worktimeEntry.getEndTime()));
	}

	@Test
	public void testUnknownEnd() throws ParseException {
		// 28.7. has the start in one file and the end in the logfile of the
		// next day
		LogfileDataStorage logfileDataStorage = new LogfileDataStorage(
				new File("testdata"));
		WorktimeEntry worktimeEntry = logfileDataStorage
				.getWorktimeEntry(DATE_ONLY.parse("2014-08-08"));
		Assert.assertEquals("08.08.2014 11:10:21",
				DATE_AND_TIME.format((worktimeEntry.getStartTime())));
		Assert.assertNull(worktimeEntry.getEndTime());
	}

	@Test
	public void testUnknownBoth() throws ParseException {
		LogfileDataStorage logfileDataStorage = new LogfileDataStorage(
				new File("testdata"));
		WorktimeEntry worktimeEntry = logfileDataStorage
				.getWorktimeEntry(DATE_ONLY.parse("2014-08-09"));
		Assert.assertNull(worktimeEntry.getStartTime());
		Assert.assertNull(worktimeEntry.getEndTime());
	}
}
