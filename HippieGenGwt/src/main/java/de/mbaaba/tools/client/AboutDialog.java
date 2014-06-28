package de.mbaaba.tools.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AboutDialog extends DialogBox {
 
	private static final String ABOUT_TEXT = "<h2>Hippie-ipsum.</h2>This is a utility for creating arbitrary blind-text which can be used to fill the empty spaces on web-pages during development.<br><br>Created by Hauke Walden, kannweg \"at\" gmx.biz<br><br>Inspired by <a href=\"http://http://www.loremipsum.de/\">Lorem ipsum</a> and <a href=\"http://baconipsum.com\">Bacon ipsum</a> and triggered by Inka Chall who, by the way, writes the <a href=\"http://blickgewinkelt.de\">best german travel-blog</a> ever! :)<br><br>Contact: <a href=\"mailto://hippie.ipsum@gmail.com\">hippie.ipsum@gmail.com</a>";

	public AboutDialog() {
		final DialogBox box = this;
		box.setText("About");

		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.setSize("695px", "212px");

		Image image = new Image("ProgramIcon.jpg");
		horizontalPanel.add(image);
		horizontalPanel.setCellVerticalAlignment(image,
				HasVerticalAlignment.ALIGN_MIDDLE);

		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.setSpacing(5);
		horizontalPanel.add(verticalPanel);
		horizontalPanel.setCellVerticalAlignment(verticalPanel,
				HasVerticalAlignment.ALIGN_BOTTOM);
		horizontalPanel.setCellHorizontalAlignment(verticalPanel,
				HasHorizontalAlignment.ALIGN_CENTER);

		InlineHTML html = new InlineHTML();
		html.setHTML(ABOUT_TEXT);
		verticalPanel.add(html);

		Button btnOk = new Button("Ok");
		btnOk.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				box.hide();

			}
		});
		btnOk.setWidth("90px");
		verticalPanel.setCellHorizontalAlignment(btnOk,
				HasHorizontalAlignment.ALIGN_RIGHT);
		verticalPanel.add(btnOk);

		box.add(horizontalPanel);
		setPopupPositionAndShow(new PopupPanel.PositionCallback() {
			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				int left = ((Window.getClientWidth() - offsetWidth) / 2) >> 0;
				int top = ((Window.getClientHeight() - offsetHeight) / 2) >> 0;
				setPopupPosition(left, top);
			}
		});
	}

}
