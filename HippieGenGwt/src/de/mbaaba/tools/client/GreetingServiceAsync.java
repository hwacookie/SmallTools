package de.mbaaba.tools.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.mbaaba.tools.shared.Style;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GreetingServiceAsync {
	// void saveList(String aStyleName, String aWordType, String aLines,
	// AsyncCallback<Void> callback);
	// void loadList(String aStyleName, String aWordType, AsyncCallback<String>
	// callback);
	void getStyleNames(AsyncCallback<String[]> callback);

	void getStyle(String aName, AsyncCallback<Style> callback);

	void saveStyle(Style aStyle, AsyncCallback<Void> callback);
}
