package de.mbaaba.tools.shared;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Stringify;

import de.mbaaba.tools.client.StyleEvent;
import de.mbaaba.tools.client.StyleEvent.StyleAction;
import de.mbaaba.tools.client.NotificationManager;

@Entity
public class Style implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String NAME = "NAME";
	private static final String DESCRIPTION = "DESCRIPTION";
	private static final String SEPA = "/";
	private static final String OWNER = "OWNER";

	@Id
	private String name;

	private String description;
	private String owner;
	@Stringify(WordTypeStringifier.class)
	private Map<WordTypes, WordList> wordsMap;

	public Style() {
		this("New Theme", "No Description yet",
				new HashMap<WordTypes, WordList>());
		// add all word types
		for (WordTypes wordType : WordTypes.values()) {
			wordsMap.put(wordType, new WordList(wordType));
		}
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

	public String exportToText() {
		String s = "";
		s = s + NAME + "=" + name + "\n";
		s = s + DESCRIPTION + "=" + description + "\n";
		s = s + OWNER + "=" + owner + "\n";

		s = s + printList(wordsMap.get(WordTypes.NUMBER_SINGULAR)) + "\n";
		s = s + printList(wordsMap.get(WordTypes.NOUN_SINGULAR)) + "\n";
		s = s + printList(wordsMap.get(WordTypes.VERB_SINGULAR)) + "\n";
		s = s + printList(wordsMap.get(WordTypes.NUMBER_PLURAL)) + "\n";
		s = s + printList(wordsMap.get(WordTypes.NOUN_PLURAL)) + "\n";
		s = s + printList(wordsMap.get(WordTypes.VERB_PLURAL)) + "\n";
		s = s + printList(wordsMap.get(WordTypes.ATTRIBUTES)) + "\n";
		s = s + printList(wordsMap.get(WordTypes.PREPOSITION)) + "\n";
		s = s + printList(wordsMap.get(WordTypes.POSSESSIVE_PRONOUN)) + "\n";
		s = s + printList(wordsMap.get(WordTypes.PUNCTUATION)) + "\n";
		return s;

	}

	private String printList(WordList wordList) {
		String s = "";
		List<String> words = wordList.getWords();
		for (String word : words) {
			s = s + word + SEPA;
		}
		s = wordList.getWordType().name() + "=" + s;
		return s;

	}

	public void importFromText(String text) {
		Properties props = new Properties();
		props.load(text);
		String importedName = props.getProperty(NAME);
		setName(importedName);
		setDescription(props.getProperty(DESCRIPTION));
		Set<WordTypes> keySet = wordsMap.keySet();
		for (WordTypes wordTypes : keySet) {
			String s = (String) props.getProperty(wordTypes.name());
			WordList wordList = wordsMap.get(wordTypes);
			wordList.parse(s, SEPA);
		}
		NotificationManager.getInstance().fireStyleEvent(
				new StyleEvent(this, StyleAction.CHANGED));

	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}
}
