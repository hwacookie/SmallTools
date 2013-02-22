package de.mbaaba.tools.client;

import java.io.Serializable;

import de.mbaaba.tools.shared.Style;


public class StyleEvent implements Serializable {

	public enum StyleAction {
		CHANGED, SAVED, EXPORTED
	};

	private static final long serialVersionUID = 1L;
	Style style;
	StyleAction action;
	public StyleEvent(Style currentStyle, StyleAction exported) {
		style = currentStyle;
		action = exported;
	}
}