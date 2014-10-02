/* --------------------------------------------------------------------------
 * @author Hauke Walden
 * @created 28.06.2011 
 * Copyright 2011 by Hauke Walden 
 * All rights reserved.
 * --------------------------------------------------------------------------
 */
package de.mbaaba.tool.pw.detectors;

import java.awt.Point;

public class MouseMoveActivityDetector extends AbstractActivityDetector {



	public MouseMoveActivityDetector(long aMinActivityTime, long aInactivityTime) {
		super(aMinActivityTime, aInactivityTime);
	}


	private Point lastMousePos = null;
	private boolean mousePosChanged = false;

	
	private boolean mousePosChanged(Point newMousePos, Point aLastMousePos) {
		return ((newMousePos.getLocation().x != aLastMousePos.getLocation().x) || (newMousePos.getLocation().y != aLastMousePos
				.getLocation().y));
	}


	@Override
	public
	void detect(Point newMousePos) {
		long now = System.currentTimeMillis();
		if (newMousePos != null) {
			if (lastMousePos == null) {
				lastActivityAt = System.currentTimeMillis();
				lastMousePos = newMousePos;
			} else  {
				if (!mousePosChanged) {
					mousePosChanged = mousePosChanged(newMousePos, lastMousePos);
				}
				if (now > lastActivityAt + minActivityTime) {
					if (mousePosChanged) {
						lastActivityAt = System.currentTimeMillis();
						lastMousePos = newMousePos;
						setActivity(Activity.WORKING);
						mousePosChanged=false;
					}
				}
			}

			// if more than the defined has passed without a change
			// of the mouse position ...
			if (now > lastActivityAt + inactivityTime) {
				setActivity(Activity.IDLE);
			}
		}
	}
	
}
