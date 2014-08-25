/* --------------------------------------------------------------------------
 * @author Hauke Walden
 * @created 28.06.2011 
 * Copyright 2011 by Hauke Walden 
 * All rights reserved.
 * --------------------------------------------------------------------------
 */
package de.mbaaba.tool.pw.detectors;

import java.awt.MouseInfo;
import java.awt.PointerInfo;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import de.mbaaba.tool.pw.data.WorktimeEntry;
import de.mbaaba.util.Units;

public class ScreenLockActivityDetector extends AbstractActivityDetector {

	public ScreenLockActivityDetector() {

		super();

		Timer t = new Timer(false);
		TimerTask tt = new TimerTask() {

			@Override
			public void run() {
				PointerInfo newMousePos = MouseInfo.getPointerInfo();
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
		};
		t.scheduleAtFixedRate(tt, 20, Units.MINUTE);
	}

}
