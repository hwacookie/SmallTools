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

	public void export() {

		System.out.println("name=" + name);
		System.out.println("description=" + description);

		System.out.println(printList(wordsMap.get(WordTypes.NUMBER_SINGULAR)));
		System.out.println(printList(wordsMap.get(WordTypes.NOUN_SINGULAR)));
		System.out.println(printList(wordsMap.get(WordTypes.VERB_SINGULAR)));
		System.out.println(printList(wordsMap.get(WordTypes.NUMBER_PLURAL)));
		System.out.println(printList(wordsMap.get(WordTypes.NOUN_PLURAL)));
		System.out.println(printList(wordsMap.get(WordTypes.VERB_PLURAL)));
		System.out.println(printList(wordsMap.get(WordTypes.ATTRIBUTES)));
		System.out.println(printList(wordsMap.get(WordTypes.PREPOSITION)));
		System.out.println(printList(wordsMap.get(WordTypes.POSSESSIVE_PRONOUN)));
		System.out.println(printList(wordsMap.get(WordTypes.PUNCTUATION)));

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
}
