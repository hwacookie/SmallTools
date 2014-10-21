package de.mbaaba.tool.pw.gui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.wb.swt.SWTResourceManager;

import de.mbaaba.tool.pw.DataStorageManager;
import de.mbaaba.tool.pw.data.WorktimeContentProvider;
import de.mbaaba.tool.pw.data.WorktimeEntry;
import de.mbaaba.tool.pw.data.WorktimeEntryUtils;
import de.mbaaba.util.ConfigManager;

public class HistoryViewerDialog extends Dialog {
	/**
	 * The logger.
	 */
	private static final Logger LOG = Logger.getLogger(HistoryViewerDialog.class);

	private Table table;
	private TableViewer tableViewer;

	// private WorktimeEntry currentWorktimeEntry;
	private DataStorageManager dataStorage;
	private DateTime startDate;

	private WorktimeContentProvider worktimeContentProvider;
	private WorktimeLabelProvider labelProvider;
	private Label lblBalanceOut;
	private Date curStartDate;
	private Label lblWeekBalance;
	private Label lbWarning;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public HistoryViewerDialog(Shell parentShell) {
		super(parentShell);
		worktimeContentProvider = new WorktimeContentProvider();
		labelProvider = new WorktimeLabelProvider();

		dataStorage = DataStorageManager.getInstance();
		setShellStyle(getShellStyle() | SWT.RESIZE);

	}

	@Override
	protected Point getInitialLocation(Point initialSize) {
		Shell shell = this.getShell();
		Monitor primary = shell.getMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = shell.getBounds();

		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		return new Point(x, y);
	}

	@Override
	public void create() {
		super.create();
		setStartDate(new Date());
	}

	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("Zeige Historie");
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.numColumns = 2;

		startDate = new DateTime(container, SWT.CALENDAR);
		GridData gd_startDate = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
		gd_startDate.heightHint = 145;
		startDate.setLayoutData(gd_startDate);

		startDate.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				Calendar cal = new GregorianCalendar();
				cal.set(Calendar.DAY_OF_MONTH, startDate.getDay());
				cal.set(Calendar.MONTH, startDate.getMonth());
				cal.set(Calendar.YEAR, startDate.getYear());
				setStartDate(cal.getTime());
			}

		});

		tableViewer = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);

		tableViewer.getTable().setLinesVisible(true);
		tableViewer.getTable().setHeaderVisible(true);

		table = tableViewer.getTable();

		Menu popupMenu = new Menu(table);
		MenuItem miOpenEditor = new MenuItem(popupMenu, SWT.PUSH);
		miOpenEditor.setText("Werte ändern");
		SelectionAdapter selectionAdapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				openEditor();
			}
		};
		miOpenEditor.addSelectionListener(selectionAdapter);

		MenuItem miSetHoliday = new MenuItem(popupMenu, SWT.PUSH);
		miSetHoliday.setText("Urlaub");
		createCommentMI(miSetHoliday);

		MenuItem miSickDay = new MenuItem(popupMenu, SWT.PUSH);
		miSickDay.setText("Krank");
		createCommentMI(miSickDay);

		table.setMenu(popupMenu);

		table.addMouseListener(new org.eclipse.swt.events.MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				openEditor();
			}
		});

		GridData gd_table = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_table.heightHint = 143;
		table.setLayoutData(gd_table);

		TableViewerColumn tvcDate = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn colDate = tvcDate.getColumn();
		colDate.setWidth(98);
		colDate.setText("Datum");

		TableViewerColumn tvcStart = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn colStart = tvcStart.getColumn();
		colStart.setWidth(65);
		colStart.setText("Startzeit");

		TableViewerColumn tvcEnd = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn colEnd = tvcEnd.getColumn();
		colEnd.setWidth(56);
		colEnd.setText("Ende");

		TableViewerColumn tvcBrake = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn colBrake = tvcBrake.getColumn();
		colBrake.setWidth(59);
		colBrake.setText("Pause");

		TableViewerColumn tvcSum = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn colSum = tvcSum.getColumn();
		colSum.setWidth(75);
		colSum.setText("Arbeitszeit");

		TableViewerColumn tvcPlan = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn colPlan = tvcPlan.getColumn();
		colPlan.setWidth(53);
		colPlan.setText("Plan");

		TableViewerColumn tvcBalance = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn colBalance = tvcBalance.getColumn();
		colBalance.setWidth(54);
		colBalance.setText("Saldo");

		TableViewerColumn tvcComment = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn colComment = tvcComment.getColumn();
		colComment.setWidth(226);
		colComment.setText("Kommentar");
		new Label(container, SWT.NONE);

		Composite composite = new Composite(container, SWT.NONE);
		GridData gridData = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		composite.setLayoutData(gridData);
		composite.setLayout(new RowLayout(SWT.HORIZONTAL));

		lblWeekBalance = new Label(composite, SWT.NONE);
		lblWeekBalance.setForeground(SWTResourceManager.getColor(144, 238, 144));
		lblWeekBalance.setFont(SWTResourceManager.getFont("Segoe UI", 14, SWT.BOLD));
		lblWeekBalance.setText("Saldo:");

		lblBalanceOut = new Label(composite, SWT.NONE);
		lblBalanceOut.setForeground(SWTResourceManager.getColor(255, 192, 203));
		lblBalanceOut.setFont(SWTResourceManager.getFont("Segoe UI", 14, SWT.BOLD));
		lblBalanceOut.setLayoutData(new RowData(77, SWT.DEFAULT));
		lblBalanceOut.setText("1");
		new Label(container, SWT.NONE);

		lbWarning = new Label(container, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, false).align(SWT.LEFT, SWT.CENTER).minSize(500, 0).applyTo(lbWarning);
		// lbWarning
		// .setText("...............................................................................................................................................");

		Composite composite_1 = new Composite(container, SWT.NONE);
		FillLayout fl_composite_1 = new FillLayout(SWT.HORIZONTAL);
		fl_composite_1.spacing = 5;
		composite_1.setLayout(fl_composite_1);

		tableViewer.setLabelProvider(labelProvider);
		tableViewer.setContentProvider(worktimeContentProvider);

		return container;
	}

	private void createCommentMI(final MenuItem aMenuItem) {
		aMenuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();

				Iterator<WorktimeEntry> iterator = selection.iterator();

				while (iterator.hasNext()) {
					WorktimeEntry weCopy = iterator.next();
					weCopy.setComment(aMenuItem.getText());
					weCopy.setPlanned(0);
					dataStorage.saveWorktimeEntry(weCopy);
				}
				setStartDate(curStartDate);
				dataStorage.saveData();
			}
		});
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(1166, 847);
	}

	protected void setStartDate(Date aDate) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(aDate);
		curStartDate = aDate;
		List<WorktimeEntry> list = new ArrayList<WorktimeEntry>();
		int day = 0;
		int currentMonth = cal.get(Calendar.MONTH);
		while (cal.get(Calendar.MONTH) == currentMonth) {
			day++;
			cal.set(Calendar.DAY_OF_MONTH, day);
			if (cal.get(Calendar.MONTH) == currentMonth) {
				Date thisDate = cal.getTime();
				list.add(dataStorage.getWorktimeEntry(thisDate));
			}
		}

		try {
			tableViewer.setInput(list.toArray(new WorktimeEntry[list.size()]));
			tableViewer.setItemCount(list.size());
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}

		// calc balance
		int balance = 0;
		int sumPlan = 0;
		int sumCheck = 0;
		Date today = new Date(System.currentTimeMillis());
		for (WorktimeEntry worktimeEntry : list) {
			int time = WorktimeEntryUtils.getNetWorktimeInMinutes(worktimeEntry);
			if ((worktimeEntry.getDate().before(today))) {
				balance += (time - worktimeEntry.getPlanned());
			}
			sumPlan += worktimeEntry.getPlanned();
			if (!(WorktimeEntryUtils.isHoliday(worktimeEntry.getDate()) || "Urlaub".equals(worktimeEntry.getComment()) || "Krank"
					.equals(worktimeEntry.getComment()))) {
				sumCheck += ConfigManager.getInstance().getProperty(ConfigManager.CFG_DEFAULT_MINUTES, 480);
			}
		}

		lblBalanceOut.setText(WorktimeEntryUtils.formatMinutes(balance));

		if (balance >= 0) {
			lblWeekBalance.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GREEN));
			lblBalanceOut.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GREEN));
		} else {
			lblWeekBalance.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
			lblBalanceOut.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
		}

		String msg;
		if (sumCheck > sumPlan) {
			msg = "Es wurden nur " + WorktimeEntryUtils.formatMinutes(sumPlan) + " Std/min verplant, es müssten aber "
					+ WorktimeEntryUtils.formatMinutes(sumCheck) + " sein! ("
					+ WorktimeEntryUtils.formatMinutes(sumCheck - sumPlan) + " Minusstunden)";
		} else {
			msg = "Es wurden " + WorktimeEntryUtils.formatMinutes(sumPlan) + " Std/min verplant ("
					+ WorktimeEntryUtils.formatMinutes(sumPlan - sumCheck) + " Überstunden)";
		}
		lbWarning.setText(msg);
	}

	private WorktimeEntry getSelectedWorkEntry() {
		int selectionIndex = table.getSelectionIndex();
		Object[] elements = worktimeContentProvider.getElements(null);
		WorktimeEntry weCopy = WorktimeEntryUtils.clone((WorktimeEntry) elements[selectionIndex]);
		return weCopy;
	}

	private void openEditor() {
		WorktimeEntry weCopy = getSelectedWorkEntry();

		WorktimeEntryEditorDialog editor = new WorktimeEntryEditorDialog(HistoryViewerDialog.this.getShell());

		editor.setWorktimeEntry(weCopy);
		int open = editor.open();
		if (open == Window.OK) {
			dataStorage.saveWorktimeEntry(weCopy);
			setStartDate(weCopy.getDate());
			dataStorage.saveData();
		}
	}

	public static void openViewer() {
		Realm.runWithDefault(SWTObservables.getRealm(Display.getDefault()), new Runnable() {
			public void run() {
				HistoryViewerDialog historyViewer = new HistoryViewerDialog(null);
				historyViewer.open();
			}
		});
	}

}
