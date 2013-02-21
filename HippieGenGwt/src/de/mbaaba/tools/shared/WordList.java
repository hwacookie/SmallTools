package de.mbaaba.tools.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WordList implements Serializable {

	public static Random random = new Random();

	private static final long serialVersionUID = 1L;

	private WordTypes wordType;
	private List<String> words;

	public WordList() {
		words = new ArrayList<String>();
	}

	public WordList(WordTypes aWordType) {
		this();
		setWordType(aWordType);
	}

	public WordList(WordTypes aWordType, String numbersSingularDefaults) {
		this(aWordType);
		parse(numbersSingularDefaults);
	}

	public WordTypes getWordType() {
		return wordType;
	}

	public void setWordType(WordTypes wordType) {
		this.wordType = wordType;
	}

	public List<String> getWords() {
		return words;
	}

	public String getRandomWord() {
		return words.get(random.nextInt(words.size()));
	}

	public void addWord(String aWord) {
		words.add(aWord);
	}

	public void clearAll() {
		words.clear();
	}

	public String buildString() {
		StringBuffer buf = new StringBuffer();
		for (String word : words) {
			if (word.trim().length() > 0) {
				buf.append(word.trim()).append("\n");
			}
		}
		return buf.toString();
	}

	public void parse(String aWordsAsString) {
		clearAll();
		String[] split = aWordsAsString.split("\\n");
		for (String string : split) {
			addWord(string.trim());
		}
	}
	
	

}
