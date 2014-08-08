package de.mbaaba.tool.pw.data;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class WorktimeLabelProvider extends LabelProvider implements
		ITableLabelProvider {

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		WorktimeEntry we = (WorktimeEntry) element;
		switch (columnIndex) {
		case 0:
			return we.getDate().toString();
		case 1:
			return we.getStartTime().toString();
		case 2:
			return we.getEndTime().toString();

		default:
			break;
		}
		return null;
	}

}
