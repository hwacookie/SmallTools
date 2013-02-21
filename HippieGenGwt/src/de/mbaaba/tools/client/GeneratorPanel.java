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
	private static final String INTRO_TEXT = "Um Blindtext zu erzeugen, klicke auf 'Go'.\n"
			+ "Unter 'Themes' im Menü oben kannst du das Thema ändern, dann ändern sich die Wortlisten links. Die Wörter der Wortlisten können jeweils verändert oder erweitert werden. Ausnahme: Theme Hippie.\n"
			+ "Und ey, keinen unhippiemäßigen Quatsch machen! Die Wortlisten werden nicht lokal sondern auf dem Server für alle Menschenskinder gespeichert. Die anderen wollen ja auch nochmal.\n"
			+ "\n"
			+

			"To generate blindtext click 'Go'.\n"
			+ "Change a theme in menu on top - the lists of words on the left side will change as well. You can change and add the words of the lists. Exception: Theme Hippie.\n"
			+ "And, hey, don´t do unhippie shit! The lists of words won´t be saved on your computer but on the server for every human child. Peace.";

	protected Style currentStyle;
	private TextArea resultTextBox;

	public GeneratorPanel() {
		setSpacing(5);
		CellPanel parameterArea = createParameterArea();
		add(parameterArea);

		SimplePanel simplePanel = new SimplePanel();
		add(simplePanel);
		simplePanel.setSize("100%", "100%");
		setCellHeight(simplePanel, "100%");

		resultTextBox = new TextArea();
		simplePanel.setWidget(resultTextBox);
		setCellHeight(resultTextBox, "100%");
		setCellWidth(resultTextBox, "100%");
		resultTextBox.setText(INTRO_TEXT);
		resultTextBox.setStyleName("gwt-DialogBox");
		resultTextBox.setSize("441px", "100%");
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

	public void setCurrentStyle(Style aStyle) {
		currentStyle = aStyle;
		resultTextBox.setText("");
	}
}
