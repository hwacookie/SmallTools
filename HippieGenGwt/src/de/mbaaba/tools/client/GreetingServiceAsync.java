package de.mbaaba.tools.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.mbaaba.tools.shared.Style;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GreetingServiceAsync {
	void getStyleNames(AsyncCallback<String[]> callback);

	void getStyle(String aName, AsyncCallback<Style> callback);

	void saveStyle(Style aStyle, AsyncCallback<Void> callback);
}
