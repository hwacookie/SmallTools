package de.mbaaba.tools.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class HippieGenGwt implements EntryPoint {



	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		MainWindow mainWindow = new MainWindow();
		RootPanel.get().add(mainWindow);
	}
}
