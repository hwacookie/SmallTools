package de.mbaaba.tools.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import de.mbaaba.tools.shared.Style;

public class HippieGen {

	static final String DEFAULT_STYLE = "Default";

	// private Map<String, WordList> allLists = new HashMap<String, WordList>();

	private final GreetingServiceAsync client;

	public HippieGen() {
		client = GWT.create(GreetingService.class);
		// loadStyle(DEFAULT_STYLE, new TypedListener<Style>() {
		//
		// @Override
		// public void notifyMe(Style aStyle) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void notifyFail(Throwable aCaught) {
		// // TODO Auto-generated method stub
		//
		// }
		// });
	}

	public void loadStyle(final String aStyleName,
			final TypedListener<Style> aListener) {
		AsyncCallback<Style> callback = new AsyncCallback<Style>() {

			@Override
			public void onFailure(Throwable aCaught) {
				aListener.notifyFail(aCaught);
			}

			@Override
			public void onSuccess(Style aStyle) {
				aListener.notifyMe(aStyle);
			}
		};
		getClient().getStyle(aStyleName, callback);
	}

	public void saveStyle(Style aStyle, final TypedListener<Boolean> aListener) {
		AsyncCallback<Void> callback = new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable aCaught) {
				aListener.notifyFail(aCaught);
			}

			@Override
			public void onSuccess(Void blubber) {
				aListener.notifyMe(Boolean.TRUE);
			}
		};
		getClient().saveStyle(aStyle, callback);
	}

	// private void getStyles() {
	// setAvailableStyles(new String[0]);
	// AsyncCallback<String[]> callback = new AsyncCallback<String[]>() {
	//
	// @Override
	// public void onFailure(Throwable aCaught) {
	// AlertBox.alertWidget("Error",
	// "Could not load the list of styles, using default settings instead.");
	// setAvailableStyles(new String[] { DEFAULT_STYLE });
	// }
	//
	// @Override
	// public void onSuccess(String[] aResult) {
	// setAvailableStyles(aResult);
	// }
	// };
	// getClient().getStyleNames(callback);
	// }

	public GreetingServiceAsync getClient() {
		return client;
	}

	public void getStyleNames(final TypedListener<String[]> aListener) {
		AsyncCallback<String[]> callback = new AsyncCallback<String[]>() {

			@Override
			public void onSuccess(String[] result) {
				aListener.notifyMe(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				aListener.notifyFail(caught);
			}
		};
		client.getStyleNames(callback);
	}

}
