/* --------------------------------------------------------------------------
 * @author Hauke Walden
 * @created 28.06.2011 
 * Copyright 2011 by Hauke Walden 
 * All rights reserved.
 * --------------------------------------------------------------------------
 */

package de.mbaaba.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * The Class AdapterConfigurator contains functionality to configure the
 * connection parameters for the {@link #Adapter()}.
 */
class PropertyFileConfigurator implements Configurator {

	/**
	 * The logger.
	 */
	private static final Logger LOG = Logger.getLogger(PropertyFileConfigurator.class);

	/** The file that contains the configuration data. */
	private Properties props;

	private String propFileName;

	/**
	 * Instantiates a new adapter configurator.
	 * 
	 * @param aPropFileName
	 */

	public PropertyFileConfigurator(String aPropFileName) {
		propFileName = aPropFileName;
		readProperties();
	}

	public boolean saveProperties() {
		File f = new File(propFileName);
		try {
			props.save(new FileOutputStream(f), "");
			return true;
		} catch (FileNotFoundException e) {
			LOG.error(e.getMessage(), e);
			return false;
		}

	}

	/**
	 * Reads configuration properties from the file "adapter.properties". This
	 * file must reside anywhere in the classpath.
	 */
	public boolean readProperties() {
		props = new Properties();
		try {
			File f = new File(propFileName);
			InputStream resourceStream;
			if (f.exists()) {
				resourceStream = new FileInputStream(f);
			} else {
				resourceStream = getClass().getClassLoader().getResourceAsStream(propFileName);
				if (resourceStream == null) {
					resourceStream = new FileInputStream(new File("../etc/" + propFileName));
				}
			}

			if (resourceStream != null) {
				props.load(resourceStream);
				return true;
			}

		} catch (IOException e) {
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.biotronik.Configurator.adapter.AdapterConfigurator#getProperty(java.lang.String,
	 *      java.lang.String)
	 */
	public String getProperty(String aPropertyName, String aDefaultValue) {
		return props.getProperty(aPropertyName, aDefaultValue);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.biotronik.Configurator.adapter.AdapterConfigurator#setProperty(java.lang.String,
	 *      java.lang.String)
	 */
	public void setProperty(String aPropertyName, String aValue) {
		props.setProperty(aPropertyName, aValue);
	}

	public int getProperty(String aPropertyName, int aDefaultValue) {
		try {
			String s = props.getProperty(aPropertyName, "" + aDefaultValue);
			if (s.startsWith("<setup-")) {
				printerr("Missing a value for " + aPropertyName + " in the property file, please fix!");
				printerr("I will exit now.");
				System.exit(1);
			}
			Integer res = Integer.parseInt(s);
			return res.intValue();
		} catch (NumberFormatException e) {
			return aDefaultValue;
		}
	}

	private void printerr(String aString) {
		LOG.error(aString);
	}

	@Override
	public boolean getProperty(String aPropertyName, boolean aDefaultValue) {
		String s = getProperty(aPropertyName, String.valueOf(aDefaultValue));
		return Boolean.valueOf(s);
	}

	@Override
	public void setProperty(String aPropertyName, boolean aValue) {
		setProperty(aPropertyName, Boolean.toString(aValue));
	}

	@Override
	public void setProperty(String aPropertyName, int aValue) {
		setProperty(aPropertyName, Integer.toString(aValue));
	}

}
