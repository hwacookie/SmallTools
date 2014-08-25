package de.mbaaba.tool.pw.gui;

import java.util.Timer;
import java.util.TimerTask;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
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

	protected static final String IMG_ALARM = "alarm.png";
	protected static final String IMG_WALK = "walking.png";
	protected static final String IMG_COFFEE = "coffee.png";
	protected static final String IMG_HOME = "home.png";
	private Label lbMessage;
	private ProgressBar pbTimeLeft;
	private long timerTick;
	private String message;
	private long numSeconds;
	private Timer pbTimer;
	private String title;
	private String imageName;
	protected String soundName;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public BreakInfoDialog(Shell parentShell) {
		super(parentShell);
		setBlockOnOpen(false);
	}

	public void openWithData(String aTitle, String aMessage, long aNumSeconds, String aImageName) {
		message = aMessage;
		title = aTitle;
		numSeconds = aNumSeconds;
		imageName = aImageName;
		soundName = "ring.wav";
		open();
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
		container.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.marginRight = 8;
		gridLayout.marginLeft = 8;
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.numColumns = 2;
		getShell().setText(title);

		Label label = new Label(container, SWT.NONE);
		GridData gd_label = new GridData(SWT.CENTER, SWT.TOP, false, false, 1, 1);
		gd_label.minimumWidth = 200;
		gd_label.minimumHeight = 133;
		label.setLayoutData(gd_label);
		label.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		label.setImage(SWTResourceManager.getImage(BreakInfoDialog.class, "/images/walking.png"));

		if (imageName != null) {
			label.setImage(SWTResourceManager.getImage(BreakInfoDialog.class, "/images/" + imageName));
		}

		lbMessage = new Label(container, SWT.WRAP | SWT.CENTER);
		lbMessage.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lbMessage.setFont(SWTResourceManager.getFont("Segoe Script", 10, SWT.ITALIC));
		GridData gd_lbMessage = new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1);
		gd_lbMessage.minimumHeight = 40;
		lbMessage.setLayoutData(gd_lbMessage);
		lbMessage.setText("Hachja, *jetzt* ne sch\u00F6ne Tasse Kaffee ... das w\u00E4r's, oder?");

		lbMessage.setText(message);

		int numSteps = 100;

		pbTimeLeft = new ProgressBar(container, SWT.SMOOTH);
		pbTimeLeft.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		GridData gd_pbTimeLeft = new GridData(SWT.CENTER, SWT.CENTER, true, true, 2, 1);
		gd_pbTimeLeft.widthHint = 400;
		gd_pbTimeLeft.heightHint = 4;
		pbTimeLeft.setLayoutData(gd_pbTimeLeft);
		pbTimeLeft.setSelection(100);

		pbTimeLeft.setMinimum(0);
		pbTimeLeft.setMaximum(300);
		pbTimeLeft.setSelection(0);

		if (numSeconds > 0) {

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
		} else {
			pbTimeLeft.setVisible(false);
		}

		playSound();

		return container;
	}

	@Override
	public int open() {
		setBlockOnOpen(false);
		return super.open();
	}

	@Override
	public boolean close() {
		if (pbTimer != null) {
			pbTimer.cancel();
		}
		return super.close();
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(474, 250);
	}

	public synchronized void playSound() {
		new Thread(new Runnable() {
			// The wrapper thread is unnecessary, unless it blocks on the
			// Clip finishing; see comments.
			public void run() {
				try {
					Clip clip = AudioSystem.getClip();
					AudioInputStream inputStream = AudioSystem.getAudioInputStream(CreateAlarmDialog.class
							.getResourceAsStream("/sounds/" + soundName));
					clip.open(inputStream);
					clip.start();
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}
		}).start();
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button createButton = createButton(parent, IDialogConstants.OK_ID, "Jaja, schon gut!", true);
	}

}
