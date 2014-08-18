package de.mbaaba.tool;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.wb.swt.SWTResourceManager;

import de.mbaaba.tool.pw.data.WorktimeEntry;
import de.mbaaba.tool.pw.data.WorktimeEntryUtils;
import de.mbaaba.util.Units;

public class BalanceLabel extends Composite {

	public static Color PINK = SWTResourceManager.getColor(255, 192, 203);
	public static Color LIGHTGREEN = SWTResourceManager.getColor(144, 238, 144);

	private Label lbBalance;
	protected Point movingOffset;

	private DataStorageManager dataStorage = DataStorageManager.getInstance();
	private Date startDate;
	protected Integer lastDayForBreakDetection;

	private int balance;

	public BalanceLabel(Composite aParent, int aStyle) {
		super(aParent, aStyle);
		startDate = null;
		initGui();
		createUpdateTimer();
	}

	private void initGui() {
		// FillLayout layout = new FillLayout(SWT.HORIZONTAL);
		// layout.marginHeight = 2;
		// layout.marginWidth = 3;

		GridLayoutFactory.fillDefaults().margins(new Point(3, 3)).applyTo(this);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginBottom = 3;
		gridLayout.marginTop = 3;
		gridLayout.marginRight = 3;
		gridLayout.marginLeft = 3;
		gridLayout.marginHeight = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		setLayout(gridLayout);

		lbBalance = new Label(this, SWT.NONE);
		lbBalance.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		lbBalance.setAlignment(SWT.CENTER);
		lbBalance.setText("-100:00");

		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).applyTo(lbBalance);

		// GridData gridData = new GridData(SWT.FILL);
		// gridData.grabExcessHorizontalSpace = true;
		// gridData.grabExcessVerticalSpace = true;
		// lbBalance.setLayoutData(gridData);

		lbBalance.addMouseMoveListener(new MouseMoveListener() {

			public void mouseMove(MouseEvent e) {
				if (movingOffset != null) {
					// Point newShellPos = lbBalance.toDisplay(e.x -
					// movingOffset.x, e.y - movingOffset.y);
					Point newShellPos = lbBalance.toDisplay(e.x, e.y);
					getShell().setLocation(newShellPos);
				}
			}
		});

		MouseAdapter mouseAdapter = new MouseAdapter() {

			@Override
			public void mouseDown(MouseEvent aE) {
				if (aE.button == 1) {
					Point posOflabelInShell = new Point(0, 0);
					movingOffset = new Point(aE.x + posOflabelInShell.x, aE.y + posOflabelInShell.y);
				}
			}

			@Override
			public void mouseUp(MouseEvent aE) {
				movingOffset = null;
			}

		};
		lbBalance.addMouseListener(mouseAdapter);
	}
	
	
	public Control getControl() {
		return lbBalance;
	}

	private void createUpdateTimer() {
		Timer t = new Timer(false);
		TimerTask tt = new TimerTask() {
			@Override
			public void run() {
				timeChange();
			}
		};
		t.scheduleAtFixedRate(tt, 20, Units.MINUTE);
	}

	private void timeChange() {
		Display display = getDisplay();
		if (display != null) {
			display.asyncExec(new Runnable() {

				public void run() {

					WorktimeEntry todaysWorktimeEntry = dataStorage.getTodaysWorktimeEntry();
					long currentTimeMillis = System.currentTimeMillis();
					if (startDate == null) {
						// assume current day
						setBalance(WorktimeEntryUtils.calculatePlannedBalance(todaysWorktimeEntry, currentTimeMillis));

					} else {
						Calendar cal = new GregorianCalendar();
						cal.set(Calendar.HOUR_OF_DAY, 23);
						cal.set(Calendar.MINUTE, 59);
						Date endOfToday = cal.getTime();
						setBalance(WorktimeEntryUtils.calcMultiDayBalance(startDate, endOfToday));
					}

					if ((WorktimeEntryUtils.isInShortBreak(todaysWorktimeEntry, currentTimeMillis) || (WorktimeEntryUtils
							.isInLongBreak(todaysWorktimeEntry, currentTimeMillis)))) {
						lbBalance.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.ITALIC));
					} else {
						lbBalance.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
					}

				}
			});
		}

	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
		timeChange();
	}

	@Override
	public void setMenu(Menu menu) {
		super.setMenu(menu);
		lbBalance.setMenu(menu);
	}

	@Override
	public void setBackground(Color color) {
		super.setBackground(color);
		lbBalance.setBackground(color);
	}

	public int getBalance() {
		return balance;
	}

	public void setBalance(int balance) {
		this.balance = balance;
		lbBalance.setText(WorktimeEntryUtils.formatMinutes(balance));
		if (balance < 0) {
			// not yet done
			setBackground(PINK);
		} else {
			// did my work today
			setBackground(LIGHTGREEN);
		}

	}
	

}
