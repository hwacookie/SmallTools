/* --------------------------------------------------------------------------
 * @author Hauke Walden
 * @created 28.06.2011 
 * Copyright 2011 by Hauke Walden 
 * All rights reserved.
 * --------------------------------------------------------------------------
 */

package de.mbaaba.util;

public interface Units {
	/** one kilobyte */
	public static final int KILOBYTE = 1024;

	/** one Megabyte */
	public static final int MEGABYTE = KILOBYTE * KILOBYTE;

	/** one Gigabyte */
	public static final int GIGABYTE = KILOBYTE * MEGABYTE;

	/** one millisecond */
	public static final long MILLISECOND = 1;

	/** one second in milliseconds */
	public static final long SECOND = 1000;

	/** one minute in milliseconds */
	public static final long MINUTE = 60 * SECOND;

	/** one hour in milliseconds */
	public static final long HOUR = 60 * MINUTE;

	/** one day in milliseconds */
	public static final long DAY = 24 * HOUR;

	/** one week in milliseconds */
	public static final long WEEK = 7 * DAY;

	/** one <i>month</i> in milliseconds */
	public static final long THIRTY_DAYS = 30 * DAY;

	/** three days in milliseconds */
	public static final long THREE_DAYS = DAY * 3;

	/** 15 days in milliseconds */
	public static final long FIFTEEN_DAYS = DAY * 15;

	/** base for hex-numbers */
	public static final int BASE_HEX = 16;

	/** base for decimal-numbers */
	public static final int BASE_DECIMAL = 10;

	/** base for octal-numbers */
	public static final int BASE_OCTAL = 8;

	/** base for binary-numbers */
	public static final int BASE_BINARY = 2;

	/** number of bits in a byte */
	public static final int BITS_IN_BYTE = 8;
}

