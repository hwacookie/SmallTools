package de.mbaaba.tools.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

import de.mbaaba.tools.shared.Style;

public class GeneratorPanel extends VerticalPanel {
	protected Style currentStyle;
	private TextArea resultTextBox;

	public GeneratorPanel() {
		CellPanel parameterArea = createParameterArea();
		add(parameterArea);

		SimplePanel outputArea = createOutputPanel();
		add(outputArea);
	}

	private CellPanel createParameterArea() {
		HorizontalPanel horzPanel = new HorizontalPanel();
		horzPanel.setSpacing(5);
		horzPanel.setSize("100%", "");

		Label lblNewLabel = new Label("Number of paragraphs:");
		horzPanel.add(lblNewLabel);
		lblNewLabel.setWidth("135px");
		horzPanel.setCellVerticalAlignment(lblNewLabel, HasVerticalAlignment.ALIGN_MIDDLE);

		final IntegerBox integerBox = new IntegerBox();
		integerBox.setAlignment(TextAlignment.RIGHT);
		integerBox.setVisibleLength(4);
		integerBox.setText("4");
		integerBox.setWidth("35%");
		horzPanel.add(integerBox);

		horzPanel.setCellVerticalAlignment(integerBox, HasVerticalAlignment.ALIGN_MIDDLE);
		horzPanel.setCellHorizontalAlignment(integerBox, HasHorizontalAlignment.ALIGN_LEFT);

		Label lb2 = new Label("Paragraph length:");
		horzPanel.add(lb2);
		horzPanel.setCellVerticalAlignment(lb2, HasVerticalAlignment.ALIGN_MIDDLE);

		final IntegerBox numSentencesBox = new IntegerBox();
		numSentencesBox.setAlignment(TextAlignment.CENTER);
		numSentencesBox.setVisibleLength(4);
		numSentencesBox.setText("6");
		numSentencesBox.setWidth("43px");
		horzPanel.add(numSentencesBox);
		horzPanel.setCellVerticalAlignment(numSentencesBox, HasVerticalAlignment.ALIGN_MIDDLE);

		horzPanel.setCellVerticalAlignment(integerBox, HasVerticalAlignment.ALIGN_MIDDLE);
		horzPanel.setCellHorizontalAlignment(integerBox, HasHorizontalAlignment.ALIGN_LEFT);

		Button btnNewButton = new Button("Go");
		btnNewButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				TextGenerator textGenerator = new TextGenerator(currentStyle);
				String s = textGenerator.createText(integerBox.getValue(), numSentencesBox.getValue());
				resultTextBox.setText(s);
			}
		});
		btnNewButton.setText("Go!");
		horzPanel.add(btnNewButton);
		horzPanel.setCellVerticalAlignment(btnNewButton, HasVerticalAlignment.ALIGN_MIDDLE);
		horzPanel.setCellHorizontalAlignment(btnNewButton, HasHorizontalAlignment.ALIGN_RIGHT);

		return horzPanel;

	}

	private SimplePanel createOutputPanel() {
		SimplePanel simplePanel = new SimplePanel();
		simplePanel.setSize("100%", "100%");

		resultTextBox = new TextArea();
		resultTextBox.setVisibleLines(10);
		resultTextBox.setAlignment(TextAlignment.CENTER);
		resultTextBox
				.setText("Dies ist ein Testtext. Klicken Sie auf \"Go\" um Blindtext zu erzeugen. Auf der linken Seite können die Listen mit den verwendeten Wörtern verändert bzw. erweitert werden.");
		resultTextBox.setStyleName("gwt-DialogBox");
		resultTextBox.setTextAlignment(TextArea.ALIGN_LEFT);
		simplePanel.setWidget(resultTextBox);
		resultTextBox.setSize("100%", "100%");

		return simplePanel;
	}

	public void setCurrentStyle(Style aStyle) {
		currentStyle = aStyle;
		resultTextBox.setText("");
	}
}
