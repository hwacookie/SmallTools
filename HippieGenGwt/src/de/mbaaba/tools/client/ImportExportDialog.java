package de.mbaaba.tools.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;

public class ImportExportDialog extends DialogBox {


	public ImportExportDialog() {
		setText("Import");
		final DialogBox box = this;
		box.setText("Theme Import/Export");
		setPopupPositionAndShow(new PopupPanel.PositionCallback() {
			public void setPosition(int offsetWidth, int offsetHeight) {
				int left = ((Window.getClientWidth() - offsetWidth) / 2) >> 0;
				int top = ((Window.getClientHeight() - offsetHeight) / 2) >> 0;
				setPopupPosition(left, top);
			}
		});

		DockPanel dockPanel = new DockPanel();
		setWidget(dockPanel);
		dockPanel.setSize("100%", "100%");

		TextBox txtStyleAsText = new TextBox();
		txtStyleAsText.setText("");
		dockPanel.add(txtStyleAsText, DockPanel.CENTER);
		txtStyleAsText.setWidth("100%");

		HorizontalPanel btnArea = new HorizontalPanel();
		dockPanel.add(btnArea, DockPanel.SOUTH);
		btnArea.setWidth("100%");
		
		Button btnImport = new Button("Import");
		btnImport.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
			}
		});
	}

}
