package de.mbaaba.tool.pw.detectors;

import java.awt.MouseInfo;
import java.awt.Point;
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

import de.mbaaba.tool.pw.DataStorageManager;
import de.mbaaba.tool.pw.data.WorktimeEntry;
import de.mbaaba.util.Units;

public abstract class AbstractActivityDetector {

	public static final long INACTIVITY_TIME = Units.SECOND * 60;
	public static final long MIN_ACTIVITY_TIME = Units.SECOND * 30;

	public enum Activity {
		WORKING, IDLE
	}

	public interface ActivityListener {
		void notify(Activity aActivity);
	}

	protected DataStorageManager dataStorage = DataStorageManager.getInstance();

	private Logger logger = Logger.getLogger("ActivityDetector");

	private Activity activity;
	protected long inactivityTime;
	protected long minActivityTime;
	protected long lastActivityAt;

	public AbstractActivityDetector(long aMinActivityTime, long aInactivityTime) {
		this.minActivityTime = aMinActivityTime;
		this.inactivityTime = aInactivityTime;
		activity = null;
		String home = System.getProperty("user.home");
		File homeFile = new File(home + "/.presenceWatcher");
		homeFile.mkdirs();

		try {
			DailyRollingFileAppender dailyRollingFileAppender;
			dailyRollingFileAppender = new DailyRollingFileAppender(new SimpleLayout(), homeFile.getCanonicalPath()
					+ "/PresenceWatcher.log", "'.'yyyy-MM-dd");
			logger.addAppender(dailyRollingFileAppender);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void setActivity(Activity aActivity) {
		if (aActivity != activity) {
			String currentUser = new com.sun.security.auth.module.NTSystem().getName();
			System.out.println("Settting activity to " + aActivity + ". Current user is " + currentUser);

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
				// java.awt.Toolkit.getDefaultToolkit().beep();
				// make sure that todays worktimeEntry has a start date!
				if (worktimeEntry.getStartTime() == null) {
					worktimeEntry.setStartTime(now);
				}
				dataStorage.saveWorktimeEntry(worktimeEntry);
				dataStorage.saveData();

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

	public final void startDetection() {
		setActivity(Activity.WORKING);
		lastActivityAt = System.currentTimeMillis();
		
		Timer t = new Timer(false);
		TimerTask tt = new TimerTask() {

			@Override
			public void run() {
				PointerInfo newMousePos = MouseInfo.getPointerInfo();
				detect(newMousePos.getLocation());
			}

		};
		t.scheduleAtFixedRate(tt, 20, Units.SECOND);

	}

	
	public abstract void detect(Point newMousePos);
}
