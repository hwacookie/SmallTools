/* --------------------------------------------------------------------------
 * @author Hauke Walden
 * @created 28.06.2011 
 * Copyright 2011 by Hauke Walden 
 * All rights reserved.
 * --------------------------------------------------------------------------
 */
package de.mbaaba.tool;

import java.awt.MouseInfo;
import java.awt.PointerInfo;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

import de.mbaaba.util.Configurator;
import de.mbaaba.util.PropertyFileConfigurator;
import de.mbaaba.util.Units;

public class HereIAm {

	private static final int WORKDAY_NOT_STARTED_YET = -1;

	enum Activity {
		STARTING, IDLE, WORKING
	}

	protected static final long WORK_TIME_PER_DAY = 8 * Units.HOUR + 45 * Units.MINUTE;

	private Logger logger;

	private long idleTime = 5 * Units.SECOND;

	private long startToday = 0;

	private long lastActivity = 0;

	private Activity activity;

	private PointerInfo lastMousePos;

	private Configurator configurator;

	private long endToday;

	private final PresenceListener presenceListener;

	public HereIAm(Configurator aConfigurator, PresenceListener aPresenceListener) throws IOException, InterruptedException {

		presenceListener = aPresenceListener;
		registerShutdownHook();

		configurator = aConfigurator;
		idleTime = configurator.getProperty("IDLE_TIME", 10) * Units.SECOND;

		logger = Logger.getLogger("HereIAm");

		String home = System.getProperty("user.home");
		File homeFile=new File(home+"/.presenceWatcher");
		homeFile.mkdirs();
		
		
		DailyRollingFileAppender dailyRollingFileAppender = new DailyRollingFileAppender(new SimpleLayout(), homeFile.getCanonicalPath()+"/PresenceWatcher.log",
				"'.'yyyy-MM-dd");
		logger.addAppender(dailyRollingFileAppender);

		startWorkDay();
		lastActivity = System.currentTimeMillis();
		setActivity(Activity.STARTING);
		lastMousePos = MouseInfo.getPointerInfo();

		Timer t = new Timer(false);
		TimerTask tt = new TimerTask() {

			@Override
			public void run() {
				PointerInfo newMousePos = MouseInfo.getPointerInfo();
				if (newMousePos == null) {
					// screen locked ?
					setActivity(Activity.IDLE);
				} else {
					if ((newMousePos.getLocation().x != lastMousePos.getLocation().x)
							|| (newMousePos.getLocation().y != lastMousePos.getLocation().y)) {
						lastMousePos = newMousePos;
						lastActivity = System.currentTimeMillis();
					}
					if (lastActivity + idleTime < System.currentTimeMillis()) {
						setActivity(Activity.IDLE);
					} else {
						setActivity(Activity.WORKING);
					}
				}
				timeChange();
			}
		};
		t.scheduleAtFixedRate(tt, 2000, 500);
	}

	private void registerShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				endWorkDay(" (manual shutdown)");
			}
		});
	}

	private void startWorkDay() {
		SimpleDateFormat sdfKey = new SimpleDateFormat("yyyy_MM_dd");
		String dayKey = sdfKey.format(new Date());
		SimpleDateFormat sdfValue = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		String keyNameStart = "START_" + dayKey;
		String defaultValue = "" + WORKDAY_NOT_STARTED_YET;
		String startFromProperty = configurator.getProperty(keyNameStart, defaultValue);

		if (startFromProperty.equals(defaultValue)) {
			setStartTime(System.currentTimeMillis());
			// save in properties
			configurator.setProperty(keyNameStart, sdfValue.format(getStartToday()));
		} else {
			try {
				long tempStartToday = sdfValue.parse(startFromProperty).getTime();
				setStartTime(tempStartToday);
			} catch (ParseException e) {
				setStartTime(System.currentTimeMillis());
			}
		}
	}

	protected void timeChange() {
		// check if new day has started
		checkIfNewDayHasStarted();
		presenceListener.timeChange();
	}

	private void checkIfNewDayHasStarted() {
		if (getStartToday() != WORKDAY_NOT_STARTED_YET) {
			Date now = new Date();
			Date lad = new Date(lastActivity);
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTime(now);
			int hourNow = calendar.get(Calendar.HOUR_OF_DAY);

			calendar.setTime(lad);
			int hourLad = calendar.get(Calendar.HOUR_OF_DAY);

			if (hourNow < hourLad) {
				// new day has started!
				endWorkDay("");
			}
		}
	}

	/**
	 * This method must be called if an end of the work-day has been detected, i.e. if a day-change was detected.
	 */
	private void endWorkDay(String aComment) {
		// a change of day has happened. Prepare for a new work-day.
		setStartTime(WORKDAY_NOT_STARTED_YET);
		SimpleDateFormat sdfValue = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		String msg = sdfValue.format(new Date(lastActivity));
		logfileAdd("END-OF-WORKDAY: " + msg);

		SimpleDateFormat sdfKey = new SimpleDateFormat("yyyy_MM_dd");
		String dayKey = sdfKey.format(lastActivity);
		String keyNameEnd = "END_" + dayKey;
		configurator.setProperty(keyNameEnd, msg);

		configurator.setProperty(keyNameEnd, sdfValue.format(lastActivity) + aComment);

	}

	protected void setActivity(Activity aActivity) {
		if (aActivity != activity) {
			activity = aActivity;
			logfileAdd(activity.name());

			switch (activity) {
			case IDLE:
				break;
			case WORKING:
			case STARTING:
				if (getStartToday() == WORKDAY_NOT_STARTED_YET) {
					startNewDay();
				}
				break;
			default:
				break;
			}
			presenceListener.statusChange(activity);
		}
	}

	private void startNewDay() {
		setActivity(Activity.STARTING);
		setStartTime(System.currentTimeMillis());
	}

	private void logfileAdd(String aString) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		String msg = sdf.format(new Date()) + ": " + aString;
		System.out.println(msg);
		logger.info(msg);
	}

	public static void main(String[] args) throws InterruptedException, IOException {
		Configurator configurator = new PropertyFileConfigurator("HereIAm.properties");
		new HereIAm(configurator, new PresenceListener() {

			public void timeChange() {
				// TODO Auto-generated method stub

			}

			public void statusChange(Activity aActivity) {
				// TODO Auto-generated method stub

			}
		});
		Thread.sleep(Long.MAX_VALUE);
	}

	public void setStartTime(long aTime) {
		// manual setting of start time
		startToday = aTime;
		if (startToday!=WORKDAY_NOT_STARTED_YET) {
			SimpleDateFormat sdfValue = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
			String msg = sdfValue.format(new Date(aTime));
			logfileAdd("MANUAL-START-OF-WORKDAY: " + msg);
	
			SimpleDateFormat sdfKey = new SimpleDateFormat("yyyy_MM_dd");
			String dayKey = sdfKey.format(lastActivity);
			String keyNameStart = "START_" + dayKey;
			configurator.setProperty(keyNameStart, msg);
			endToday = getStartToday() + WORK_TIME_PER_DAY;
		}
	}

	public Activity getActivity() {
		return activity;
	}

	public long getEndToday() {
		return endToday;
	}

	public long getLastActivity() {
		return lastActivity;
	}

	public long getStartToday() {
		return startToday;
	}
}
