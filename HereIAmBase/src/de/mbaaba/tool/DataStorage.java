package de.mbaaba.tool;

import java.util.Date;

import de.mbaaba.tool.pw.data.WorktimeEntry;

public interface DataStorage {

	void saveWorktimeEntry(WorktimeEntry aWorktimeEntry);

	WorktimeEntry getWorktimeEntry(Date aDate);
}
