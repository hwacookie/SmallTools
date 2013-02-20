package de.mbaaba.tools.shared;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Style implements Serializable {

	private static final long serialVersionUID = 1L;
	private String description;
	private String name;
	private Map<WordTypes, WordList> wordsMap;

	public Style() {
		this("","",new HashMap<WordTypes, WordList>());
	}

	public Style(String aName, String aDescription, Map<WordTypes, WordList> map) {
		wordsMap = map;
		name = aName;
		description = aDescription;
	}

	public Map<WordTypes, WordList> getWordsMap() {
		return wordsMap;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	
}


// private List<Listener> listeners = new ArrayList<Listener>();
//
// public void addLoadCompleteListener(Listener aListener) {
// listeners.add(aListener);
// }
//
// public void removeLoadListener(Listener aListener) {
// listeners.remove(aListener);
// }
