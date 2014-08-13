package de.mbaaba.tool;

import java.awt.MouseInfo;
import java.awt.PointerInfo;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import de.mbaaba.tool.HereIAm.Activity;
import de.mbaaba.tool.pw.data.WorktimeContentProvider;
import de.mbaaba.tool.pw.data.WorktimeEntry;
import de.mbaaba.tool.pw.data.WorktimeEntryUtils;
import de.mbaaba.tool.pw.data.WorktimeLabelProvider;
import de.mbaaba.util.Units;

public class HistoryViewer extends Dialog {

	private Table table;
	private TableViewer tableViewer;

	// private WorktimeEntry currentWorktimeEntry;
	private DataStorageManager dataStorage;
	private DateTime startDate;

	private WorktimeContentProvider worktimeContentProvider;
	private WorktimeLabelProvider labelProvider;
	private Label lblWeekBalanceOut;
	private Date curStartDate;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public HistoryViewer(Shell parentShell) {
		super(parentShell);
		worktimeContentProvider = new WorktimeContentProvider();
		labelProvider = new WorktimeLabelProvider();

		dataStorage = DataStorageManager.getInstance();
		setShellStyle(getShellStyle() | SWT.RESIZE);

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
		GridData gd_startDate = new GridData(SWT.LEFT, SWT.TOP, false, false,
				1, 1);
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

		tableViewer = new TableViewer(container, SWT.BORDER
				| SWT.FULL_SELECTION | SWT.MULTI);

		tableViewer.getTable().setLinesVisible(true);
		tableViewer.getTable().setHeaderVisible(true);

		table = tableViewer.getTable();

		Menu popupMenu = new Menu(table);
		MenuItem miOpenEditor = new MenuItem(popupMenu, SWT.PUSH);
		miOpenEditor.setText("Werte ändern");
		SelectionAdapter selectionAdapter = new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				WorktimeEntry weCopy = getSelectedWorkEntry();

				WorktimeEntryEditor editor = new WorktimeEntryEditor(
						HistoryViewer.this.getShell());

				editor.setWorktimeEntry(weCopy);
				int open = editor.open();
				if (open == Window.OK) {
					dataStorage.saveWorktimeEntry(weCopy);
					setStartDate(weCopy.getDate());
					dataStorage.saveData();
				}

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

		table.addSelectionListener(selectionAdapter);

		GridData gd_table = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_table.heightHint = 143;
		table.setLayoutData(gd_table);

		TableViewerColumn tvcDate = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn colDate = tvcDate.getColumn();
		colDate.setWidth(98);
		colDate.setText("Datum");

		TableViewerColumn tvcStart = new TableViewerColumn(tableViewer,
				SWT.NONE);
		TableColumn colStart = tvcStart.getColumn();
		colStart.setWidth(65);
		colStart.setText("Startzeit");

		TableViewerColumn tvcEnd = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn colEnd = tvcEnd.getColumn();
		colEnd.setWidth(56);
		colEnd.setText("Ende");

		TableViewerColumn tvcBrake = new TableViewerColumn(tableViewer,
				SWT.NONE);
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

		TableViewerColumn tvcBalance = new TableViewerColumn(tableViewer,
				SWT.NONE);
		TableColumn colBalance = tvcBalance.getColumn();
		colBalance.setWidth(54);
		colBalance.setText("Saldo");

		TableViewerColumn tvcComment = new TableViewerColumn(tableViewer,
				SWT.NONE);
		TableColumn colComment = tvcComment.getColumn();
		colComment.setWidth(226);
		colComment.setText("Kommentar");

		new Label(container, SWT.NONE);

		Composite composite = new Composite(container, SWT.NONE);
		FillLayout fl_composite = new FillLayout(SWT.HORIZONTAL);
		fl_composite.spacing = 5;
		composite.setLayout(fl_composite);

		Label lblWeekBalance = new Label(composite, SWT.NONE);
		lblWeekBalance.setText("Saldo:");

		lblWeekBalanceOut = new Label(composite, SWT.NONE);
		lblWeekBalanceOut.setText("0");
		new Label(container, SWT.NONE);

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

				IStructuredSelection selection = (IStructuredSelection) tableViewer
						.getSelection();

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
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// calc balance
		int balance = 0;
		Date today = new Date();
		for (WorktimeEntry worktimeEntry : list) {
			int time = WorktimeEntryUtils
					.getNetWorktimeInMinutes(worktimeEntry);
			if ((worktimeEntry.getDate().before(today))
					&& (worktimeEntry.getPlanned() > 0)) {
				balance += (time - worktimeEntry.getPlanned());
			}
		}

		lblWeekBalanceOut.setText(WorktimeEntryUtils.formatMinutes(balance));
	}

	private WorktimeEntry getSelectedWorkEntry() {
		int selectionIndex = table.getSelectionIndex();
		Object[] elements = worktimeContentProvider.getElements(null);
		WorktimeEntry weCopy = WorktimeEntryUtils
				.clone((WorktimeEntry) elements[selectionIndex]);
		return weCopy;
	}

	public static void openViewer(final Shell s) {
		Realm.runWithDefault(SWTObservables.getRealm(s.getDisplay()),
				new Runnable() {
					public void run() {
						HistoryViewer historyViewer = new HistoryViewer(s);
						historyViewer.open();
					}
				});
	}

}
