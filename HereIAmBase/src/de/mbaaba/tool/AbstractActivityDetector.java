package de.mbaaba.tool;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

import de.mbaaba.tool.pw.data.WorktimeEntry;

public abstract class AbstractActivityDetector {

	enum Activity {
		WORKING, IDLE
	}

	public interface ActivityListener {
		void notify(Activity aActivity);
	}

	
//	List<ActivityListener> listeners = new ArrayList<AbstractActivityDetector.ActivityListener>();
//	public void addListener(ActivityListener aListener) {
//		if (!listeners.contains(aListener)) {
//			listeners.add(aListener);
//			aListener.notify(activity);
//		}
//	}
	
	protected DataStorageManager dataStorage = DataStorageManager.getInstance();

	private Logger logger = Logger.getLogger("ActivityDetector");

	private Activity activity;

	public AbstractActivityDetector() {
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
}
