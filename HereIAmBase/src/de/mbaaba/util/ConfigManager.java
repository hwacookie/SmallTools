package de.mbaaba.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class ConfigManager implements Configurator {

	public static final String CFG_LONG_BREAK = "longBreak";
	public static final String CFG_SHORT_BREAK = "shortBreak";
	public static final String CFG_START = "start";
	public static final String CFG_LENGTH = "length";
	public static final String CFG_STOP_CLOCK = "stopClock";

	public static final String CFG_LONG_BREAK_START = CFG_LONG_BREAK + "." + CFG_START;
	public static final String CFG_SHORT_BREAK_START = CFG_SHORT_BREAK + "." + CFG_START;
	public static final String CFG_LONG_BREAK_LENGTH = CFG_LONG_BREAK + "." + CFG_LENGTH;
	public static final String CFG_SHORT_BREAK_LENGTH = CFG_SHORT_BREAK + "." + CFG_LENGTH;
	public static final String CFG_LONG_BREAK_STOP_CLOCK = CFG_LONG_BREAK + "." + CFG_STOP_CLOCK;
	public static final String CFG_SHORT_BREAK_STOP_CLOCK = CFG_SHORT_BREAK + "." + CFG_STOP_CLOCK;

	public static final String CFG_SHOW_DISPLAY = "ShowDisplay";
	public static final String CFG_REMIND_ON_EOW = "RemindOnEOW";
	public static final String CFG_REMIND_ON_BREAKS = "RemindOnBreaks";

	public static final String CFG_SHELL_Y_POS = "shell.y";
	public static final String CFG_SHELL_X_POS = "shell.x";
	public static final String CFG_DEFAULT_MINUTES = "DefaultMinutes";

	private static ConfigManager instance;
	private PropertyFileConfigurator configFile;

	private ConfigManager() {
		String home = System.getProperty("user.home");
		File homeFile = new File(home + "/.presenceWatcher");
		homeFile.mkdirs();
		File appdata = new File(homeFile, "presenceWatcher.properties");
		try {
			configFile = new PropertyFileConfigurator(appdata.getCanonicalPath());
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (configFile != null) {
			Thread hook = new Thread() {
				@Override
				public void run() {
					super.run();
					configFile.saveProperties();

				}
			};
			Runtime.getRuntime().addShutdownHook(hook);
		}
	}

	public static ConfigManager getInstance() {
		if (instance == null) {
			instance = new ConfigManager();
		}
		return instance;
	}

	@Override
	public String getProperty(String aPropertyName, String aDefaultValue) {
		return configFile.getProperty(aPropertyName, aDefaultValue);
	}

	@Override
	public int getProperty(String aPropertyName, int aDefaultValue) {
		return configFile.getProperty(aPropertyName, aDefaultValue);
	}

	@Override
	public boolean getProperty(String aPropertyName, boolean aDefaultValue) {
		return configFile.getProperty(aPropertyName, aDefaultValue);
	}

	@Override
	public void setProperty(String aPropertyName, String aValue) {
		configFile.setProperty(aPropertyName, aValue);
		notifyListeners(aPropertyName, aValue);
	}

	@Override
	public void setProperty(String aPropertyName, boolean aValue) {
		configFile.setProperty(aPropertyName, aValue);
		notifyListeners(aPropertyName, aValue);
	}

	@Override
	public boolean saveProperties() {
		return configFile.saveProperties();
	}

	@Override
	public void setProperty(String aPropertyName, int aValue) {
		configFile.setProperty(aPropertyName, aValue);
		notifyListeners(aPropertyName, aValue);
	}

	private List<Listener> listeners = new ArrayList<Listener>();

	public void registerConfigListener(Listener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);

			// notify listener once initially
			Event event = new Event();
			event.text = "";
			event.data = null;
			listener.handleEvent(event);

		}
	}

	private void notifyListeners(final String paramName, Object data) {
		for (Listener listener : listeners) {
			Event event = new Event();
			event.text = paramName;
			event.data = data;
			listener.handleEvent(event);
		}
	}

}
