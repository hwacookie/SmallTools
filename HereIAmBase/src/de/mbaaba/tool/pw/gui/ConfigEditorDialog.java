package de.mbaaba.tool.pw.gui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import de.mbaaba.util.ConfigManager;
import de.mbaaba.util.Configurator;

public class ConfigEditorDialog extends Dialog {

	private final Configurator configurator = ConfigManager.getInstance();

	public class BreakDefinition {
		private String breakName;

		public BreakDefinition(String aBreakName) {
			breakName = aBreakName;
		}

		private Composite defineGui(Composite aContainer) {

			Composite composite = new Composite(aContainer, SWT.NONE);
			GridLayoutFactory.fillDefaults().numColumns(5).applyTo(composite);

			{
				Label lbShortBrake = new Label(composite, SWT.NONE);
				lbShortBrake.setText("Pause nach:");

				int minuteStart = configurator.getProperty(breakName + "." + ConfigManager.CFG_START, 4 * 60);
				final DateTime dtStart = new DateTime(composite, SWT.BORDER | SWT.TIME | SWT.SHORT);
				dtStart.setHours(minuteStart / 60);
				dtStart.setMinutes((minuteStart % 60));

				dtStart.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						int minutes = dtStart.getHours() * 60 + dtStart.getMinutes();
						configurator.setProperty(breakName + "." + ConfigManager.CFG_START, minutes);
					}
				});

			}

			{
				Label lblLength = new Label(composite, SWT.NONE);
				lblLength.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
				lblLength.setText("Dauer:");

				int minuteLength = configurator.getProperty(breakName + "." + ConfigManager.CFG_LENGTH, 15);
				final DateTime dtLength = new DateTime(composite, SWT.BORDER | SWT.TIME | SWT.SHORT);
				dtLength.setHours(minuteLength / 60);
				dtLength.setMinutes((minuteLength % 60));

				dtLength.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						int minutes = dtLength.getHours() * 60 + dtLength.getMinutes();
						configurator.setProperty(breakName + "." + ConfigManager.CFG_LENGTH, minutes);
					}
				});
			}

			String text = "Pflichtpause";
			final String configParamName = breakName + "." + ConfigManager.CFG_STOP_CLOCK;
			createCheckbox(composite, text, configParamName);

			return composite;

		}
	}

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public ConfigEditorDialog(Shell parentShell) {
		super(parentShell);
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
		container.setLayout(gridLayout);

		BreakDefinition firstBreak = new BreakDefinition(ConfigManager.CFG_SHORT_BREAK);
		firstBreak.defineGui(container);

		BreakDefinition secondBreak = new BreakDefinition(ConfigManager.CFG_LONG_BREAK);
		secondBreak.defineGui(container);

		new Label(container, SWT.NONE);

		createCheckbox(container, "Auf Pausen hinweisen", ConfigManager.CFG_REMIND_ON_BREAKS);
		createCheckbox(container, "Auf Feierabend hinweisen", ConfigManager.CFG_REMIND_ON_EOW);
		
		

		return container;
	}

	private void createCheckbox(Composite composite, String text, final String configParamName) {
		final Button checkBox = new Button(composite, SWT.CHECK);
		checkBox.setText(text);
		checkBox.setSelection(configurator.getProperty(configParamName, false));
		checkBox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				configurator.setProperty(configParamName, checkBox.getSelection());
			}
		});
	}

	@Override
	protected void configureShell(Shell newShell) {
		newShell.setText("Einstellungen");
		super.configureShell(newShell);
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
		return new Point(450, 300);
	}

	@Override
	protected void buttonPressed(int buttonId) {
		configurator.saveProperties();
		super.buttonPressed(buttonId);
	}

}
