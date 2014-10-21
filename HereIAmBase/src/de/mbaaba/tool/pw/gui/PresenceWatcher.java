/* --------------------------------------------------------------------------
 * @author Hauke Walden
 * @created 28.06.2011 
 * Copyright 2011 by Hauke Walden 
 * All rights reserved.
 * --------------------------------------------------------------------------
 */
package de.mbaaba.tool.pw.gui;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import de.mbaaba.tool.pw.DataStorageManager;
import de.mbaaba.tool.pw.FancyJFaceTooltip;
import de.mbaaba.tool.pw.data.WorktimeEntry;
import de.mbaaba.tool.pw.data.WorktimeEntryUtils;
import de.mbaaba.tool.pw.detectors.AbstractActivityDetector;
import de.mbaaba.tool.pw.detectors.MouseMoveActivityDetector;
import de.mbaaba.util.ConfigManager;
import de.mbaaba.util.Configurator;
import de.mbaaba.util.Units;

@SuppressWarnings("unused")
public class PresenceWatcher {

	static {
		PropertyConfigurator.configure("etc/log4j.properties");
	}

	/**
	 * The logger.
	 */
	private static final Logger LOG = Logger.getLogger(PresenceWatcher.class);

	private Display display;

	private Shell shell;

	private Composite composite;

	private BalanceLabel lbDailyBalance;

	private BalanceLabel lbMonthlyBalance;

	private final Configurator configurator = ConfigManager.getInstance();

	private MenuItem miShellVisible;

	private DataStorageManager dataStorage = DataStorageManager.getInstance();

	private boolean remindOnBreakes = true;

	private boolean remindOnOvertime = true;

	private boolean shortBreakDetected;

	private boolean longBreakDetected;

	private Integer lastDayForBreakDetection;

	private MenuItem miCreateReminder;

	private Point dpi;

	private boolean endOfPlanDetected;

	private TrayItemView trayItemView;

	private AbstractActivityDetector activityDetector;

	public PresenceWatcher() throws IOException, InterruptedException {
		createGui();

		ConfigManager.getInstance().registerConfigListener(new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				readConfig();
				activityDetector.startDetection();
			}
		});

		createUpdateTimer();

	}

	private void createUpdateTimer() {
		Timer t = new Timer(false);
		TimerTask tt = new TimerTask() {
			@Override
			public void run() {
				timeChange();
			}
		};
		t.scheduleAtFixedRate(tt, 20, Units.SECOND);
	}

	private void createGui() {
		display = new Display();

		dpi = display.getDPI();

		shell = new Shell(display, SWT.ON_TOP);
		shell.setText("Shell");
		shell.setLocation(70, 2);

		composite = new Composite(shell, SWT.NONE);
		// // GridLayout gridLayout = new GridLayout(2, false);
		// // gridLayout.marginWidth = 0;
		// // composite.setLayout(gridLayout);
		// RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL);
		// rowLayout.marginWidth=0;
		// rowLayout.marginHeight=0;
		// rowLayout.marginTop=0;
		// rowLayout.marginBottom=0;
		// rowLayout.marginLeft=0;
		// rowLayout.marginRight=0;
		shell.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent arg0) {
				AbstractActivityDetector.saveTimestamp();
			}
		});
		// Runtime.getRuntime().addShutdownHook(new Thread() {
		// @Override
		// public void run() {
		// activityDetector.stopDetection();
		// }
		// });

		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(true).spacing(0, 0).margins(new Point(0, 0)).applyTo(composite);

		lbDailyBalance = new BalanceLabel(composite, SWT.NONE);
		createTooltip(lbDailyBalance.getControl());
		lbDailyBalance.setStartDate(null);

		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).align(SWT.CENTER, SWT.CENTER).applyTo(lbDailyBalance);

		lbMonthlyBalance = new BalanceLabel(composite, SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).align(SWT.CENTER, SWT.CENTER).applyTo(lbMonthlyBalance);
		createTooltip(lbMonthlyBalance.getControl());
		Calendar cal = new GregorianCalendar();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		lbMonthlyBalance.setStartDate(cal.getTime());

		trayItemView = new TrayItemView(display);

		Menu popupMenu = createPopup();
		composite.pack();

		lbDailyBalance.setMenu(popupMenu);
		lbMonthlyBalance.setMenu(popupMenu);
		composite.setMenu(popupMenu);
		shell.setMenu(popupMenu);
		trayItemView.setPopupMenu(popupMenu);

		shell.pack();
		shell.open();

	}

	protected void readConfig() {

		boolean shellVisible = configurator.getProperty(ConfigManager.CFG_SHOW_DISPLAY, true);
		shell.setVisible(Boolean.valueOf(shellVisible));
		shell.setFocus();
		miShellVisible.setSelection(shell.isVisible());

		setRemindOnBreakes(configurator.getProperty(ConfigManager.CFG_REMIND_ON_BREAKS, true));

		remindOnOvertime = (configurator.getProperty(ConfigManager.CFG_REMIND_ON_EOW, true));

		loadShellPos();

		activityDetector = new MouseMoveActivityDetector(AbstractActivityDetector.MIN_ACTIVITY_TIME,
				AbstractActivityDetector.INACTIVITY_TIME);

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

		myTooltipLabel.setShift(new Point(10, 10));
		myTooltipLabel.setHideOnMouseDown(false);
		myTooltipLabel.activate();
	}

	private Menu createPopup() {
		Menu popupMenu = new Menu(shell, SWT.POP_UP);

		miShellVisible = new MenuItem(popupMenu, SWT.CHECK);
		miShellVisible.setText("Zeige Display");
		miShellVisible.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				configurator.setProperty(ConfigManager.CFG_SHOW_DISPLAY, miShellVisible.getSelection());
			}
		});

		MenuItem miShowHistory = new MenuItem(popupMenu, SWT.PUSH);
		miShowHistory.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent aE) {
				HistoryViewerDialog.openViewer();
				lbDailyBalance.setStartDate(null);
				Calendar cal = new GregorianCalendar();
				cal.set(Calendar.DAY_OF_MONTH, 1);
				lbMonthlyBalance.setStartDate(cal.getTime());
			}

		});
		miShowHistory.setText("Zeige Historie");
		new MenuItem(popupMenu, SWT.SEPARATOR);

		miCreateReminder = new MenuItem(popupMenu, SWT.PUSH);
		miCreateReminder.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent aE) {
				CreateAlarmDialog createAlarmDialog = new CreateAlarmDialog(null);
				createAlarmDialog.open();
			}

		});
		miCreateReminder.setText("Erinnerung anlegen");

		new MenuItem(popupMenu, SWT.SEPARATOR);
		MenuItem itemConfig = new MenuItem(popupMenu, SWT.PUSH);
		itemConfig.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent aE) {
				ConfigEditorDialog configEditor = new ConfigEditorDialog(null);
				configEditor.open();
			}

		});
		itemConfig.setText("Einstellungen");
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
		itemExit.setText("Beenden");
		return popupMenu;
	}

	private void setRemindOnBreakes(boolean selection) {
		if (selection) {
			lastDayForBreakDetection = null;
			shortBreakDetected = false;
			longBreakDetected = false;
			endOfPlanDetected = false;
		}
	}

	private void timeChange() {
		dayChangeDetection();
		breakTimeDetection();

		display = getDisplay();
		if (display != null) {
			display.asyncExec(new Runnable() {

				public void run() {
					trayItemView.setToolTipText(calculateStatusMessage(false));
				}
			});
		}

	}

	private void dayChangeDetection() {
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
			cal.set(Calendar.DAY_OF_MONTH, 1);
			lbMonthlyBalance.setStartDate(cal.getTime());
		}
	}

	private boolean breakTimeDetection() {
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
							openBreakInfo("P�uschen", "Hachja, *jetzt* ne sch�ne Tasse Kaffee ... das w�r's, oder?",
									WorktimeEntryUtils.SHORT_BREAK_LENGTH * 60, BreakInfoDialog.IMG_COFFEE);
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
							openBreakInfo("Pause", "Und jetzt: Wie w�r's denn mit einem kleinen Spaziergang?",
									WorktimeEntryUtils.LONG_BREAK_LENGTH * 60, BreakInfoDialog.IMG_WALK);
						}
					});
				}
			}
		}
		if (remindOnOvertime) {
			if (WorktimeEntryUtils.getNetWorktimeInMinutes(todaysWorktimeEntry) > todaysWorktimeEntry.getPlanned()) {
				if (!endOfPlanDetected) {
					endOfPlanDetected = true;
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							openBreakInfo("Feierabend!",
									"Also, so an und f�r sich *k�nnte* man jetzt ja auch mal nach Hause gehen ...", 60 * 60 * 3,
									BreakInfoDialog.IMG_HOME);
						}
					});
				}
			}
		}
		return temp;
	}

	protected void openBreakInfo(String aTitle, String aMessage, int aNumSeconds, String aImageName) {
		BreakInfoDialog breakInfoDialog = new BreakInfoDialog(null);
		breakInfoDialog.openWithData(aTitle, aMessage, aNumSeconds, aImageName);
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
		if (todaysWorktimeEntry.getStartTime() == null) {
			return "Du arbeitest noch gar nicht. Geh wieder in's Bett, Geist!";
		}

		int breakMinutes = WorktimeEntryUtils
				.getBreaktimeMinutes(todaysWorktimeEntry.getDate(), todaysWorktimeEntry.getPlanned());
		Date possibleEndDate = new Date(todaysWorktimeEntry.getStartTime().getTime()
				+ (todaysWorktimeEntry.getPlanned() + breakMinutes) * Units.MINUTE);

		String timeToStayString = WorktimeEntryUtils.formatMinutes(WorktimeEntryUtils.calculatePlannedBalance(
				todaysWorktimeEntry, System.currentTimeMillis()));
		String statusMessage;

		if (aLongVersion) {
			if (timeToStayString.startsWith("-")) {
				statusMessage = "Du bist um " + WorktimeEntryUtils.TIME_ONLY.format(todaysWorktimeEntry.getStartTime())
						+ " gekommen,\n" + "wolltest heute eigentlich "
						+ WorktimeEntryUtils.formatMinutes(todaysWorktimeEntry.getPlanned()) + " arbeiten,\n"
						+ "hast davon schon "
						+ (WorktimeEntryUtils.formatMinutes(WorktimeEntryUtils.getNetWorktimeInMinutes(todaysWorktimeEntry)))
						+ " hinter dir,\n" + "und kannst demnach inklusive einer Pause von\n"
						+ WorktimeEntryUtils.formatMinutes(breakMinutes) + " um "
						+ WorktimeEntryUtils.TIME_ONLY.format(possibleEndDate) + " gehen, " + "also in "
						+ timeToStayString.substring(1) + " Std/Minuten.";

			} else {
				statusMessage = "Du bist um " + WorktimeEntryUtils.TIME_ONLY.format(todaysWorktimeEntry.getStartTime())
						+ " gekommen,\nmusstest heute " + WorktimeEntryUtils.formatMinutes(todaysWorktimeEntry.getPlanned())
						+ " arbeiten,\nwarst tats�chlich aber sogar "
						+ (WorktimeEntryUtils.formatMinutes(WorktimeEntryUtils.getNetWorktimeInMinutes(todaysWorktimeEntry)))
						+ " lang fleissig,\nund konntest demnach inklusive\neiner Pause von "
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

	private void saveShellPos() {
		configurator.setProperty(ConfigManager.CFG_SHELL_X_POS, shell.getLocation().x);
		configurator.setProperty(ConfigManager.CFG_SHELL_Y_POS, shell.getLocation().y);
	}

	private void loadShellPos() {
		int savedXPos = (int) configurator.getProperty(ConfigManager.CFG_SHELL_X_POS, 950);
		int savedYPos = (int) configurator.getProperty(ConfigManager.CFG_SHELL_Y_POS, 3);
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

	private Display getDisplay() {
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

}
