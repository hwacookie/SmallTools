package de.mbaaba.tools.client;

import java.util.ArrayList;
import java.util.List;

public class StyleManager {

	private static StyleManager instance;
	private List<TypedListener<StyleEvent>> styleChangeListeners;

	private StyleManager() {
		styleChangeListeners = new ArrayList<TypedListener<StyleEvent>>();
	}

	public static StyleManager getInstance() {
		if (instance==null) {
			instance=new StyleManager();
		}
		return instance;
	}

	public void addListener(TypedListener<StyleEvent> aListener) {
		styleChangeListeners.add(aListener);
	}

	public void removeListener(TypedListener<StyleEvent> aListener) {
		styleChangeListeners.remove(aListener);
	}

	public void notifyChange(StyleEvent aStyleEvent) {
		for (TypedListener<StyleEvent> listener : styleChangeListeners) {
			listener.notifyMe(aStyleEvent);
		}
	}

}
