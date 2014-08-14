/* --------------------------------------------------------------------------
 * @author Hauke Walden
 * @created 28.06.2011 
 * Copyright 2011 by Hauke Walden 
 * All rights reserved.
 * --------------------------------------------------------------------------
 */
package de.mbaaba.tool;

import de.mbaaba.tool.HereIAm.Activity;

public interface PresenceListener {
	void timeChange();
	void activityChange(Activity aActivity);
}
