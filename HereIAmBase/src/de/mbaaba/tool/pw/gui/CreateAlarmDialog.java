package de.mbaaba.tool.pw.gui;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.mbaaba.tool.pw.Log;
import de.mbaaba.tool.pw.data.WorktimeEntryUtils;
import de.mbaaba.util.Units;

public class CreateAlarmDialog extends Dialog {
	private Text text;
	private DateTime dateTime;
	private Label lbInMinutes;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public CreateAlarmDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected void configureShell(Shell newShell) {
		newShell.setText("Erinnerung anlegen");
		super.configureShell(newShell);
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

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.numColumns = 7;

		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("Erinnere mich um:");

		dateTime = new DateTime(container, SWT.BORDER | SWT.TIME | SWT.SHORT);
		GridData gd_dateTime = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_dateTime.widthHint = 60;
		dateTime.setLayoutData(gd_dateTime);

		dateTime.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				calcMinutes();
			}
		});

		createTimeAddButton(container, 5);
		createTimeAddButton(container, 15);
		createTimeAddButton(container, 30);
		createTimeAddButton(container, 60);

		lbInMinutes = new Label(container, SWT.NONE);
		lbInMinutes.setText("(in xx Minuten) ");
		GridData gd_dateTime2 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_dateTime.widthHint = 80;
		lbInMinutes.setLayoutData(gd_dateTime2);

		Label lbReminderText = new Label(container, SWT.NONE);
		lbReminderText.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
		lbReminderText.setText("... und zwar an:");

		text = new Text(container, SWT.BORDER | SWT.MULTI);
		text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 6, 1));
		text.setText("Hier, äh, Dingens ... verflixt, was war es denn nochmal?");
		text.setSelection(0, text.getText().length());

		addTime(5);

		return container;
	}

	protected void calcMinutes() {
		int hours = dateTime.getHours();
		int minutes = dateTime.getMinutes();
		Calendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		cal.set(Calendar.HOUR_OF_DAY, hours);
		cal.set(Calendar.MINUTE, minutes);

		long diff = (cal.getTime().getTime() - System.currentTimeMillis()) / Units.MINUTE;
		String string = "(in " + WorktimeEntryUtils.formatMinutes((int) diff) + " Minuten)";
		lbInMinutes.setText(string);
	}

	private void createTimeAddButton(Composite container, final int aTime) {
		Button btnNewButton = new Button(container, SWT.NONE);
		btnNewButton.setText("+" + aTime);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addTime(aTime);
			}

		});
	}

	private void addTime(final int aTime) {
		int hours = dateTime.getHours();
		int minutes = dateTime.getMinutes();
		int total = hours * 60 + minutes;
		total += aTime;
		hours = total / 60;
		total = total - (hours * 60);
		minutes = total;
		dateTime.setHours(hours);
		dateTime.setMinutes(minutes);
		calcMinutes();
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	protected void buttonPressed(int buttonId) {
		final String msg = text.getText();
		if (buttonId == IDialogConstants.OK_ID) {
			final Timer t = new Timer();
			TimerTask tt = new TimerTask() {
				@Override
				public void run() {
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							Log.logfileAdd("Opening alarm window");
							BreakInfoDialog breakInfoDialog = new BreakInfoDialog(null);
							breakInfoDialog.openWithData("Erinnerung", msg, 0 , BreakInfoDialog.IMG_ALARM);
							t.cancel();
						}
					});
				}
			};
			int hours = dateTime.getHours();
			int minutes = dateTime.getMinutes();
			Calendar cal = new GregorianCalendar();
			cal.set(Calendar.HOUR_OF_DAY, hours);
			cal.set(Calendar.MINUTE, minutes);
			cal.set(Calendar.SECOND, 0);

			Date alarmTime = new Date(cal.getTimeInMillis());
			Log.logfileAdd("Create a new alarm that should ring at " + alarmTime);
			t.schedule(tt, alarmTime);
		}
		super.buttonPressed(buttonId);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 179);
	}



}
