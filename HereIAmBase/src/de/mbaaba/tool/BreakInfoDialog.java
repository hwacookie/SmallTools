package de.mbaaba.tool;

import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.internal.databinding.swt.ShellTextProperty;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

import de.mbaaba.util.Units;

public class BreakInfoDialog extends Dialog {

	private Label lbMessage;
	private ProgressBar pbTimeLeft;
	private long timerTick;
	private String message;
	private long numSeconds;
	private Timer pbTimer;
	private String title;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public BreakInfoDialog(Shell parentShell) {
		super(parentShell);
	}

	public void openWithData(String aTitle, String aMessage, long aNumSeconds) {
		message = aMessage;
		title = aTitle;
		numSeconds = aNumSeconds;
		setBlockOnOpen(false);
		open();
	}
	
	@Override
	protected Point getInitialLocation(Point initialSize) {
		Shell shell = this.getShell(); 
		Monitor primary = shell.getMonitor(); 
		Rectangle bounds = primary.getBounds (); 
		Rectangle rect = shell.getBounds ();
		
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
		gridLayout.numColumns = 2;

		lbMessage = new Label(container, SWT.WRAP | SWT.CENTER);
		lbMessage.setFont(SWTResourceManager.getFont("Segoe Script", 10, SWT.ITALIC));
		GridData gd_lbMessage = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 2, 1);
		gd_lbMessage.heightHint = 40;
		gd_lbMessage.minimumHeight = 40;
		lbMessage.setLayoutData(gd_lbMessage);
		lbMessage.setText("Hachja, *jetzt* ne sch\u00F6ne Tasse Kaffee ... das w\u00E4r's, oder?");

		Label lblNewLabel = new Label(container, SWT.NONE);
		GridData gd_lblNewLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 3);
		gd_lblNewLabel.widthHint = 200;
		gd_lblNewLabel.heightHint = 133;
		lblNewLabel.setLayoutData(gd_lblNewLabel);
		lblNewLabel.setImage(SWTResourceManager.getImage(BreakInfoDialog.class, "/images/Coffee_break.jpg"));
		lblNewLabel.setSize(200, 150);

		lbMessage.setText(message);
		getShell().setText(title);

		new Label(container, SWT.NONE);

		pbTimeLeft = new ProgressBar(container, SWT.NONE);
		pbTimeLeft.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		pbTimeLeft.setSelection(100);

		int numSteps = 100;

		pbTimeLeft.setMinimum(0);
		pbTimeLeft.setMaximum(numSteps);
		pbTimeLeft.setSelection(0);
		new Label(container, SWT.NONE);

		timerTick = ((numSeconds * Units.SECOND) / numSteps);

		TimerTask tt = new TimerTask() {

			@Override
			public void run() {
				getShell().getDisplay().asyncExec(new Runnable() {

					@Override
					public void run() {
						int newSelection = pbTimeLeft.getSelection() + 1;
						if (newSelection <= pbTimeLeft.getMaximum()) {
							pbTimeLeft.setSelection(newSelection);
						} else {
							close();
						}
					}
				});
			}
		};

		pbTimer = new Timer(false);
		pbTimer.scheduleAtFixedRate(tt, 100, timerTick);

		return container;
	}

	@Override
	public boolean close() {
		pbTimer.cancel();
		return super.close();
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button button = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		button.setText("Jaja, schon gut");
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 263);
	}

}
