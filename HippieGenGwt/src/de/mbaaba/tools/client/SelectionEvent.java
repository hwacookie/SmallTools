package de.mbaaba.tools.client;

import java.io.Serializable;


public class SelectionEvent implements Serializable {
	private static final long serialVersionUID = 1L;
	String word;
	public SelectionEvent(String aWord) {
		word = aWord;
	}
}