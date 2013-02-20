package de.mbaaba.tools.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import de.mbaaba.tools.shared.DefaultWordLists;
import de.mbaaba.tools.shared.Style;
import de.mbaaba.tools.shared.WordList;
import de.mbaaba.tools.shared.WordTypes;

public class HippieGen {

	static final String DEFAULT_STYLE = "Default";

	private Random random;

	// private Map<String, WordList> allLists = new HashMap<String, WordList>();

	private final GreetingServiceAsync client;

	private String[] availableStyles;

	private Style currentStyle;

	public HippieGen() {
		client = GWT.create(GreetingService.class);
		random = new Random();
		loadStyle(DEFAULT_STYLE, new Listener() {

			@Override
			public void notifyMe() {
				// TODO Auto-generated method stub

			}

			@Override
			public void notifyFail(Throwable aCaught) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	public String createText(int numParagraph, int aNumSentences) {
		String result = "";

		for (int para = 0; para < numParagraph; para++) {
			int numSentences = random.nextInt(aNumSentences) + (aNumSentences / 2);
			for (int sentence = 0; sentence < numSentences; sentence++) {
				String s = createSentence();
				result = result + s + " ";
			}
			result = result + "\n\n";
		}
		return result;
	}

	public void loadStyle(final String aStyleName, final Listener aListener) {
		AsyncCallback<Style> callback = new AsyncCallback<Style>() {

			@Override
			public void onFailure(Throwable aCaught) {
				aListener.notifyMe();
			}

			@Override
			public void onSuccess(Style aStyle) {
				currentStyle = aStyle;
				aListener.notifyMe();
			}
		};
		getClient().getStyle(aStyleName, callback);
	}

	public void saveStyle(final Listener aListener) {
		AsyncCallback<Void> callback = new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable aCaught) {
				aListener.notifyFail(aCaught);
			}

			@Override
			public void onSuccess(Void blubber) {
				aListener.notifyMe();
			}
		};
		getClient().saveStyle(currentStyle, callback);
	}

	// private void getStyles() {
	// setAvailableStyles(new String[0]);
	// AsyncCallback<String[]> callback = new AsyncCallback<String[]>() {
	//
	// @Override
	// public void onFailure(Throwable aCaught) {
	// AlertBox.alertWidget("Error",
	// "Could not load the list of styles, using default settings instead.");
	// setAvailableStyles(new String[] { DEFAULT_STYLE });
	// }
	//
	// @Override
	// public void onSuccess(String[] aResult) {
	// setAvailableStyles(aResult);
	// }
	// };
	// getClient().getStyleNames(callback);
	// }

	private String createSentence() {

		WordList numbersSingular = getCurrentStyle().getWordsMap().get(WordTypes.NUMBER_SINGULAR);
		WordList attributesSubjectSingular = getCurrentStyle().getWordsMap().get(WordTypes.ATTRIBUTE_OF_SUBJECT_SINGULAR);
		WordList subjectsSingular = getCurrentStyle().getWordsMap().get(WordTypes.NOUN_SINGULAR);
		WordList verbsSingular = getCurrentStyle().getWordsMap().get(WordTypes.VERB_SINGULAR);
		WordList numbersPlural = getCurrentStyle().getWordsMap().get(WordTypes.NUMBER_PLURAL);
		WordList attributesSubjectPlural = getCurrentStyle().getWordsMap().get(WordTypes.ATTRIBUTE_OF_SUBJECT_PLURAL);
		WordList subjectsPlural = getCurrentStyle().getWordsMap().get(WordTypes.NOUN_PLURAL);
		WordList verbsPlural = getCurrentStyle().getWordsMap().get(WordTypes.VERB_PLURAL);
		WordList attributesObject = getCurrentStyle().getWordsMap().get(WordTypes.ATTRIBUTE_OF_OBJECT);
		WordList prepositions = getCurrentStyle().getWordsMap().get(WordTypes.PREPOSITION);
		WordList possessivePronouns = getCurrentStyle().getWordsMap().get(WordTypes.POSSESSIVE_PRONOUN);
		WordList punctuations = getCurrentStyle().getWordsMap().get(WordTypes.PUNCTUATION);

		ArrayList<String> words = new ArrayList<String>();

		boolean plural = random.nextBoolean();

		if (plural) {
			String noun = randomWord(subjectsPlural).trim();
			int articleIdx = noun.indexOf(" ");
			if (articleIdx > 0) {
				noun = noun.substring(articleIdx + 1);
			}

			addWord(words, firstToUpper(randomWord(numbersPlural)));
			addWord(words, randomWord(attributesSubjectPlural));
			addWord(words, noun);
			addWord(words, randomWord(verbsPlural));
		} else {
			String article = "";
			String noun = randomWord(subjectsSingular).trim();
			int articleIdx = noun.indexOf(" ");
			if (articleIdx > 0) {
				article = firstToUpper(noun.substring(0, articleIdx));
				noun = firstToUpper(noun.substring(articleIdx + 1));
			} else {
				article = firstToUpper(randomWord(numbersSingular));
			}

			addWord(words, article);
			addWord(words, randomWord(attributesSubjectSingular));
			addWord(words, noun);
			addWord(words, randomWord(verbsSingular));
		}

		if (random.nextBoolean()) {
			addWord(words, randomWord(attributesObject));
		}
		addWord(words, randomWord(prepositions));
		addWord(words, randomWord(possessivePronouns));

		addWordNoSpace(words, getSingularNoun());

		String ending = randomWord(punctuations);
		addWordNoSpace(words, ending);

		while (isNotFinal(ending)) {
			words.add(" ");
			addWord(words, randomWord(words, attributesObject));
			addWord(words, randomWord(words, prepositions));
			addWord(words, randomWord(possessivePronouns));
			addWordNoSpace(words, getSingularNoun());
			ending = randomWord(punctuations);
			addWordNoSpace(words, ending);
		}

		StringBuffer buf = new StringBuffer();
		for (String string : words) {
			buf.append(string);
		}
		return buf.toString();

	}

	private String getSingularNoun() {
		WordList subjectsSingular = getCurrentStyle().getWordsMap().get(WordTypes.NOUN_SINGULAR);
		String noun = randomWord(subjectsSingular).trim();
		int articleIdx = noun.indexOf(" ");
		if (articleIdx > 0) {
			return firstToUpper(noun.substring(articleIdx + 1));
		}
		return noun;
	}

	private String randomWord(List<String> aUsedWords, WordList aSet) {
		String res = randomWord(aSet);
		while (aUsedWords.contains(res)) {
			res = randomWord(aSet);
		}
		return res;
	}

	private String randomWord(WordList aSet) {
		if (aSet.getWords().size() == 0) {
			return "";
		}
		return aSet.getRandomWord();
	}

	private void addWord(List<String> words, String aWord) {
		if (aWord.length() > 0) {
			words.add(aWord + " ");
		}
	}

	private void addWordNoSpace(List<String> words, String aWord) {
		words.add(aWord);
	}

	private boolean isNotFinal(String aEnding) {
		if ((",;:".indexOf(aEnding) >= 0) || (" -".equals(aEnding))) {
			return true;
		} else {
			return false;
		}
	}

	private String firstToUpper(String aRandomWord) {
		if (aRandomWord.length() > 1) {
			return aRandomWord.substring(0, 1).toUpperCase() + aRandomWord.substring(1);
		} else {
			return aRandomWord.toUpperCase();
		}
	}

	public void getStyleNames(final Listener aListener) {
		AsyncCallback<String[]> callback = new AsyncCallback<String[]>(
				) {
			
			@Override
			public void onSuccess(String[] result) {
				availableStyles = result;
				aListener.notifyMe();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				availableStyles = new String[0];
				aListener.notifyFail(caught);
			}
		};
		client.getStyleNames(callback);
	}

	private void setAvailableStyles(String[] availableStyles) {
		this.availableStyles = availableStyles;
	}

	public GreetingServiceAsync getClient() {
		return client;
	}

	public Style getCurrentStyle() {
		if (currentStyle == null) {
			return DefaultWordLists.defaultStyle;
		}
		return currentStyle;
	}

	public String[] getAvailableStyles() {
		return availableStyles;
	}

}
