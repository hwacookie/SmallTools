package de.mbaaba.tools.shared;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Style implements Serializable {

	private static final long serialVersionUID = 1L;
	private String description;
	private String name;
	private Map<WordTypes, WordList> wordsMap;

	public Style() {
		this("", "", new HashMap<WordTypes, WordList>());
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
		s = s + "name=" + name + "\n";
		s = s + "description=" + description + "\n";

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
			s = s + word + "|";
		}
		s = wordList.getWordType().name() + "=" + s;
		return s;

	}

	public void importFromText(String text) {
	}
}
