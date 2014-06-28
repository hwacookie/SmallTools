package de.mbaaba.tools.client;

import java.util.ArrayList;
import java.util.List;

public class NotificationManager {

	private static NotificationManager instance;
	private List<TypedListener<StyleEvent>> styleChangeListeners;
	private List<TypedListener<SelectionEvent>> selectionListeners;

	private NotificationManager() {
		styleChangeListeners = new ArrayList<TypedListener<StyleEvent>>();
		selectionListeners = new ArrayList<TypedListener<SelectionEvent>>();
	}

	public static NotificationManager getInstance() {
		if (instance == null) {
			instance = new NotificationManager();
		}
		return instance;
	}

	public void addSelectionListener(TypedListener<SelectionEvent> aListener) {
		selectionListeners.add(aListener);
	}

	public void removeSelectionListener(TypedListener<SelectionEvent> aListener) {
		selectionListeners.remove(aListener);
	}

	public void addListener(TypedListener<StyleEvent> aListener) {
		styleChangeListeners.add(aListener);
	}

	public void removeListener(TypedListener<StyleEvent> aListener) {
		styleChangeListeners.remove(aListener);
	}

	public void fireStyleEvent(StyleEvent aStyleEvent) {
		for (TypedListener<StyleEvent> listener : styleChangeListeners) {
			listener.notifyMe(aStyleEvent);
		}
	}

	public void fireSelectionEvent(SelectionEvent aSelectionEvent) {
		for (TypedListener<SelectionEvent> listener : selectionListeners) {
			listener.notifyMe(aSelectionEvent);
		}
	}

}
