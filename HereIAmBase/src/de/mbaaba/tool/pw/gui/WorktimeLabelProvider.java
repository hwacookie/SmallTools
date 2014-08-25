package de.mbaaba.tool.pw.gui;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;

import de.mbaaba.tool.pw.data.WorktimeEntry;
import de.mbaaba.tool.pw.data.WorktimeEntryUtils;

public class WorktimeLabelProvider extends CellLabelProvider implements
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
			if (we.getDate() != null)
				return WorktimeEntryUtils.DATE_ONLY.format(we.getDate());
			break;
		case 1:
			if (we.getStartTime() != null) {
				return WorktimeEntryUtils.TIME_ONLY.format(we.getStartTime());
			}
			break;
		case 2:
			if (we.getEndTime() != null) {
				return WorktimeEntryUtils.TIME_ONLY.format(we.getEndTime());
			}
			break;
		case 3:
			int breakTime = WorktimeEntryUtils.getBreaktimeMinutes(we);
			if (breakTime > 0) {
				return WorktimeEntryUtils.formatMinutes(breakTime);
			}
			break;
		case 4:
			int time = WorktimeEntryUtils.getNetWorktimeInMinutes(we);
			if (time > 0) {
				return WorktimeEntryUtils.formatMinutes(time);
			}
			break;
		case 5:
			int planned = we.getPlanned();
			if (planned > 0) {
				return WorktimeEntryUtils.formatMinutes(planned);
			}
			break;

		case 6:
			String s = WorktimeEntryUtils.calculateBalanceString(we);
			return s;

		case 7:
			if (we.getComment() != null) {
				return we.getComment();
			} else if (WorktimeEntryUtils.isHoliday(we.getDate())) {
				return WorktimeEntryUtils.whatHoliday(we.getDate());
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
