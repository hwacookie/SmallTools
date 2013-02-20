package de.mbaaba.tools.client;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratedStackPanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

import de.mbaaba.tools.shared.Style;
import de.mbaaba.tools.shared.WordList;
import de.mbaaba.tools.shared.WordTypes;

public class MainWindow extends Composite {

	private static final String VERSION_STRING = "0.1";
	private HippieGen hippieGen;
	private IntegerBox integerBox;
	private TextArea resultTextBox;
	private Map<WordTypes, TextArea> areas = new HashMap<WordTypes, TextArea>();
	private MenuBar styleMenuBar;
	private Label lbStyle;
	private InlineHTML styleDesription;

	public MainWindow() {

		/**
		 * Create a remote service proxy to talk to the server-side Greeting
		 * service.
		 */

		hippieGen = new HippieGen();

		final Timer timer = new Timer() {

			@Override
			public void run() {
				hippieGen.getStyleNames(new Listener() {

					@Override
					public void notifyMe() {
						String[] availableStyles = hippieGen.getAvailableStyles();
						if (availableStyles.length > 0) {
							setStyles(availableStyles);
						} else {
							schedule(1000);
						}
					}

					@Override
					public void notifyFail(Throwable aCaught) {
						schedule(1000);
					}
				});
			}
		};

		timer.schedule(1000);

		DockPanel dockPanel = new DockPanel();
		dockPanel.setBorderWidth(1);
		initWidget(dockPanel);
		dockPanel.setHeight("100%");
		dockPanel.setWidth("165px");

		Image image = new Image("HippieIpsum.png");
		dockPanel.add(image, DockPanel.WEST);

		// createMenuBar(vPanel);
		CellPanel themeInfoPanel = createThemeInfoPanel();
		dockPanel.add(themeInfoPanel, DockPanel.NORTH);

		CellPanel generatorPanel = createGeneratorPanel();
		dockPanel.add(generatorPanel, DockPanel.CENTER);
		dockPanel.setCellHeight(generatorPanel, "100%");

		CellPanel statusPanel = createStatusBar();
		dockPanel.add(statusPanel, DockPanel.SOUTH);
		dockPanel.setCellVerticalAlignment(statusPanel, HasVerticalAlignment.ALIGN_BOTTOM);
		dockPanel.setCellHorizontalAlignment(statusPanel, HasHorizontalAlignment.ALIGN_RIGHT);
	}

	protected void setStyles(String[] availableThemes) {
		for (final String string : availableThemes) {
			styleMenuBar.addItem(new MenuItem(string, new ScheduledCommand() {

				@Override
				public void execute() {
					loadStyle(string);
				}
			}));
		}
	}

	protected void loadStyle(String aStyleName) {

		// clear old lists
		Collection<TextArea> values = areas.values();
		for (TextArea textArea : values) {
			textArea.setText("");
		}
		resultTextBox.setText("");
		final WaitBox box = new WaitBox("Please Wait", "Loading style \"" + aStyleName + "\", please wait ...");
		box.show();

		Listener listener = new Listener() {

			@Override
			public void notifyMe() {
				Style style = hippieGen.getCurrentStyle();
				lbStyle.setText("Style: " + style.getName());
				styleDesription.setHTML("<div align=left><h3>" + hippieGen.getCurrentStyle().getName() + " - Style"
						+ "</h3></div>&nbsp;&nbsp;" + style.getName());
				Collection<Entry<WordTypes, TextArea>> areaEntries = areas.entrySet();
				for (Entry<WordTypes, TextArea> areaEntry : areaEntries) {
					WordList wl = style.getWordsMap().get(areaEntry.getKey());
					areaEntry.getValue().setText(wl.buildString());
				}
				box.hide();
			}

			@Override
			public void notifyFail(Throwable aCaught) {
				box.hide();
				AlertBox alertBox = new AlertBox("Error", "Could not load style: " + aCaught.getMessage());
				alertBox.show();
			}
		};

		hippieGen.loadStyle(aStyleName, listener);
	}

	private CellPanel createStatusBar() {
		HorizontalPanel statusPanel = new HorizontalPanel();
		statusPanel.setSpacing(5);
		statusPanel.setHeight("20");

		Label lbVersion = new Label("Version: " + VERSION_STRING);
		statusPanel.add(lbVersion);
		statusPanel.setCellVerticalAlignment(lbVersion, HasVerticalAlignment.ALIGN_MIDDLE);

		lbStyle = new Label("Theme: " + "Default");
		lbStyle.setText("Theme: " + "Default");
		statusPanel.add(lbStyle);
		statusPanel.setCellVerticalAlignment(lbStyle, HasVerticalAlignment.ALIGN_MIDDLE);
		return statusPanel;
	}

	private void createMenuBar(VerticalPanel vPanel) {
		MenuBar mainMenu = new MenuBar(false);
		vPanel.add(mainMenu);

		MenuItem miThemes = createThemesMenu();
		mainMenu.addItem(miThemes);

		MenuItem miHelp = createHelpMenu();
		mainMenu.addItem(miHelp);
	}

	private CellPanel createGeneratorPanel() {
		HorizontalPanel generatorPanel = new HorizontalPanel();
		generatorPanel.setSize("800px", "576px");

		DecoratedStackPanel leftPanel = new DecoratedStackPanel();
		leftPanel.setStyleName("gwt-StackPanel");
		leftPanel.setSize("265px", "100%");
		createLists(leftPanel);

		VerticalPanel rightPanel = new VerticalPanel();
		rightPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		rightPanel.setBorderWidth(0);
		rightPanel.setSize("100%", "100%");

		CellPanel parameterArea = createParameterArea();
		rightPanel.add(parameterArea);

		SimplePanel outputArea = createOutputPanel();
		rightPanel.add(outputArea);

		generatorPanel.setCellHeight(leftPanel, "100%");
		generatorPanel.setCellWidth(leftPanel, "300");
		generatorPanel.add(leftPanel);
		generatorPanel.setCellHeight(rightPanel, "100%");
		generatorPanel.setCellWidth(rightPanel, "100%");
		generatorPanel.add(rightPanel);

		return generatorPanel;

	}

	private CellPanel createThemeInfoPanel() {
		HorizontalPanel themeInfoPanel = new HorizontalPanel();
		themeInfoPanel.setSpacing(5);
		themeInfoPanel.setSize("100%", "85px");

		styleDesription = new InlineHTML();
		styleDesription.setHTML("<h2>Info panel</h2>\r\nSome description");
		themeInfoPanel.add(styleDesription);
		styleDesription.setHeight("100%");
		return themeInfoPanel;
	}

	private CellPanel createParameterArea() {
		HorizontalPanel horzPanel = new HorizontalPanel();
		horzPanel.setSpacing(5);
		horzPanel.setSize("100%", "");

		Label lblNewLabel = new Label("Number of paragraphs:");
		horzPanel.add(lblNewLabel);
		lblNewLabel.setWidth("135px");
		horzPanel.setCellVerticalAlignment(lblNewLabel, HasVerticalAlignment.ALIGN_MIDDLE);

		integerBox = new IntegerBox();
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
				Set<Entry<WordTypes, TextArea>> entrySet = areas.entrySet();
				for (Entry<WordTypes, TextArea> entry : entrySet) {
					WordList wordList = hippieGen.getCurrentStyle().getWordsMap().get(entry.getKey());
					wordList.parse(entry.getValue().getText());
				}
				String s = hippieGen.createText(integerBox.getValue(), numSentencesBox.getValue());
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

	private void createLists(DecoratedStackPanel parent) {
		createWordGroup(parent, WordTypes.NUMBER_SINGULAR);
		createWordGroup(parent, WordTypes.NUMBER_PLURAL);
		createWordGroup(parent, WordTypes.ATTRIBUTE_OF_SUBJECT_SINGULAR);
		createWordGroup(parent, WordTypes.ATTRIBUTE_OF_SUBJECT_PLURAL);
		createWordGroup(parent, WordTypes.NOUN_SINGULAR);
		createWordGroup(parent, WordTypes.NOUN_PLURAL);
		createWordGroup(parent, WordTypes.VERB_SINGULAR);
		createWordGroup(parent, WordTypes.VERB_PLURAL);

		createWordGroup(parent, WordTypes.ATTRIBUTE_OF_OBJECT);
		createWordGroup(parent, WordTypes.PREPOSITION);
		createWordGroup(parent, WordTypes.POSSESSIVE_PRONOUN);
		createWordGroup(parent, WordTypes.PUNCTUATION);
	}

	private MenuItem createThemesMenu() {
		MenuBar menuBar = new MenuBar(true);
		MenuItem menuItem = new MenuItem("Themes", false, menuBar);

		hippieGen.getAvailableStyles();

		styleMenuBar = new MenuBar(true);
		menuBar.addItem("Select", styleMenuBar);

		hippieGen.getAvailableStyles();// .loadStyle(HippieGen.DEFAULT_STYLE);

		MenuItem miSave = new MenuItem("Save", false, new Command() {

			@Override
			public void execute() {
				saveStyle();
			}
		});

		menuBar.addItem(miSave);

		return menuItem;
	}

	private MenuItem createHelpMenu() {
		MenuBar menuBar = new MenuBar(true);
		MenuItem menuItem = new MenuItem("Help", false, menuBar);

		MenuItem mntmAbout = new MenuItem("About", false, new Command() {
			public void execute() {
				new AboutDialog();
			}
		});
		menuBar.addItem(mntmAbout);
		return menuItem;
	}

	private void createWordGroup(DecoratedStackPanel wordListStackPanel, final WordTypes aWordType) {
		WordList wordList = hippieGen.getCurrentStyle().getWordsMap().get(aWordType);
		final TextArea txtArea = new TextArea();
		txtArea.setVisibleLines(14);
		wordListStackPanel.add(txtArea, wordList.getWordType().toString(), false);
		txtArea.setSize("100%", "100%");
		txtArea.setText("Juhu!");
		areas.put(aWordType, txtArea);
	}

	private void saveStyle() {
		Set<Entry<WordTypes, TextArea>> entrySet = areas.entrySet();
		for (Entry<WordTypes, TextArea> entry : entrySet) {
			WordList wordList = hippieGen.getCurrentStyle().getWordsMap().get(entry.getKey());
			wordList.parse(entry.getValue().getText());
		}

		final WaitBox box = new WaitBox("Please Wait", "Saving data ...");
		box.show();

		Listener listener = new Listener() {

			@Override
			public void notifyMe() {
				box.hide();
			}

			@Override
			public void notifyFail(Throwable aCaught) {
				box.hide();
				AlertBox alertBox = new AlertBox("Error", "Could not save style: " + aCaught.getMessage());
				alertBox.show();
			}
		};
		hippieGen.saveStyle(listener);
	}

}
