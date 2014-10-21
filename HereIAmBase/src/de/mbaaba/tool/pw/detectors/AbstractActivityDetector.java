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

	/** The logger. */
	private static final Logger LOG = Logger.getLogger(AbstractActivityDetector.class);

	public static final long INACTIVITY_TIME = Units.SECOND * 60;
	public static final long MIN_ACTIVITY_TIME = Units.SECOND * 30;

	public enum Activity {
		WORKING, IDLE
	}

	public interface ActivityListener {
		void notify(Activity aActivity);
	}

	protected DataStorageManager dataStorage = DataStorageManager.getInstance();

	private Logger logFileStorage = Logger.getLogger("ActivityDetector");

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
			logFileStorage.addAppender(dailyRollingFileAppender);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	protected void setActivity(Activity aActivity) {
		if (aActivity != activity) {
			String currentUser = new com.sun.security.auth.module.NTSystem().getName();
			LOG.debug("Settting activity to " + aActivity + ". Current user is " + currentUser);

			activity = aActivity;
			logfileAdd(activity.name());

			saveTimestamp();

			switch (activity) {
			case IDLE:
//				java.awt.Toolkit.getDefaultToolkit().beep();
				break;
			case WORKING:
				break;
			default:
				break;
			}
		}
	}

	public static void saveTimestamp() {
		Date now = new Date();
		WorktimeEntry worktimeEntry = DataStorageManager.getInstance().getTodaysWorktimeEntry();
		worktimeEntry.setEndTime(now);
		if (worktimeEntry.getStartTime() == null) {
			worktimeEntry.setStartTime(now);
		}
		DataStorageManager.getInstance().saveWorktimeEntry(worktimeEntry);
		DataStorageManager.getInstance().saveData();
	}

	private void logfileAdd(String aString) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		String msg = sdf.format(new Date()) + ": " + aString;
		LOG.info(msg);
		logFileStorage.info(msg);
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

	public final void stopDetection() {
		setActivity(Activity.IDLE);
	}

	public abstract void detect(Point newMousePos);
}
