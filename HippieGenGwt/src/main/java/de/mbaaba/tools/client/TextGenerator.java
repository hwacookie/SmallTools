package de.mbaaba.tools.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.mbaaba.tools.shared.Style;
import de.mbaaba.tools.shared.WordList;
import de.mbaaba.tools.shared.WordTypes;

public class TextGenerator {

	private Style currentStyle;

	private Random random = new Random();

	public TextGenerator(Style aStyle) {
		currentStyle = aStyle;
	}

	public String createText(int numParagraph, int aNumSentences) {
		x=0;
		String result = "";

		for (int para = 0; para < numParagraph; para++) {
			int numSentences = random.nextInt(aNumSentences)
					+ (aNumSentences / 2);
			for (int sentence = 0; sentence < numSentences; sentence++) {
				String s = createSentence();
				result = result + s + " ";
			}
			result = result + "\n\n";
		}
		return result;
	}

	private String createSentence() {

		WordList numbersSingular = getCurrentStyle().getWordsMap().get(
				WordTypes.NUMBER_SINGULAR);
		WordList attributes = getCurrentStyle().getWordsMap().get(
				WordTypes.ATTRIBUTES);
		WordList nounsSingular = getCurrentStyle().getWordsMap().get(
				WordTypes.NOUN_SINGULAR);
		WordList verbsSingular = getCurrentStyle().getWordsMap().get(
				WordTypes.VERB_SINGULAR);
		WordList numbersPlural = getCurrentStyle().getWordsMap().get(
				WordTypes.NUMBER_PLURAL);
		WordList nounsPlural = getCurrentStyle().getWordsMap().get(
				WordTypes.NOUN_PLURAL);
		WordList verbsPlural = getCurrentStyle().getWordsMap().get(
				WordTypes.VERB_PLURAL);
		WordList prepositions = getCurrentStyle().getWordsMap().get(
				WordTypes.PREPOSITION);
		WordList possessivePronouns = getCurrentStyle().getWordsMap().get(
				WordTypes.POSSESSIVE_PRONOUN);
		WordList punctuations = getCurrentStyle().getWordsMap().get(
				WordTypes.PUNCTUATION);

		ArrayList<String> words = new ArrayList<String>();

		boolean plural = random.nextBoolean();

		if (plural) {
			addWord(words, firstToUpper(randomWord(numbersPlural)));
			addWord(words, randomWord(attributes));
			addWord(words, randomWord(nounsPlural).trim());
			addWord(words, randomWord(verbsPlural));
		} else {
			addWord(words, firstToUpper(randomWord(numbersSingular)));
			addWord(words, randomWord(attributes));
			addWord(words, randomWord(nounsSingular).trim());
			addWord(words, randomWord(verbsSingular));
		}

		if (random.nextBoolean()) {
			addWord(words, randomWord(attributes));
		}
		addWord(words, randomWord(prepositions));
		addWord(words, randomWord(possessivePronouns));

		addWordNoSpace(words, getSingularNoun());

		String ending = randomWord(punctuations);
		addWordNoSpace(words, ending);

		while (isNotFinal(ending)) {
			words.add(" ");
			addWord(words, randomWord(words, attributes));
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
		WordList subjectsSingular = getCurrentStyle().getWordsMap().get(
				WordTypes.NOUN_SINGULAR);
		return randomWord(subjectsSingular).trim();
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

	int x = 0;
	private void addWord(List<String> words, String aWord) {
		if (aWord.length() > 0) {
			words.add(aWord + " ");
			SelectionEvent event = new SelectionEvent(aWord);
			if (x<5) {
				NotificationManager.getInstance().fireSelectionEvent(event);
				x++;
			}
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
			return aRandomWord.substring(0, 1).toUpperCase()
					+ aRandomWord.substring(1);
		} else {
			return aRandomWord.toUpperCase();
		}
	}

	private Style getCurrentStyle() {
		return currentStyle;
	}
}
