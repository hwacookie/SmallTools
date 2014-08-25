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
import java.util.Timer;
import java.util.TimerTask;

import de.mbaaba.util.Units;

public class MouseMoveActivityDetector extends AbstractActivityDetector {

	protected static final double MIN_WAY_AFTER_IDLE = 500;

	private PointerInfo lastMousePos;

	protected double waySinceLastIdle;

	protected int numIdles;

	public MouseMoveActivityDetector() {
		super();
		setActivity(Activity.WORKING);
		lastMousePos = MouseInfo.getPointerInfo();

		Timer t = new Timer(false);
		TimerTask tt = new TimerTask() {

			@Override
			public void run() {
				PointerInfo newMousePos = MouseInfo.getPointerInfo();
				if (newMousePos == null) {
					// screen locked
					waySinceLastIdle = 0;
					setActivity(Activity.IDLE);
					lastMousePos = null;
				} else {
					if (lastMousePos == null) {
						// back from lock-screen!
						lastMousePos = newMousePos;
						waySinceLastIdle = MIN_WAY_AFTER_IDLE + 1;
					} else if ((newMousePos.getLocation().x != lastMousePos.getLocation().x)
							|| (newMousePos.getLocation().y != lastMousePos.getLocation().y)) {

						int diffX = Math.abs(newMousePos.getLocation().x - lastMousePos.getLocation().x);
						int diffY = Math.abs(newMousePos.getLocation().y - lastMousePos.getLocation().y);
						double way = Math.sqrt(diffX * diffX + diffY * diffY);

						waySinceLastIdle += way;
						dataStorage.getTodaysWorktimeEntry().addActivity((int) way);
						lastMousePos = newMousePos;
					}
				}

				// in order to prevent activation that occurs just because of a
				// small push against the mouse ...
				if (waySinceLastIdle > MIN_WAY_AFTER_IDLE) {
					setActivity(Activity.WORKING);
					waySinceLastIdle = 0;
					numIdles = 10;
				} else {
					if (numIdles <= 0) {
						waySinceLastIdle = 0;
						setActivity(Activity.IDLE);
					} else {
						numIdles--;
					}
				}
				waySinceLastIdle--;
				if (waySinceLastIdle < 0) {
					waySinceLastIdle = 0;
				}
			}
		};
		t.scheduleAtFixedRate(tt, 20, Units.SECOND);
	}

}
