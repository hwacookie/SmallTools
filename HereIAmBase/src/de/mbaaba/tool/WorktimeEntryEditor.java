package de.mbaaba.tool;

import java.util.Calendar;
import java.util.GregorianCalendar;

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

public class WorktimeEntryEditor extends Dialog {
	private Text txtComment;
	private Label lblHeadline;
	private DateTime dtCome;
	private DateTime dtLeave;
	private DateTime dtPlannedTime;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public WorktimeEntryEditor(Shell parentShell) {
		super(parentShell);
	}

	
	public void setWorktimeEntry(WorktimeEntry aWorktimeEntry) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(aWorktimeEntry.getDate());
		//lblHeadline.setText(string);
		
	}
	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.numColumns = 2;
		
		lblHeadline = new Label(container, SWT.NONE);
		lblHeadline.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		lblHeadline.setText("Zeiten f\u00FCr ");
		
		Label lblCome = new Label(container, SWT.NONE);
		lblCome.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblCome.setAlignment(SWT.RIGHT);
		lblCome.setText("Kommen:");
		
		dtCome = new DateTime(container, SWT.BORDER | SWT.TIME | SWT.SHORT);
		GridData gd_dtCome = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_dtCome.widthHint = 60;
		dtCome.setLayoutData(gd_dtCome);
		
		Label lblLeave = new Label(container, SWT.NONE);
		lblLeave.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblLeave.setAlignment(SWT.RIGHT);
		lblLeave.setText("Gehen:");
		
		dtLeave = new DateTime(container, SWT.BORDER | SWT.TIME | SWT.SHORT);
		GridData gd_dtLeave = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_dtLeave.widthHint = 60;
		dtLeave.setLayoutData(gd_dtLeave);
		
		Label lblPlan = new Label(container, SWT.NONE);
		lblPlan.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPlan.setAlignment(SWT.RIGHT);
		lblPlan.setText("Plan:");
		
		dtPlannedTime = new DateTime(container, SWT.BORDER | SWT.TIME | SWT.SHORT);
		GridData gd_dtPlannedTime = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_dtPlannedTime.widthHint = 60;
		dtPlannedTime.setLayoutData(gd_dtPlannedTime);

		
		Label lblComment = new Label(container, SWT.NONE);
		lblComment.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblComment.setText("Kommentar:");
		lblComment.setAlignment(SWT.RIGHT);
		
		txtComment = new Text(container, SWT.BORDER | SWT.MULTI);
		
		txtComment.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		return container;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 254);
	}

}
