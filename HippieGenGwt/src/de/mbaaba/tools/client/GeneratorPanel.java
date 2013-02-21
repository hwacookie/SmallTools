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
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.i18n.client.HasDirection.Direction;

public class GeneratorPanel extends VerticalPanel {
	protected Style currentStyle;

	public GeneratorPanel() {
		CellPanel parameterArea = createParameterArea();
		add(parameterArea);
		
		SimplePanel simplePanel = new SimplePanel();
		add(simplePanel);
		simplePanel.setSize("100%", "100%");
		setCellHeight(simplePanel, "100%");
		
		TextArea txtrDiesIstEin = new TextArea();
		txtrDiesIstEin.setDirection(Direction.LTR);
		simplePanel.setWidget(txtrDiesIstEin);
		setCellHeight(txtrDiesIstEin, "100%");
		setCellWidth(txtrDiesIstEin, "100%");
		txtrDiesIstEin.setVisibleLines(10);
		txtrDiesIstEin.setTextAlignment(TextBoxBase.ALIGN_LEFT);
		txtrDiesIstEin.setText("Dies ist ein Testtext. Klicken Sie auf \"Go\" um Blindtext zu erzeugen. Auf der linken Seite können die Listen mit den verwendeten Wörtern verändert bzw. erweitert werden. hjhgbjhgjhg");
		txtrDiesIstEin.setStyleName("gwt-DialogBox");
		txtrDiesIstEin.setAlignment(TextAlignment.CENTER);
		txtrDiesIstEin.setSize("441px", "100%");
	}

	private CellPanel createParameterArea() {
		HorizontalPanel horzPanel = new HorizontalPanel();
		horzPanel.setSpacing(5);
		horzPanel.setSize("100%", "");

		Label lblNewLabel = new Label("Number of paragraphs:");
		horzPanel.add(lblNewLabel);
		lblNewLabel.setWidth("");
		horzPanel.setCellVerticalAlignment(lblNewLabel, HasVerticalAlignment.ALIGN_MIDDLE);

		final IntegerBox integerBox = new IntegerBox();
		integerBox.setAlignment(TextAlignment.RIGHT);
		integerBox.setVisibleLength(4);
		integerBox.setText("4");
		integerBox.setWidth("16");
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
		numSentencesBox.setWidth("16");
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
	}

	public void setCurrentStyle(Style aStyle) {
		currentStyle = aStyle;
		resultTextBox.setText("");
	}
}
