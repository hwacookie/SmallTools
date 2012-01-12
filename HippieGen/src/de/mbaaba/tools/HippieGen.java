package de.mbaaba.tools;

import java.util.ArrayList;
import java.util.Random;

public class HippieGen {
	String[] numbersSingular = new String[] { "ein", "manch", "der", "die", "das" };
	String[] numbersPlural = new String[] { "viele", "manche", "tausend", "mehrere", "dutzende", "die" };
	String[] praeds1 = new String[] { "orange", "blumige", "freie", "singende", "lachende", "weinende", "nachdenkliche", "grübelnde", "tanzende" };
	String[] subjectsSingular = new String[] { "Hippie", "Joint", "Tüte", "Kiffer", "Blume", "Gras", "Polizist", "Protest", "Softie", "Gitarrist" };
	String[] subjectsPlural = new String[] { "Hippies", "Joints", "Tüten", "Kiffer", "Blumen", "Polizisten" };
	String[] verbsSingular = new String[] { "raucht", "vögelt", "kifft", "liebt", "schläft", "protestiert", "träumt", "fühlt", "denkt", "isst", "trinkt", "demonstriert", "streikt", "schnarcht",
			"küsst" };
	String[] verbsPlural = new String[] { "rauchen", "vögeln", "kiffen", "lieben", "schlafen", "protestieren", "träumen", "fühlen", "denken", "essen", "trinken" };
	String[] praeds = new String[] { "lebendig", "fröhlich", "orange", "blumig", "frei", "singend", "lachend", "weinend", "nachdenklich", "grübelnd" };
	String[] prePoss = new String[] { "in", "an", "zu", "bei", "auf", "neben", "unter", "am", "von", "im" };
	String[] whose = new String[] { "der", "die", "das", "dem", "seiner", "ihrer", "meiner", "deiner", "unserer", "seine", "ihre", "meine", "deine", "unsere" };
	String[] objects = new String[] { "Frau", "Mann", "Gitarre", "San Francisco", "New York", "Bett", "Bus", "VW-Bus", "Bully", "Wiese", "Polizei", "Bett", "Futon", "See", "Strand", "Palme",
			"Bundeswehr", "Demonstration", "Protest" };
	String[] endings = new String[] { ".", ".", ".", ".", "!", "!!", "?", "?!", ":", ",", ",", ",", ";" };
	String[] finals = new String[] { ".", ".", ".", ".", "!", "!!", "?", "?!" };
	private Random random;

	public static void main(String[] args) {
		new HippieGen(10);
	}

	public HippieGen(int numParagraph) {
		random = new Random();

		for (int para = 0; para < numParagraph; para++) {
			int numSentences = random.nextInt(5) + 3;
			for (int sentence = 0; sentence < numSentences; sentence++) {
				String s = createSentence();
				System.out.print(s + " ");
			}
			System.out.println();
			System.out.println();
		}
	}

	private String createSentence() {

		ArrayList<String> words = new ArrayList<String>();

		boolean plural = random.nextBoolean();

		if (plural) {
			addWord(words, firstToUpper(randomWord(numbersPlural)));
			addWord(words, randomWord(praeds1));
			addWord(words, randomWord(subjectsPlural));
			addWord(words, randomWord(verbsPlural));
		} else {
			addWord(words, firstToUpper(randomWord(numbersSingular)));
			addWord(words, randomWord(subjectsSingular));
			addWord(words, randomWord(verbsSingular));
		}

		if (random.nextBoolean()) {
			addWord(words, randomWord(praeds));
		}
		addWord(words, randomWord(prePoss));
		addWord(words, randomWord(whose));

		addWordNoSpace(words, randomWord(objects));

		String ending = randomWord(endings);
		addWordNoSpace(words, ending);

		if (isNotFinal(ending)) {
			addWord(words, "");
			addWord(words, randomWord(words, praeds));
			addWord(words, randomWord(words, prePoss));
			addWord(words, randomWord(whose));
			addWordNoSpace(words, randomWord(words, objects));
			addWordNoSpace(words, randomWord(finals));
		}

		StringBuffer buf = new StringBuffer();
		for (String string : words) {
			buf.append(string);
		}
		return buf.toString();

	}

	private String randomWord(ArrayList<String> aWords, String[] aSet) {
		String res = randomWord(aSet);
		while (aWords.contains(res)) {
			res = randomWord(aSet);
		}
		return res;
	}

	private void addWord(ArrayList<String> words, String aWord) {
		words.add(aWord + " ");
	}

	private void addWordNoSpace(ArrayList<String> words, String aWord) {
		words.add(aWord);
	}

	private boolean isNotFinal(String aEnding) {
		return ",;:".indexOf(aEnding) >= 0;
	}

	private String firstToUpper(String aRandomWord) {
		if (aRandomWord.length() > 1) {
			return aRandomWord.substring(0, 1).toUpperCase() + aRandomWord.substring(1);
		} else {
			return aRandomWord.toUpperCase();
		}
	}

	private String randomWord(String[] aSet) {
		return aSet[random.nextInt(aSet.length)];
	}
}
