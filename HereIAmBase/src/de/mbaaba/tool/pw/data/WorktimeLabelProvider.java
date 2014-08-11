package de.mbaaba.tool.pw.data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;

public class WorktimeLabelProvider extends CellLabelProvider implements
		ITableLabelProvider {

	private static final DateFormat DATE_ONLY = new SimpleDateFormat(
			"dd.MM.yyyy (E)");
	private static final DateFormat TIME_ONLY = new SimpleDateFormat("HH:mm");

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		WorktimeEntry we = (WorktimeEntry) element;
		switch (columnIndex) {
		case 0:
			if (we.getDate() != null)
				return DATE_ONLY.format(we.getDate());
			break;
		case 1:
			if (we.getStartTime() != null) {
				return TIME_ONLY.format(we.getStartTime());
			}
			break;
		case 2:
			if (we.getEndTime() != null) {
				return TIME_ONLY.format(we.getEndTime());
			}
			break;
		case 3:
			if (we.getComment() != null) {
				return we.getComment();
			}
			break;

		default:
			break;
		}
		return "";
	}

	@Override
	public void update(ViewerCell cell) {
		// TODO Auto-generated method stub

	}

}
