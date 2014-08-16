package de.mbaaba.tool;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

public class Log {
	private static Logger logger = Logger.getLogger("HereIAm");

	public static void logfileAdd(String aString) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		String msg = sdf.format(new Date()) + ": " + aString;
		System.out.println(msg);
		logger.info(msg);
	}

}
