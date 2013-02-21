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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

import de.mbaaba.tool.HereIAm.Activity;
import de.mbaaba.util.Configurator;
import de.mbaaba.util.PropertyFileConfigurator;
import de.mbaaba.util.Units;

public class PresenceWatcher implements PresenceListener {

	private static final String SHELL_VISIBLE = "shell.visible";

	private static final String SHELL_Y_POS = "shell.y";

	private static final String SHELL_X_POS = "shell.x";

	private static final String TIME_FORMAT = "HH':'mm':'ss";

	private Display display;

	private Shell shell;

	private Composite composite;

	private Label lbStatus;

	protected de.mbaaba.tool.DisplayMode displayMode;

	private HereIAm presenceWatcher;

	protected Point movingOffset;

	protected static final long WORK_TIME_PER_DAY = 8 * Units.HOUR + 45 * Units.MINUTE;

	private final Configurator configurator;

	private Image iconGreen;

	private Image iconRed;

	private TrayItem trayItem;

	private Menu popupMenu;

	private MenuItem miShellVisible;

	public PresenceWatcher(Configurator aConfigurator) throws IOException, InterruptedException {
		configurator = aConfigurator;
		displayMode = DisplayMode.TIME_LEFT;
		createGui();
		loadShellPos();
		shell.pack();
		loadImages();
		presenceWatcher = new HereIAm(aConfigurator, this);

	}

	private void createTrayItem() {

		final Tray tray = display.getSystemTray();
		if (tray == null) {
			System.out.println("The system tray is not available");
		} else {
			trayItem = new TrayItem(tray, SWT.NONE);
			//trayItem.setToolTipText("Presence Watcher - IDLE");
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
		shell = new Shell(display, SWT.ON_TOP);
		shell.setText("Shell");
		shell.setSize(130, 16);
		shell.setLocation(70, 3);

		createTrayItem();

		composite = new Composite(shell, SWT.NONE);
		GridLayout gridLayout = new GridLayout(1, false);
		composite.setLayout(gridLayout);

		lbStatus = new Label(composite, SWT.NONE);
		lbStatus.setText("+00:00:00");

		GridData gridData = new GridData(SWT.FILL);
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		lbStatus.setLayoutData(gridData);

		lbStatus.setMenu(popupMenu);
		composite.setMenu(popupMenu);
		shell.setMenu(popupMenu);

		composite.pack();
		shell.pack();
		shell.open();

		setColorScheme(SWT.COLOR_RED);

		lbStatus.addMouseMoveListener(new MouseMoveListener() {
			public void mouseMove(MouseEvent e) {
				if (movingOffset != null) {
					Point newShellPos = lbStatus.toDisplay(e.x - movingOffset.x, e.y - movingOffset.y);
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

		lbStatus.addMouseListener(mouseAdapter);
		String shellVisible = configurator.getProperty(SHELL_VISIBLE, Boolean.TRUE.toString());
		shell.setVisible(Boolean.valueOf(shellVisible));
		miShellVisible.setSelection(shell.isVisible());

	}

	private Menu createPopup() {
		popupMenu = new Menu(shell, SWT.POP_UP);

		createDisplayModeMenuItem(popupMenu, DisplayMode.TIME_PASSED);
		createDisplayModeMenuItem(popupMenu, DisplayMode.IDLE_SINCE);
		createDisplayModeMenuItem(popupMenu, DisplayMode.TIME_LEFT);
		createDisplayModeMenuItem(popupMenu, DisplayMode.TIME_STARTED);
		createDisplayModeMenuItem(popupMenu, DisplayMode.TIME_FINISHED);

		new MenuItem(popupMenu, SWT.SEPARATOR);
		MenuItem itemSetStart = new MenuItem(popupMenu, SWT.PUSH);
		itemSetStart.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent aE) {
				setStartTime();
			}

		});
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

		itemSetStart.setText("Startzeit setzen");
		new MenuItem(popupMenu, SWT.SEPARATOR);
		MenuItem itemExit = new MenuItem(popupMenu, SWT.PUSH);
		itemExit.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent aE) {
				System.exit(1);
			}

		});
		itemExit.setText("Exit");

		return popupMenu;
	}

	private void createDisplayModeMenuItem(Menu menu, final DisplayMode dm) {
		MenuItem item = new MenuItem(menu, SWT.RADIO);
		item.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent aE) {
				displayMode = dm;
			}

		});
		item.setText(dm.getMsg()+" ...");
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
					} else {
						setTrayImage(iconGreen);
						fg = SWT.COLOR_BLACK;
					}
					composite.setBackground(getDisplay().getSystemColor(aColor));
					lbStatus.setBackground(getDisplay().getSystemColor(aColor));
					lbStatus.setForeground(getDisplay().getSystemColor(fg));

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

	protected void setStartTime() {
		final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		IInputValidator validator = new IInputValidator() {

			public String isValid(String aArg0) {
				try {
					sdf.parse(aArg0);
					return null;
				} catch (ParseException e) {
					return "Ungültiges Datumsformat";
				}
			}
		};
		InputDialog inputDialog = new InputDialog(shell, "Setze Startzeit", "Startzeit manuell setzen", sdf.format(presenceWatcher.getStartToday()), validator);
		int ret = inputDialog.open();
		if (ret == InputDialog.OK) {
			String h = inputDialog.getValue().substring(0, inputDialog.getValue().indexOf(":")).trim();
			String m = inputDialog.getValue().substring(inputDialog.getValue().indexOf(":") + 1).trim();
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(h));
			calendar.set(Calendar.MINUTE, Integer.parseInt(m));
			calendar.set(Calendar.SECOND, 0);
			long newTime = calendar.getTimeInMillis();
			presenceWatcher.setStartTime(newTime);
		}

	}

	public void statusChange(final Activity aActivity) {
//		shell.getDisplay().asyncExec(new Runnable() {
//			public void run() {
//				trayItem.setToolTipText("Presence Watcher - " + aActivity);
//			}
//		});
	}

	public void timeChange() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		String temp = "";
		long val;
		switch (displayMode) {
		case TIME_LEFT:

			if (presenceWatcher.getActivity() == Activity.IDLE) {
				val = presenceWatcher.getEndToday() - presenceWatcher.getLastActivity();
			} else {
				val = presenceWatcher.getEndToday() - System.currentTimeMillis();
			}
			if (val < 0) {
				temp = "+" + DurationFormatUtils.formatDuration(-val, TIME_FORMAT);
			} else {
				temp = "-" + DurationFormatUtils.formatDuration(val, TIME_FORMAT);
			}
			break;
		case IDLE_SINCE:
			temp = sdf.format(presenceWatcher.getLastActivity());
			break;

		case TIME_PASSED:
			if (presenceWatcher.getActivity() == Activity.IDLE) {
				val = (presenceWatcher.getLastActivity() - presenceWatcher.getStartToday());
			} else {
				val = (System.currentTimeMillis() - presenceWatcher.getStartToday());
			}
			temp = DurationFormatUtils.formatDuration(val, TIME_FORMAT);
			break;

		case TIME_STARTED:
			temp = sdf.format(new Date(presenceWatcher.getStartToday()));
			break;

		case TIME_FINISHED:
			temp = sdf.format(presenceWatcher.getEndToday());
			break;

		default:
			break;
		}
		
		display = getDisplay();
		if (display != null) {
			final String newStatusString = temp;
			getDisplay().asyncExec(new Runnable() {

				public void run() {
					trayItem.setToolTipText(displayMode.getMsg()+": "+newStatusString);
					lbStatus.setText(newStatusString);
					if (presenceWatcher.getEndToday() - System.currentTimeMillis() < 0) {
						// did my work today
						setColorScheme(SWT.COLOR_GREEN);
					} else {
						// not yet done
						setColorScheme(SWT.COLOR_RED);
					}
				}
			});
		}

	}

	public static void main(String[] args) throws InterruptedException, IOException {
		String home = System.getProperty("user.home");
		File homeFile = new File(home + "/.presenceWatcher");
		homeFile.mkdirs();
		File appdata = new File(homeFile, "PresenceWatcher.properties");

		Configurator configurator = new PropertyFileConfigurator(appdata.getCanonicalPath());
		PresenceWatcher hereIAmDisplay = new PresenceWatcher(configurator);
		hereIAmDisplay.run();

	}

	private void run() {
		while (!shell.isDisposed()) {
			if (!getDisplay().readAndDispatch()) {
				getDisplay().sleep();
			}
		}
	}

}
