/* --------------------------------------------------------------------------
 * @author Hauke Walden
 * @created 28.06.2011 
 * Copyright 2011 by Hauke Walden 
 * All rights reserved.
 * --------------------------------------------------------------------------
 */
package de.mbaaba.tool;

enum DisplayMode {
	TIME_PASSED("Zeit vergangen ..."), TIME_LEFT("Zeit �brig ..."), TIME_STARTED("Gekommen um ..."), TIME_FINISHED(
			"Feierabend ist um ..."), IDLE_SINCE("Unt�tig seid ...");

	private String msg;

	public String getMsg() {
		return msg;
	}

	DisplayMode(String aMsg) {
		msg = aMsg;
	}
}