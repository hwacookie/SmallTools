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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

import de.mbaaba.tool.pw.data.WorktimeEntry;
import de.mbaaba.util.Units;

public class HereIAm {

	enum Activity {
		WORKING, IDLE
	}

	private Logger logger;

	private long idleProtectionTime = 5 * Units.SECOND;

	private long lastActivity = 0;

	private Activity activity;

	private PointerInfo lastMousePos;

	private final PresenceListener presenceListener;

	private DataStorageManager dataStorage = DataStorageManager.getInstance();

	public HereIAm(PresenceListener aPresenceListener) throws IOException,
			InterruptedException {

		presenceListener = aPresenceListener;

		logger = Logger.getLogger("HereIAm");

		String home = System.getProperty("user.home");
		File homeFile = new File(home + "/.presenceWatcher");
		homeFile.mkdirs();

		DailyRollingFileAppender dailyRollingFileAppender = new DailyRollingFileAppender(
				new SimpleLayout(), homeFile.getCanonicalPath()
						+ "/PresenceWatcher.log", "'.'yyyy-MM-dd");
		logger.addAppender(dailyRollingFileAppender);

		lastActivity = System.currentTimeMillis();
		activity = null;
		setActivity(Activity.WORKING);
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
					if ((newMousePos.getLocation().x != lastMousePos
							.getLocation().x)
							|| (newMousePos.getLocation().y != lastMousePos
									.getLocation().y)) {
						lastMousePos = newMousePos;
						lastActivity = System.currentTimeMillis();
					}
					if (lastActivity + idleProtectionTime < System
							.currentTimeMillis()) {
						setActivity(Activity.IDLE);
					} else {
						dataStorage.getTodaysWorktimeEntry().setEndTime(new Date());
						setActivity(Activity.WORKING);
					}
				}
				presenceListener.timeChange();
			}
		};
		t.scheduleAtFixedRate(tt, 20, Units.SECOND);
	}

	protected void setActivity(Activity aActivity) {
		if (aActivity != activity) {
			activity = aActivity;
			logfileAdd(activity.name());
			Date now = new Date();
			WorktimeEntry worktimeEntry = dataStorage.getTodaysWorktimeEntry();

			switch (activity) {
			case IDLE:
				worktimeEntry.setEndTime(now);
				dataStorage.saveWorktimeEntry(worktimeEntry);
				dataStorage.saveData();
				break;
			case WORKING:
				if (worktimeEntry.getStartTime() == null) {
					worktimeEntry.setStartTime(now);
				}

				break;
			default:
				break;
			}
		}
	}

	private void logfileAdd(String aString) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		String msg = sdf.format(new Date()) + ": " + aString;
		System.out.println(msg);
		logger.info(msg);
	}

}
