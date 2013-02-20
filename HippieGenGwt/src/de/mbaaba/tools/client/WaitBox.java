package de.mbaaba.tools.client;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class WaitBox extends DialogBox {
	public WaitBox(final String header, final String content) {
		super();
		final DialogBox box = this;
		final VerticalPanel panel = new VerticalPanel();
		box.setText(header);
		panel.add(new Label(content));
		// few empty labels to make widget larger
		final Label emptyLabel = new Label("");
		emptyLabel.setSize("auto", "25px");
		panel.add(emptyLabel);
		panel.add(emptyLabel);
		box.add(panel);
		setPopupPositionAndShow(new PopupPanel.PositionCallback() {
			public void setPosition(int offsetWidth, int offsetHeight) {
				int left = ((Window.getClientWidth() - offsetWidth) / 2) >> 0;
				int top = ((Window.getClientHeight() - offsetHeight) / 2) >> 0;
				setPopupPosition(left, top);
			}
		});
	}

	public static void alertWidget(String header, String content) {
		new WaitBox(header, content);

	}

}
