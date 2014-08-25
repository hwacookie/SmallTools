package de.mbaaba.tool.pw.gui;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.value.DateAndTimeObservableValue;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.mbaaba.tool.pw.data.WorktimeEntry;
import de.mbaaba.tool.pw.data.WorktimeEntryUtils;

public class WorktimeEntryEditorDialog extends Dialog {
	private DataBindingContext m_bindingContext;
	private Text txtComment;
	private Label lblHeadline;
	private DateTime dtCome;
	private DateTime dtLeave;
	private DateTime dtPlannedTime;
	private WorktimeEntry we;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public WorktimeEntryEditorDialog(Shell parentShell) {
		super(parentShell);
	}

	public void setWorktimeEntry(WorktimeEntry aWorktimeEntry) {
		we = aWorktimeEntry;
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

		lblHeadline = new Label(container, SWT.NONE);
		lblHeadline.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 2, 1));
		lblHeadline.setText("Zeiten f\u00FCr ");

		Label lblCome = new Label(container, SWT.NONE);
		lblCome.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		lblCome.setAlignment(SWT.RIGHT);
		lblCome.setText("Kommen:");

		dtCome = new DateTime(container, SWT.BORDER | SWT.TIME | SWT.SHORT);
		GridData gd_dtCome = new GridData(SWT.LEFT, SWT.CENTER, false, false,
				1, 1);
		gd_dtCome.widthHint = 60;
		dtCome.setLayoutData(gd_dtCome);

		Label lblLeave = new Label(container, SWT.NONE);
		lblLeave.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblLeave.setAlignment(SWT.RIGHT);
		lblLeave.setText("Gehen:");

		dtLeave = new DateTime(container, SWT.BORDER | SWT.TIME | SWT.SHORT);
		GridData gd_dtLeave = new GridData(SWT.LEFT, SWT.CENTER, false, false,
				1, 1);
		gd_dtLeave.widthHint = 60;
		dtLeave.setLayoutData(gd_dtLeave);

		Label lblPlan = new Label(container, SWT.NONE);
		lblPlan.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		lblPlan.setAlignment(SWT.RIGHT);
		lblPlan.setText("Plan:");

		dtPlannedTime = new DateTime(container, SWT.BORDER | SWT.TIME
				| SWT.SHORT);
		GridData gd_dtPlannedTime = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_dtPlannedTime.widthHint = 60;
		dtPlannedTime.setLayoutData(gd_dtPlannedTime);

		Label lblComment = new Label(container, SWT.NONE);
		lblComment.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblComment.setText("Kommentar:");
		lblComment.setAlignment(SWT.RIGHT);

		txtComment = new Text(container, SWT.BORDER | SWT.MULTI);

		txtComment.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		lblHeadline.setText("Zeiten f\u00FCr "
				+ WorktimeEntryUtils.DATE_ONLY.format(we.getDate()));

		dtPlannedTime.setHours(we.getPlanned() / 60);
		dtPlannedTime.setMinutes(we.getPlanned() % 60);
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

	@Override
	public boolean close() {
		we.setPlanned(dtPlannedTime.getHours() * 60
				+ dtPlannedTime.getMinutes());
		return super.close();
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 254);
	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextTxtCommentObserveWidget = WidgetProperties
				.text(SWT.Modify).observe(txtComment);
		IObservableValue commentWeObserveValue = PojoProperties
				.value("comment").observe(we);
		bindingContext.bindValue(observeTextTxtCommentObserveWidget,
				commentWeObserveValue, null, null);
		//

		IObservableValue d = PojoProperties.value("startTime").observe(we);
		IObservableValue t = PojoProperties.value("startTime").observe(we);
		DateAndTimeObservableValue observableValue = new DateAndTimeObservableValue(
				d, t);
		bindingContext.bindValue(SWTObservables.observeSelection(dtCome),
				observableValue);

		IObservableValue d2 = PojoProperties.value("endTime").observe(we);
		IObservableValue t2 = PojoProperties.value("endTime").observe(we);
		DateAndTimeObservableValue observableValue2 = new DateAndTimeObservableValue(
				d2, t2);
		bindingContext.bindValue(SWTObservables.observeSelection(dtLeave),
				observableValue2);

		return bindingContext;
	}
}
