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
		box.setText("Import/Export " + currentStyle.getName());

		VerticalPanel mainPanel = new VerticalPanel();
		mainPanel.setSize("695px", "212px");

		txtStyleAsText = new TextArea();
		txtStyleAsText.setText(currentStyle.exportToText());
		mainPanel.add(txtStyleAsText);
		txtStyleAsText.setSize("100%", "100%");

		HorizontalPanel btnArea = new HorizontalPanel();
		mainPanel.add(btnArea);
		btnArea.setSize("100%", "30");

		Button btnImport = new Button("Import");
		btnImport.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				currentStyle.importFromText(txtStyleAsText.getText());
				StyleManager.getInstance().notifyChange(
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
		
		
		
		box.add(mainPanel);
		setPopupPositionAndShow(new PopupPanel.PositionCallback() {
			public void setPosition(int offsetWidth, int offsetHeight) {
				int left = ((Window.getClientWidth() - offsetWidth) / 2) >> 0;
				int top = ((Window.getClientHeight() - offsetHeight) / 2) >> 0;
				setPopupPosition(left, top);
			}
		});

	}

}
