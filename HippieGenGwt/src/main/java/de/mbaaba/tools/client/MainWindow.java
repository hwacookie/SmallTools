package de.mbaaba.tools.client;


import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;

import de.mbaaba.tools.client.StyleEvent.StyleAction;
import de.mbaaba.tools.shared.Style;
import de.mbaaba.tools.shared.WordList;
import de.mbaaba.tools.shared.WordTypes;

public class MainWindow extends Composite {

	private static Logger logger = Logger.getLogger(MainWindow.class);

	private static final String VERSION_STRING = "0.3";
	private static final String DEFAULT_THEME = "Hippie (deutsch)";
	private HippieGen hippieGen;
	private MenuBar styleMenuBar;
	private InlineHTML styleDesription;
	// private WordListPanel wordListPanel;
	private TextGeneratorPanel generatorPanel;
	private Style currentStyle;

	public MainWindow() {

		/**
		 * Create a remote service proxy to talk to the server-side service.
		 */

		NotificationManager.getInstance().addListener(new TypedListener<StyleEvent>() {

			@Override
			public void notifyMe(StyleEvent aResult) {
				switch (aResult.action) {
				case CHANGED:
					currentStyle = aResult.style;
					styleDesription.setHTML("<div align=left><h3>"
							+ currentStyle.getName() + " - Style"
							+ "</h3></div>&nbsp;"
							+ currentStyle.getDescription());

				default:
					break;
				}
			}

			@Override
			public void notifyFail(Throwable aCaught) {
			}
		});

		hippieGen = new HippieGen();

		final WaitBox box = new WaitBox("Please Wait",
				"Loading styles, please wait ...");
		box.show();

		final Timer timer = new Timer() {

			@Override
			public void run() {
				hippieGen.getStyleNames(new TypedListener<String[]>() {

					@Override
					public void notifyMe(String[] aStyleNames) {
						String[] availableStyles = aStyleNames;
						if (availableStyles.length > 0) {
							setStyles(availableStyles);
							box.hide();
							loadStyle(DEFAULT_THEME);
						} else {
							schedule(1000);
						}
					}

					@Override
					public void notifyFail(Throwable aCaught) {
						System.out.println(aCaught);
						schedule(1000);
					}
				});
			}
		};

		timer.schedule(1000);

		DockPanel dockPanel = new DockPanel();
		dockPanel.setBorderWidth(0);
		initWidget(dockPanel);

		dockPanel.setHeight("100%");
		dockPanel.setWidth("165px");

		Image image = new Image("HippieIpsum.png");
		dockPanel.add(image, DockPanel.WEST);

		Panel menuBar = createMenuBar();
		dockPanel.add(menuBar, DockPanel.NORTH);

		Panel themeInfoPanel = createThemeInfoPanel();
		dockPanel.add(themeInfoPanel, DockPanel.NORTH);

		Panel centerPanel = createCenterPanel();
		dockPanel.add(centerPanel, DockPanel.CENTER);
		dockPanel.setCellHeight(centerPanel, "100%");
		dockPanel.setCellWidth(centerPanel, "100%");

		Panel statusPanel = createStatusBar();
		dockPanel.add(statusPanel, DockPanel.SOUTH);
		dockPanel.setCellVerticalAlignment(statusPanel,
				HasVerticalAlignment.ALIGN_BOTTOM);
		dockPanel.setCellHorizontalAlignment(statusPanel,
				HasHorizontalAlignment.ALIGN_LEFT);
	}

	protected void setStyles(String[] availableThemes) {
		for (final String string : availableThemes) {
			if (styleMenuBar != null) {
				styleMenuBar.addItem(new MenuItem(string,
						new ScheduledCommand() {

							@Override
							public void execute() {
								loadStyle(string);
							}
						}));
			}
		}

	}

	protected void loadStyle(String aStyleName) {

		final WaitBox box = new WaitBox("Please Wait", "Loading style \""
				+ aStyleName + "\", please wait ...");
		box.show();

		TypedListener<Style> listener = new TypedListener<Style>() {

			@Override
			public void notifyMe(Style aStyle) {
				NotificationManager.getInstance().fireStyleEvent(
						new StyleEvent(aStyle, StyleAction.CHANGED));
				box.hide();
			}

			@Override
			public void notifyFail(Throwable aCaught) {
				box.hide();
				AlertBox alertBox = new AlertBox("Error",
						"Could not load style: " + aCaught.getMessage());
				alertBox.show();
			}
		};

		hippieGen.loadStyle(aStyleName, listener);
	}

	private Panel createStatusBar() {
		HorizontalPanel statusPanel = new HorizontalPanel();
		statusPanel.setSpacing(5);
		statusPanel.setHeight("20");

		Label lbVersion = new Label("Version: " + VERSION_STRING);
		statusPanel.add(lbVersion);
		statusPanel.setCellVerticalAlignment(lbVersion,
				HasVerticalAlignment.ALIGN_MIDDLE);

		Label lbImpressum = new Label();
		lbImpressum.setText(" + + + Contact: hippie.ipsum@gmail.com");
		statusPanel.add(lbImpressum);
		statusPanel.setCellVerticalAlignment(lbImpressum,
				HasVerticalAlignment.ALIGN_MIDDLE);
		statusPanel.setCellHorizontalAlignment(lbImpressum,
				HasHorizontalAlignment.ALIGN_RIGHT);

		return statusPanel;
	}

	private Panel createMenuBar() {
		SimplePanel sp = new SimplePanel();

		MenuBar mainMenu = new MenuBar(false);
		sp.add(mainMenu);

		MenuItem miThemes = createThemesMenu();
		mainMenu.addItem(miThemes);

		MenuItem miHelp = createHelpMenu();
		mainMenu.addItem(miHelp);
		return sp;
	}

	private Panel createCenterPanel() {
		HorizontalPanel centerPanel = new HorizontalPanel();
		centerPanel.setSize("800px", "576px");

		// wordListPanel = new WordListPanel();
		// wordListPanel.setStyleName("gwt-StackPanel");
		// wordListPanel.setSize("30%", "100%");

		WordCloud wordCloud = new WordCloud();
		wordCloud.setSize("30%", "100%");

		generatorPanel = new TextGeneratorPanel();
		generatorPanel.setBorderWidth(0);
		generatorPanel.setSize("70%", "100%");

		centerPanel.setCellHeight(wordCloud, "100%");
		centerPanel.setCellWidth(wordCloud, "300");
		centerPanel.add(wordCloud);

		centerPanel.setCellHeight(generatorPanel, "100%");
		centerPanel.setCellWidth(generatorPanel, "100%");
		centerPanel.add(generatorPanel);

		return centerPanel;

	}

	private Panel createThemeInfoPanel() {
		HorizontalPanel themeInfoPanel = new HorizontalPanel();
		themeInfoPanel.setSpacing(5);
		themeInfoPanel.setSize("100%", "85px");

		styleDesription = new InlineHTML();
		styleDesription.setHTML("<h2>Info panel</h2>\r\nSome description");
		themeInfoPanel.add(styleDesription);
		styleDesription.setHeight("100%");
		return themeInfoPanel;
	}

	private MenuItem createThemesMenu() {
		MenuBar menuBar = new MenuBar(true);
		menuBar.setAutoOpen(true);
		MenuItem menuItem = new MenuItem("Themes", false, menuBar);

		styleMenuBar = new MenuBar(true);
		styleMenuBar.setAutoOpen(false);

		menuBar.addItem("Select", styleMenuBar);

		MenuItem miSave = new MenuItem("Save", false, new Command() {

			@Override
			public void execute() {
				saveStyle();
			}
		});

		menuBar.addItem(miSave);

		MenuItem miCreate = new MenuItem("New", false, new Command() {

			@Override
			public void execute() {
				Map<WordTypes, WordList> map = new HashMap<WordTypes, WordList>();

				Style style = new Style("Neuer Stil", "Please add description",
						map);

				newStyle(style);
			}
		});

		menuBar.addItem(miCreate);

		MenuItem miExport = new MenuItem("Import/Export", false, new Command() {

			@Override
			public void execute() {
				if (currentStyle == null) {
					currentStyle = new Style();
				}
				ImportExportDialog importExportDialog = new ImportExportDialog(
						currentStyle);
				importExportDialog.show();
			}
		});

		menuBar.addItem(miExport);

		return menuItem;
	}

	protected void newStyle(Style aStyle) {
		// wordListPanel.reparseWordLists();
		final WaitBox box = new WaitBox("Please Wait", "Saving data ...");
		box.show();

		TypedListener<Boolean> listener = new TypedListener<Boolean>() {

			@Override
			public void notifyMe(Boolean aSuccess) {
				NotificationManager.getInstance().fireStyleEvent(
						new StyleEvent(currentStyle, StyleAction.SAVED));
				box.hide();
			}

			@Override
			public void notifyFail(Throwable aCaught) {
				box.hide();
				AlertBox alertBox = new AlertBox("Error",
						"Could not save theme: " + aCaught.getMessage());
				alertBox.show();
			}
		};
		hippieGen.newStyle(aStyle, listener);

	}

	private MenuItem createHelpMenu() {
		MenuBar menuBar = new MenuBar(true);
		MenuItem menuItem = new MenuItem("Help", false, menuBar);

		MenuItem mntmAbout = new MenuItem("About", false, new Command() {
			@Override
			public void execute() {
				new AboutDialog();
			}
		});
		menuBar.addItem(mntmAbout);
		return menuItem;
	}

	private void saveStyle() {
		// wordListPanel.reparseWordLists();
		final WaitBox box = new WaitBox("Please Wait", "Saving data ...");
		box.show();

		TypedListener<Boolean> listener = new TypedListener<Boolean>() {

			@Override
			public void notifyMe(Boolean aSuccess) {
				NotificationManager.getInstance().fireStyleEvent(
						new StyleEvent(currentStyle, StyleAction.SAVED));
				box.hide();
			}

			@Override
			public void notifyFail(Throwable aCaught) {
				box.hide();
				AlertBox alertBox = new AlertBox("Error",
						"Could not save theme: " + aCaught.getMessage());
				alertBox.show();
			}
		};
		hippieGen.saveStyle(currentStyle, listener);

	}

}
