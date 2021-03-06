package de.mbaaba.tools.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.mbaaba.tools.shared.Style;

/**
 * The async counterpart of <code>HippieIpsumService</code>.
 */
public interface HippieIpsumServiceAsync {
	void getStyleNames(AsyncCallback<String[]> callback);

	void getStyle(String aName, AsyncCallback<Style> callback);

	void saveStyle(Style aStyle, AsyncCallback<Void> callback);

	void newStyle(Style aStyle, AsyncCallback<Void> callback);
}
