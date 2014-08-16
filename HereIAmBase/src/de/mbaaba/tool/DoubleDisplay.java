package de.mbaaba.tool;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;

public class DoubleDisplay extends Composite {

	public DoubleDisplay(Composite parent, int style) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout(2, true);
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginHeight = 0;
		setLayout(gridLayout);
						
								BalanceLabel balanceLabel = new BalanceLabel(this, SWT.NONE);
								balanceLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
								balanceLabel.setBackground(SWTResourceManager.getColor(176, 224, 230));
				
						BalanceLabel balanceLabel_1 = new BalanceLabel(this, SWT.NONE);
						balanceLabel_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
						balanceLabel_1.setBalance(20);
						balanceLabel_1.setBackground(SWTResourceManager.getColor(255, 250, 205));
		// TODO Auto-generated constructor stub
	}

}
