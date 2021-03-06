package de.mbaaba.tools.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

import de.mbaaba.tools.client.StyleEvent.StyleAction;
import de.mbaaba.tools.shared.Style;

public class ImportExportDialog extends DialogBox {

	private Style currentStyle;
	private TextArea txtStyleAsText;

	public ImportExportDialog(Style aCurrentStyle) {
		currentStyle = aCurrentStyle;
		final DialogBox box = this;
		box.setText("Import/Export");

		VerticalPanel mainPanel = new VerticalPanel();
		mainPanel.setSize("695px", "212px");

		createEditArea(mainPanel);
		createButtons(mainPanel);
		
		
		
		box.add(mainPanel);
		setPopupPositionAndShow(new PopupPanel.PositionCallback() {
			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				int left = ((Window.getClientWidth() - offsetWidth) / 2) >> 0;
				int top = ((Window.getClientHeight() - offsetHeight) / 2) >> 0;
				setSize("700", "400");
				setPopupPosition(left, top);
			}
		});

	}

	private void createEditArea(VerticalPanel mainPanel) {
		txtStyleAsText = new TextArea();
		txtStyleAsText.setText(currentStyle.exportToText());
		mainPanel.add(txtStyleAsText);
		txtStyleAsText.setSize("100%", "100%");
	}

	private void createButtons(VerticalPanel mainPanel) {
		HorizontalPanel btnArea = new HorizontalPanel();
		mainPanel.add(btnArea);
		btnArea.setSize("100%", "30");

		Button btnImport = new Button("Import");
		btnImport.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				currentStyle.importFromText(txtStyleAsText.getText());
				NotificationManager.getInstance().fireStyleEvent(
						new StyleEvent(currentStyle, StyleAction.CHANGED));

			}
		});
		Button btnCancel = new Button("Cancel");
		btnCancel.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});

		btnArea.add(btnImport);
		btnArea.add(btnCancel);
	}

}
