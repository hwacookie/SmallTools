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

	public boolean getProperty(String aPropertyName, boolean aDefaultValue);


	/**
	 * Sets an arbitrary property. These values will NOT be persisted!
	 *
	 * @param aPropertyName the property name
	 * @param aValue a value
	 *
	 */
	public void setProperty(String aPropertyName, String aValue);

	public void setProperty(String aPropertyName, boolean aValue);

	public boolean saveProperties();
}
