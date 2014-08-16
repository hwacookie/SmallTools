/* --------------------------------------------------------------------------
 * @author Hauke Walden
 * @created 28.06.2011 
 * Copyright 2011 by Hauke Walden 
 * All rights reserved.
 * --------------------------------------------------------------------------
 */
package de.mbaaba.tool;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
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

import de.mbaaba.tool.pw.data.WorktimeEntry;
import de.mbaaba.tool.pw.data.WorktimeEntryUtils;
import de.mbaaba.util.ConfigManager;
import de.mbaaba.util.Configurator;
import de.mbaaba.util.Units;

public class PresenceWatcher {

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
		shell.pack();

		ConfigManager.getInstance().registerConfigListener(new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				readConfig();
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

		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(true).spacing(0, 0).margins(new Point(0, 0)).applyTo(composite);

		lbDailyBalance = new BalanceLabel(composite, SWT.NONE);
		createTooltip(lbDailyBalance);
		lbDailyBalance.setStartDate(null);

		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).align(SWT.CENTER, SWT.CENTER).applyTo(lbDailyBalance);

		lbMonthlyBalance = new BalanceLabel(composite, SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).align(SWT.CENTER, SWT.CENTER).applyTo(lbMonthlyBalance);
		createTooltip(lbMonthlyBalance);
		Calendar cal = new GregorianCalendar();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		lbMonthlyBalance.setStartDate(cal.getTime());

		composite.pack();

		trayItemView = new TrayItemView(display);

		Menu popupMenu = createPopup();

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
		miShellVisible.setSelection(shell.isVisible());

		setRemindOnBreakes(configurator.getProperty(ConfigManager.CFG_REMIND_ON_BREAKS, true));

		remindOnOvertime = (configurator.getProperty(ConfigManager.CFG_REMIND_ON_EOW, true));

		loadShellPos();

		activityDetector = new ScreenLockActivityDetector();

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
				HistoryViewer.openViewer();
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
				ConfigEditor configEditor = new ConfigEditor(null);
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
							openBreakInfo("Päuschen", "Hachja, *jetzt* ne schöne Tasse Kaffee ... das wär's, oder?",
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
							openBreakInfo("Pause", "Und jetzt: Wie wär's denn mit einem kleinen Spaziergang?",
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
									"Also, so an und für sich *könnte* man jetzt ja auch mal nach Hause gehen ...", 60 * 60 * 3,
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

	// protected String calcMonthBalance() {
	// if ((lblBalanceOut != null) && (lblBalanceOut.isVisible()) {
	// Calendar cal = new GregorianCalendar();
	// Date date = new Date();
	// cal.setTime(date);
	// List<WorktimeEntry> list = new ArrayList<WorktimeEntry>();
	// int day = 0;
	// int currentMonth = cal.get(Calendar.MONTH);
	// while (cal.get(Calendar.MONTH) == currentMonth) {
	// day++;
	// cal.set(Calendar.DAY_OF_MONTH, day);
	// if (cal.get(Calendar.MONTH) == currentMonth) {
	// Date thisDate = cal.getTime();
	// list.add(dataStorage.getWorktimeEntry(thisDate));
	// }
	// }
	//
	// // calc balance
	// int balance = 0;
	// Date yesterday = new Date(System.currentTimeMillis() - Units.DAY);
	// for (WorktimeEntry worktimeEntry : list) {
	// int time = WorktimeEntryUtils.getNetWorktimeInMinutes(worktimeEntry);
	// if ((worktimeEntry.getDate().before(yesterday))) {
	// balance += (time - worktimeEntry.getPlanned());
	// }
	// }
	//
	// String formatMinutes = WorktimeEntryUtils.formatMinutes(balance);
	// lblBalanceOut.setText(formatMinutes);
	//
	// if (balance >= 0) {
	// lblBalanceOut.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GREEN));
	// } else {
	// lblBalanceOut.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
	// }
	// }
	// }

}
