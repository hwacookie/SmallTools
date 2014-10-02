/* --------------------------------------------------------------------------
 * @author Hauke Walden
 * @created 28.06.2011 
 * Copyright 2011 by Hauke Walden 
 * All rights reserved.
 * --------------------------------------------------------------------------
 */
package de.mbaaba.tool.pw.detectors;

import java.awt.Point;
import java.util.Date;

import de.mbaaba.tool.pw.data.WorktimeEntry;

public class ScreenLockActivityDetector extends AbstractActivityDetector {

	public ScreenLockActivityDetector(long aMinActivityTime, long aInactivityTime) {
		super(aMinActivityTime, aInactivityTime);
	}

	@Override
	public void detect(Point newMousePos) {
		WorktimeEntry worktimeEntry = dataStorage.getTodaysWorktimeEntry();
		if (newMousePos == null) {
			// screen locked
			setActivity(Activity.IDLE);
		} else {
			// back from lock-screen!
			worktimeEntry.setEndTime(new Date());
			dataStorage.saveWorktimeEntry(worktimeEntry);
			dataStorage.saveData();

			setActivity(Activity.WORKING);
		}
	}

}
