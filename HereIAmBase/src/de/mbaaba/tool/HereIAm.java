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

	private Activity activity;

	private PointerInfo lastMousePos;

	private final PresenceListener presenceListener;

	private DataStorageManager dataStorage = DataStorageManager.getInstance();

	protected double waySinceLastIdle;

	protected int numIdles;

	public HereIAm(PresenceListener aPresenceListener) throws IOException, InterruptedException {

		presenceListener = aPresenceListener;

		logger = Logger.getLogger("HereIAm");

		String home = System.getProperty("user.home");
		File homeFile = new File(home + "/.presenceWatcher");
		homeFile.mkdirs();

		DailyRollingFileAppender dailyRollingFileAppender = new DailyRollingFileAppender(new SimpleLayout(),
				homeFile.getCanonicalPath() + "/PresenceWatcher.log", "'.'yyyy-MM-dd");
		logger.addAppender(dailyRollingFileAppender);

		activity = null;
		setActivity(Activity.WORKING);
		lastMousePos = MouseInfo.getPointerInfo();

		Timer t = new Timer(false);
		TimerTask tt = new TimerTask() {

			@Override
			public void run() {
				PointerInfo newMousePos = MouseInfo.getPointerInfo();
				if (newMousePos == null) {
					// screen locked
					setActivity(Activity.IDLE);
				} else {
					if ((newMousePos.getLocation().x != lastMousePos.getLocation().x)
							|| (newMousePos.getLocation().y != lastMousePos.getLocation().y)) {

						int diffX = Math.abs(newMousePos.getLocation().x - lastMousePos.getLocation().x);
						int diffY = Math.abs(newMousePos.getLocation().y - lastMousePos.getLocation().y);
						double way = Math.sqrt(diffX * diffX + diffY * diffY);

						waySinceLastIdle += way;
						dataStorage.getTodaysWorktimeEntry().addActivity((int) way);
						lastMousePos = newMousePos;
					}
				}
				if (waySinceLastIdle > 50) {
					setActivity(Activity.WORKING);
					waySinceLastIdle = 0;
					numIdles = 10;
				} else {
					if (numIdles <= 0) {
						setActivity(Activity.IDLE);
					} else {
						numIdles--;
					}
				}
				waySinceLastIdle--;
				if (waySinceLastIdle < 0) {
					waySinceLastIdle = 0;
				}

				presenceListener.timeChange();
			}
		};
		t.scheduleAtFixedRate(tt, 20, Units.SECOND);
	}

	public double getWaySinceLastIdle() {
		return waySinceLastIdle;
	}

	protected void setActivity(Activity aActivity) {
		if (aActivity != activity) {
			activity = aActivity;
			logfileAdd(activity.name());
			Date now = new Date();
			WorktimeEntry worktimeEntry = dataStorage.getTodaysWorktimeEntry();

			switch (activity) {
			case IDLE:
				waySinceLastIdle = 0;
				worktimeEntry.setEndTime(now);
				dataStorage.saveWorktimeEntry(worktimeEntry);
				dataStorage.saveData();
				break;
			case WORKING:
				// make sure that todays worktimeEntry has a start date!
				if (worktimeEntry.getStartTime() == null) {
					worktimeEntry.setStartTime(now);
				}

				break;
			default:
				break;
			}
			presenceListener.activityChange(activity);

		}
	}

	private void logfileAdd(String aString) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		String msg = sdf.format(new Date()) + ": " + aString;
		System.out.println(msg);
		logger.info(msg);
	}

}
