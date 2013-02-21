/* --------------------------------------------------------------------------
 * @author Hauke Walden
 * @created 18.03.2009
 * Copyright 2011 by Hauke Walden 
 * All rights reserved.
 * --------------------------------------------------------------------------
 */

package de.mbaaba.util;

/**
 * The Class AdapterConfigurator contains functionality to configure parameters.
 */
public interface Configurator {

	public static final String PROP_DEFAULT_REQUEST_TIMEOUT = "45";

	/** Identifier used to access requestTimeout value in adapter.properties file. */
	public static final String PROP_REQUEST_TIMEOUT = "requestTimeout";

	/**
	 * Gets an arbitrary  property from the current property file.
	 *
	 * @param aPropertyName the property name
	 * @param aDefaultValue a default value
	 *
	 * @return the property value
	 */
	public String getProperty(String aPropertyName, String aDefaultValue);

	public int getProperty(String aPropertyName, int aDefaultValue);


	/**
	 * Sets an arbitrary property. These values will NOT be persisted!
	 *
	 * @param aPropertyName the property name
	 * @param aValue a value
	 *
	 */
	public void setProperty(String aPropertyName, String aValue);
}
