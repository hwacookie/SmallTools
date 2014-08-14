/* --------------------------------------------------------------------------
 * @author Hauke Walden
 * @created 28.06.2011 
 * Copyright 2011 by Hauke Walden 
 * All rights reserved.
 * --------------------------------------------------------------------------
 */
package de.mbaaba.tool;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

import de.mbaaba.tool.HereIAm.Activity;
import de.mbaaba.tool.pw.data.WorktimeEntry;
import de.mbaaba.tool.pw.data.WorktimeEntryUtils;
import de.mbaaba.util.Configurator;
import de.mbaaba.util.PropertyFileConfigurator;
import de.mbaaba.util.Units;

public class PresenceWatcher implements PresenceListener {

	private static final String SHELL_VISIBLE = "shell.visible";

	private static final String SHELL_Y_POS = "shell.y";

	private static final String SHELL_X_POS = "shell.x";

	private Display display;

	private Shell shell;

	private Composite composite;

	private Label lbTimeDisplay;

	protected Point movingOffset;

	protected static final long WORK_TIME_PER_DAY = 8 * Units.HOUR + 45 * Units.MINUTE;

	private final Configurator configurator;

	private Image iconGreen;

	private Image iconRed;

	private TrayItem trayItem;

	private Menu popupMenu;

	private MenuItem miShellVisible;

	private DataStorageManager dataStorage = DataStorageManager.getInstance();

	private boolean remindOnBreakes = true;

	private boolean shortBreakDetected;

	private boolean longBreakDetected;

	private Integer lastDayForBreakDetection;

	private MenuItem miCreateReminder;

	private Point dpi;

	private Image iconYellow;

	private boolean endOfPlanDetected;

	public PresenceWatcher() throws IOException, InterruptedException {
		String home = System.getProperty("user.home");
		File homeFile = new File(home + "/.presenceWatcher");
		homeFile.mkdirs();
		File appdata = new File(homeFile, "PresenceWatcher.properties");
		configurator = new PropertyFileConfigurator(appdata.getCanonicalPath());

		Thread hook = new Thread() {
			@Override
			public void run() {
				super.run();
				configurator.saveProperties();

			}
		};
		Runtime.getRuntime().addShutdownHook(hook);

		createGui();
		loadShellPos();
		shell.pack();
		loadImages();
		new HereIAm(this);
	}

	private void createTrayItem() {

		final Tray tray = display.getSystemTray();
		if (tray == null) {
			System.out.println("The system tray is not available");
		} else {
			trayItem = new TrayItem(tray, SWT.NONE);
			trayItem.addListener(SWT.Show, new Listener() {
				public void handleEvent(Event event) {
				}
			});
			trayItem.addListener(SWT.Hide, new Listener() {
				public void handleEvent(Event event) {
				}
			});
			trayItem.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
				}
			});
			trayItem.addListener(SWT.DefaultSelection, new Listener() {
				public void handleEvent(Event event) {
				}
			});

			createPopup();
			trayItem.addListener(SWT.MenuDetect, new Listener() {
				public void handleEvent(Event event) {
					popupMenu.setVisible(true);
				}
			});
			setTrayImage(iconRed);
		}

	}

	private void loadImages() {
		InputStream is = getClass().getClassLoader().getResourceAsStream("images/green.ico");
		iconGreen = new Image(display, is);
		is = getClass().getClassLoader().getResourceAsStream("images/red.ico");
		iconRed = new Image(display, is);
		is = getClass().getClassLoader().getResourceAsStream("images/yellow.ico");
		iconYellow = new Image(display, is);
	}

	private void saveShellPos() {
		configurator.setProperty(SHELL_X_POS, "" + shell.getLocation().x);
		configurator.setProperty(SHELL_Y_POS, "" + shell.getLocation().y);
	}

	private void loadShellPos() {
		int savedXPos = (int) configurator.getProperty(SHELL_X_POS, 950);
		int savedYPos = (int) configurator.getProperty(SHELL_Y_POS, 3);
		if (savedXPos < 0) {
			savedXPos = 0;
		}
		if (savedYPos < 0) {
			savedYPos = 0;
		}
		Rectangle bounds = Display.getDefault().getBounds();

		if (savedXPos > (bounds.width - 3)) {
			savedXPos = bounds.width - 3;
		}
		if (savedYPos > (bounds.height - 3)) {
			savedYPos = bounds.height - 3;
		}

		shell.setLocation(savedXPos, savedYPos);
	}

	private void createGui() {
		display = new Display();

		dpi = display.getDPI();

		shell = new Shell(display, SWT.ON_TOP);
		shell.setText("Shell");
		shell.setSize(146, 16);
		shell.setLocation(70, 2);

		createTrayItem();

		composite = new Composite(shell, SWT.NONE);
		GridLayout gridLayout = new GridLayout(2, false);
		composite.setLayout(gridLayout);

		lbTimeDisplay = new Label(composite, SWT.NONE);
		lbTimeDisplay.setText("-00:00 / 000000");

		GridData gridData = new GridData(SWT.FILL);
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		lbTimeDisplay.setLayoutData(gridData);

		lbTimeDisplay.setMenu(popupMenu);
		composite.setMenu(popupMenu);
		shell.setMenu(popupMenu);

		composite.pack();
		shell.open();

		setColorScheme(SWT.COLOR_RED);

		lbTimeDisplay.addMouseMoveListener(new MouseMoveListener() {
			public void mouseMove(MouseEvent e) {
				if (movingOffset != null) {
					Point newShellPos = lbTimeDisplay.toDisplay(e.x - movingOffset.x, e.y - movingOffset.y);
					shell.setLocation(newShellPos);
					saveShellPos();

				}
			}

		});

		MouseAdapter mouseAdapter = new MouseAdapter() {

			@Override
			public void mouseDown(MouseEvent aE) {
				if (aE.button == 1) {
					movingOffset = new Point(aE.x, aE.y);
				}
			}

			@Override
			public void mouseUp(MouseEvent aE) {
				movingOffset = null;
			}

		};

		lbTimeDisplay.addMouseListener(mouseAdapter);
		String shellVisible = configurator.getProperty(SHELL_VISIBLE, Boolean.TRUE.toString());
		shell.setVisible(Boolean.valueOf(shellVisible));
		miShellVisible.setSelection(shell.isVisible());

		createTooltip(lbTimeDisplay);

	}

	private void createTooltip(Control aControl) {

		FancyJFaceTooltip myTooltipLabel = new FancyJFaceTooltip(aControl) {

			protected Composite createContentArea(Composite parent) {
				Composite comp = super.createContentArea(parent);
				comp.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
				FillLayout layout = new FillLayout();
				layout.marginWidth = 5;
				comp.setLayout(layout);

				CLabel label = new CLabel(comp, SWT.NONE);
				String longStatusMessage = calculateStatusMessage(true);
				label.setText(longStatusMessage);
				label.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
				return comp;
			}

			@Override
			protected String getHeaderText() {
				return "Anwesenheits-Info";
			}
		};
		myTooltipLabel.setShift(new Point(-5, -5));
		myTooltipLabel.setHideOnMouseDown(false);
		myTooltipLabel.activate();
	}

	private Menu createPopup() {
		popupMenu = new Menu(shell, SWT.POP_UP);

		miShellVisible = new MenuItem(popupMenu, SWT.CHECK);
		miShellVisible.setText("Zeige Display");
		miShellVisible.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (miShellVisible.getSelection()) {
					shell.setVisible(true);
					shell.forceActive();
				} else {
					shell.setVisible(false);
				}
				configurator.setProperty(SHELL_VISIBLE, Boolean.toString(shell.isVisible()));
			}
		});
		{
			MenuItem miShowHistory = new MenuItem(popupMenu, SWT.PUSH);
			miShowHistory.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent aE) {
					HistoryViewer.openViewer();
				}

			});
			miShowHistory.setText("Zeige Historie");
		}
		new MenuItem(popupMenu, SWT.SEPARATOR);
		{
			miCreateReminder = new MenuItem(popupMenu, SWT.CHECK);
			miCreateReminder.setSelection(true);
			remindOnBreakes = true;
			miCreateReminder.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent aE) {
					setRemindOnBreakes(miCreateReminder.getSelection());
				}

			});
			miCreateReminder.setText("Erinnere an Pausen");
		}

		{
			miCreateReminder = new MenuItem(popupMenu, SWT.PUSH);
			miCreateReminder.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent aE) {
					createReminder();
				}

			});
			miCreateReminder.setText("Erinnerung anlegen");
		}

		new MenuItem(popupMenu, SWT.SEPARATOR);
		MenuItem itemExit = new MenuItem(popupMenu, SWT.PUSH);
		itemExit.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent aE) {
				boolean reallyQuit = MessageDialog.openQuestion(null, "Sicher?", "Wollen Sie wirklich beenden?");
				if (reallyQuit) {
					System.exit(1);
				}
			}

		});
		itemExit.setText("Exit");

		return popupMenu;
	}

	protected void createReminder() {
		CreateAlarmDialog createAlarmDialog = new CreateAlarmDialog(null);
		int open = createAlarmDialog.open();
		if (open == Window.OK) {

		}
	}

	protected void setRemindOnBreakes(boolean selection) {
		if (selection) {
			lastDayForBreakDetection = null;
			shortBreakDetected = false;
			longBreakDetected = false;
			endOfPlanDetected = false;
		}
		if (selection != remindOnBreakes) {
			remindOnBreakes = selection;
			configurator.setProperty("remindOnBreakes", remindOnBreakes);
		}
	}

	private void setColorScheme(final int aColor) {
		display = getDisplay();
		if (display != null) {
			getDisplay().asyncExec(new Runnable() {
				public void run() {
					int fg;
					if (aColor == SWT.COLOR_RED) {
						setTrayImage(iconRed);
						fg = SWT.COLOR_WHITE;
					} else if (aColor == SWT.COLOR_YELLOW) {
						setTrayImage(iconYellow);
						fg = SWT.COLOR_BLACK;
					} else {
						setTrayImage(iconGreen);
						fg = SWT.COLOR_BLACK;
					}
					composite.setBackground(getDisplay().getSystemColor(aColor));
					lbTimeDisplay.setBackground(getDisplay().getSystemColor(aColor));
					lbTimeDisplay.setForeground(getDisplay().getSystemColor(fg));

				}
			});
		}
	}

	protected void setTrayImage(Image aIcon) {
		if (trayItem.getImage() != aIcon) {
			trayItem.setImage(aIcon);
		}
	}

	protected Display getDisplay() {
		if ((display == null) || (display.isDisposed())) {
			if (shell.isDisposed()) {
				return null;
			}
			display = shell.getDisplay();
		}
		if (display.isDisposed()) {
			display = null;
		}
		return display;
	}

	public void timeChange() {
		Calendar cal = new GregorianCalendar();
		cal.setTime(new Date());

		if (lastDayForBreakDetection == null) {
			Calendar cal2 = new GregorianCalendar();
			cal2.setTime(new Date());
			lastDayForBreakDetection = cal2.get(Calendar.DAY_OF_MONTH);
		}

		// if a new day has started ...
		if (cal.get(Calendar.DAY_OF_MONTH) != lastDayForBreakDetection) {
			shortBreakDetected = false;
			longBreakDetected = false;
			endOfPlanDetected = false;
			lastDayForBreakDetection = null;
		}

		boolean temp = false;
		WorktimeEntry todaysWorktimeEntry = dataStorage.getTodaysWorktimeEntry();
		if (remindOnBreakes) {
			if (WorktimeEntryUtils.isInShortBreak(todaysWorktimeEntry, System.currentTimeMillis() - (Units.MINUTE * 3))) {
				temp = true;
				if (!shortBreakDetected) {
					shortBreakDetected = true;
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							openBreakInfo("Päuschen", "Hachja, *jetzt* ne schöne Tasse Kaffee ... das wär's, oder?",
									WorktimeEntryUtils.SHORT_BREAK_LENGTH * 60);
						}
					});
				}
			}
			if (WorktimeEntryUtils.isInLongBreak(todaysWorktimeEntry, System.currentTimeMillis() - (Units.MINUTE * 3))) {
				temp = true;
				if (!longBreakDetected) {
					longBreakDetected = true;
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							openBreakInfo("Pause", "Und jetzt: Wie wär's denn mit einem kleinen Spaziergang?",
									WorktimeEntryUtils.LONG_BREAK_LENGTH * 60);
						}
					});
				}
			}
			if (WorktimeEntryUtils.getNetWorktimeInMinutes(todaysWorktimeEntry) > todaysWorktimeEntry.getPlanned()) {
				if (!endOfPlanDetected) {
					endOfPlanDetected = true;
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							openBreakInfo("Feierabend!",
									"Also, so an und für sich *könnte* man jetzt ja auch mal nach Hause gehen ...", 60 * 60 * 3);
						}
					});
				}
			}
		}

		final boolean inABreak = temp;

		display = getDisplay();
		if (display != null) {
			getDisplay().asyncExec(new Runnable() {

				public void run() {
					WorktimeEntry todaysWorktimeEntry = dataStorage.getTodaysWorktimeEntry();
					double meters = ((todaysWorktimeEntry.getActivityIndicator() * 2.54 / dpi.x));
					String meterString = String.valueOf((int) meters);
					while (meterString.length() < 6) {
						meterString = "0" + meterString;
					}
					final String s = WorktimeEntryUtils.calculatePlannedBalance(todaysWorktimeEntry, System.currentTimeMillis())
							+ " / " + meterString;

					lbTimeDisplay.setText(s);
					if (inABreak) {
						setColorScheme(SWT.COLOR_YELLOW);
					} else if (s.startsWith("-")) {
						// not yet done
						setColorScheme(SWT.COLOR_RED);
					} else {
						// did my work today
						setColorScheme(SWT.COLOR_GREEN);
					}
					trayItem.setToolTipText(calculateStatusMessage(false));
				}
			});
		}

	}

	protected void openBreakInfo(String aTitle, String aMessage, int aNumSeconds) {
		BreakInfoDialog breakInfoDialog = new BreakInfoDialog(null);
		breakInfoDialog.openWithData(aTitle, aMessage, aNumSeconds);
	}

	public static void main(String[] args) throws InterruptedException, IOException {
		PresenceWatcher presenceWatcher = new PresenceWatcher();
		presenceWatcher.run();

	}

	private void run() {

		while (!shell.isDisposed()) {
			if (!getDisplay().readAndDispatch()) {
				getDisplay().sleep();
			}
		}
	}

	private String calculateStatusMessage(boolean aLongVersion) {
		WorktimeEntry todaysWorktimeEntry = dataStorage.getTodaysWorktimeEntry();

		int breakMinutes = WorktimeEntryUtils
				.getBreaktimeMinutes(todaysWorktimeEntry.getDate(), todaysWorktimeEntry.getPlanned());
		Date possibleEndDate = new Date(todaysWorktimeEntry.getStartTime().getTime()
				+ (todaysWorktimeEntry.getPlanned() + breakMinutes) * Units.MINUTE);

		String timeToStayString = WorktimeEntryUtils.calculatePlannedBalance(todaysWorktimeEntry, System.currentTimeMillis());
		String statusMessage;

		if (aLongVersion) {
			if (timeToStayString.startsWith("-")) {
				statusMessage = "Du bist um " + WorktimeEntryUtils.TIME_ONLY.format(todaysWorktimeEntry.getStartTime())
						+ " gekommen,\n" + "müsstest heute eigentlich "
						+ WorktimeEntryUtils.formatMinutes(todaysWorktimeEntry.getPlanned()) + " arbeiten,\n"
						+ "und kannst demnach inklusive einer Pause von\n" + WorktimeEntryUtils.formatMinutes(breakMinutes)
						+ " um " + WorktimeEntryUtils.TIME_ONLY.format(possibleEndDate) + " gehen, " + "also in "
						+ timeToStayString.substring(1) + " Std/Minuten.";
			} else {
				statusMessage = "Du bist um " + WorktimeEntryUtils.TIME_ONLY.format(todaysWorktimeEntry.getStartTime())
						+ " gekommen,\nmusstest heute " + WorktimeEntryUtils.formatMinutes(todaysWorktimeEntry.getPlanned())
						+ " arbeiten,\nund konntest demnach inklusive\neiner Pause von "
						+ WorktimeEntryUtils.formatMinutes(breakMinutes) + " um "
						+ WorktimeEntryUtils.TIME_ONLY.format(possibleEndDate) + " gehen,\n" + "also schon vor "
						+ timeToStayString + " Std/Minuten";
			}
		} else {
			if (timeToStayString.startsWith("-")) {
				statusMessage = "Kommen um " + WorktimeEntryUtils.TIME_ONLY.format(todaysWorktimeEntry.getStartTime()) + "\n"
						+ "Geplant " + WorktimeEntryUtils.formatMinutes(todaysWorktimeEntry.getPlanned()) + "\n" + "Pause "
						+ WorktimeEntryUtils.formatMinutes(breakMinutes) + "\n" + "Gehen um "
						+ WorktimeEntryUtils.TIME_ONLY.format(possibleEndDate) + "\n" + "in " + timeToStayString.substring(1)
						+ " Std/Minuten.";
			} else {
				statusMessage = "Kommen um " + WorktimeEntryUtils.TIME_ONLY.format(todaysWorktimeEntry.getStartTime()) + "\n"
						+ "Geplant " + WorktimeEntryUtils.formatMinutes(todaysWorktimeEntry.getPlanned()) + "\n" + "Pause "
						+ WorktimeEntryUtils.formatMinutes(breakMinutes) + "\n" + "Gehen um "
						+ WorktimeEntryUtils.TIME_ONLY.format(possibleEndDate) + "\n" + "vor " + timeToStayString
						+ " Std/Minuten.";
			}

		}
		return statusMessage;
	}

	@Override
	public void activityChange(Activity aActivity) {
		// TODO Auto-generated method stub

	}

}
