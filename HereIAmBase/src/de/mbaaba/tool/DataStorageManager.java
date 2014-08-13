package de.mbaaba.tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.thoughtworks.xstream.XStream;

import de.mbaaba.tool.pw.data.WorktimeEntry;
import de.mbaaba.util.Units;

public class DataStorageManager implements DataStorage {

	private LogfileDataStorage logfileDataStorage;
	private Map<Long, WorktimeEntry> data = new HashMap<Long, WorktimeEntry>();
	private static DataStorageManager instance;

	public static DataStorageManager getInstance() {
		if (instance == null) {
			instance = new DataStorageManager();
		}
		return instance;
	}

	private DataStorageManager() {
		String home = System.getProperty("user.home");
		File homeFile = new File(home + "/.presenceWatcher");
		logfileDataStorage = new LogfileDataStorage(homeFile);
		loadData();
	}

	@Override
	public void saveWorktimeEntry(WorktimeEntry aWorktimeEntry) {
		data.put(aWorktimeEntry.getDate().getTime() / Units.DAY, aWorktimeEntry);
	}

	public WorktimeEntry getTodaysWorktimeEntry() {
		return getWorktimeEntry(new Date());
	}
	
	
	@Override
	public WorktimeEntry getWorktimeEntry(Date aDate) {
		// try map ...
		WorktimeEntry res = data.get((aDate.getTime() / Units.DAY));
		if (res != null) {
			return res;
		}

		WorktimeEntry worktimeEntry = logfileDataStorage
				.getWorktimeEntry(aDate);

		data.put(aDate.getTime() / Units.DAY, worktimeEntry);

		return worktimeEntry;
	}

	public void saveData() {
		String home = System.getProperty("user.home");
		File homeFile = new File(home + "/.presenceWatcher/data.bin");
		XStream xStream = new XStream();
		try (FileOutputStream fos = new FileOutputStream(homeFile)) {
			xStream.toXML(data, fos);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadData() {
		String home = System.getProperty("user.home");
		File homeFile = new File(home + "/.presenceWatcher/data.bin");
		XStream xStream = new XStream();
		try (FileInputStream fin = new FileInputStream(homeFile)) {
			data = (Map<Long, WorktimeEntry>) xStream.fromXML(fin);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (data == null) {
				data = new HashMap<Long, WorktimeEntry>();
			}
		}

	}

}
