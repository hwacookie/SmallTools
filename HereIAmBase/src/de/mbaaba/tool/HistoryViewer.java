package de.mbaaba.tool;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import de.mbaaba.tool.pw.data.WorktimeContentProvider;
import de.mbaaba.tool.pw.data.WorktimeEntry;
import de.mbaaba.tool.pw.data.WorktimeLabelProvider;

public class HistoryViewer extends Dialog {

	private DataBindingContext m_bindingContext;
	private Table table;
	private TableViewer tableViewer;
	private Text startTime;
	private Text endTime;

	private WorktimeEntry currentWorktimeEntry;
	private LogfileDataStorage dataStorage;
	private DateTime startDate;

	private WorktimeContentProvider worktimeContentProvider;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public HistoryViewer(Shell parentShell) {
		super(parentShell);
		dataStorage = new LogfileDataStorage(new File("testdata"));
		
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
		gridLayout.numColumns = 4;

		startDate = new DateTime(container, SWT.CALENDAR);
		startDate.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false,
				1, 1));

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
		
		
		startTime = new Text(container, SWT.BORDER);
		startTime.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));

		endTime = new Text(container, SWT.BORDER);
		endTime.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		new Label(container, SWT.NONE);

		tableViewer = new TableViewer(container, SWT.BORDER
				| SWT.FULL_SELECTION);
		
		worktimeContentProvider = new WorktimeContentProvider();
		tableViewer.setLabelProvider(new WorktimeLabelProvider());
		tableViewer.setContentProvider(worktimeContentProvider);
		
		
		tableViewer.getTable().setLinesVisible(true);
		tableViewer.getTable().setHeaderVisible(true);		
		
		table = tableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));

		
		
		
		TableViewerColumn tableViewerColumn = new TableViewerColumn(
				tableViewer, SWT.NONE);
		TableColumn colDate = tableViewerColumn.getColumn();
		colDate.setWidth(100);
		colDate.setText("Datum");

		TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(
				tableViewer, SWT.NONE);
		TableColumn colStart = tableViewerColumn_2.getColumn();
		colStart.setWidth(100);
		colStart.setText("Startzeit");

		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(
				tableViewer, SWT.NONE);
		TableColumn colEnd = tableViewerColumn_1.getColumn();
		colEnd.setWidth(100);
		colEnd.setText("Ende");

		return container;
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
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
		m_bindingContext = initDataBindings();
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}

	protected void setStartDate(Date aDate) {
		currentWorktimeEntry = dataStorage.getWorktimeEntry(aDate);
		if (m_bindingContext != null) {
			m_bindingContext.dispose();
		}
		m_bindingContext = initDataBindings();
		fillModel();
	}

	private void fillModel() {
		Calendar cal = new GregorianCalendar();
		cal.setTime(currentWorktimeEntry.getDate());
		WorktimeEntry[] elements = new WorktimeEntry[7];
		for( int i = 0; i < elements.length; i++ ) {
			cal.set(Calendar.DAY_OF_WEEK, (i+2) % 7);
			Date thisDate = cal.getTime();
			elements[i] = dataStorage.getWorktimeEntry(thisDate);
		}
		try {
			tableViewer.setInput(elements);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextStartTimeObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(startTime);
		IObservableValue startTimeCurrentWorktimeEntryObserveValue = PojoProperties
				.value("startTime").observe(currentWorktimeEntry);
		bindingContext.bindValue(observeTextStartTimeObserveWidget,
				startTimeCurrentWorktimeEntryObserveValue, null, null);
		//
		IObservableValue observeTextEndTimeObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(endTime);
		IObservableValue endTimeCurrentWorktimeEntryObserveValue = PojoProperties
				.value("endTime").observe(currentWorktimeEntry);
		bindingContext.bindValue(observeTextEndTimeObserveWidget,
				endTimeCurrentWorktimeEntryObserveValue, null, null);
		//

		return bindingContext;
	}

	// -----------------

	public static void main(String[] args) throws InterruptedException,
			IOException {

		final Display d = new Display();
		final Shell s = new Shell();

		Realm.runWithDefault(SWTObservables.getRealm(d), new Runnable() {
			public void run() {
				HistoryViewer historyViewer = new HistoryViewer(s);
				historyViewer.open();

				while (!s.isDisposed()) {
					if (!d.readAndDispatch()) {
						d.sleep();
					}
				}
			}
		});

		d.dispose();

	}
}
