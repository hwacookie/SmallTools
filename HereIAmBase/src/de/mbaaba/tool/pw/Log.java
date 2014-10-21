package de.mbaaba.tool.pw;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

public class Log {
	/**
	 * The logger.
	 */
	private static final Logger LOG = Logger.getLogger(Log.class);

	private static Logger logger = Logger.getLogger("HereIAm");

	public static void logfileAdd(String aString) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		String msg = sdf.format(new Date()) + ": " + aString;
		LOG.info(msg);
		logger.info(msg);
	}
}
