package de.mbaaba.tools.shared;

import java.util.HashMap;
import java.util.Map;

public class DefaultWordLists {

	static String numbersSingularDefaults = "eine\nein\nmanch\nder\ndie\ndas";
	static String numbersPluralDefaults = "viele\nmanche\ntausend\nmehrere\ndutzende\ndie";
	static String attributes = "orange\nblumige\nfreie\nsingende\nlachende\nweinende\nnachdenkliche\ngrübelnde\ntanzende";
	static String subjectsSingularDefaults = "Hippie\nJoint\nTüte\nKiffer\nBlume\nGras\nPolizist\nProtest\nSoftie\nGitarrist";
	static String subjectsPluralDefaults = "Hippies\nJoints\nTüten\nKiffer\nBlumen\nPolizisten";
	static String verbsSingularDefaults = "raucht\nvögelt\nkifft\nliebt\nschläft\nprotestiert\nträumt\nfühlt\ndenkt\nisst\ntrinkt\ndemonstriert\nstreikt"
			+ "\n" + "schnarcht\nküsst";
	static String verbsPluralDefaults = "rauchen\nvögeln\nkiffen\nlieben\nschlafen\nprotestieren\nträumen\nfühlen\ndenken\nessen\ntrinken";
	static String prepositionsDefaults = "in\nan\nzu\nbei\nauf\nneben\nunter\nam\nvon\nim";
	static String possessivePronounsDefaults = "der\ndie\ndas\ndem\nseiner\nihrer\nmeiner\ndeiner\nunserer\nseine\nihre\nmeine\ndeine\nunsere";
	static String punctuationsDefaults = ".\n.\n.\n.\n!\n!!\n?\n?!\n:\n,\n;\n:\n";

	static String defaultStyleNames = "Hippie\nGarten\nReise\nStartrek";
	public static Style defaultStyle;

	static {
		Map<WordTypes, WordList> defaultWords = new HashMap<WordTypes, WordList>();
		defaultWords.put(WordTypes.NUMBER_SINGULAR, new WordList(WordTypes.NUMBER_SINGULAR, numbersSingularDefaults));
		defaultWords.put(WordTypes.ATTRIBUTES, new WordList(WordTypes.ATTRIBUTES, attributes));
		defaultWords.put(WordTypes.NOUN_SINGULAR, new WordList(WordTypes.NOUN_SINGULAR, subjectsSingularDefaults));
		defaultWords.put(WordTypes.VERB_SINGULAR, new WordList(WordTypes.VERB_SINGULAR, verbsSingularDefaults));
		defaultWords.put(WordTypes.NUMBER_PLURAL, new WordList(WordTypes.NUMBER_PLURAL, numbersPluralDefaults));
		defaultWords.put(WordTypes.NOUN_PLURAL, new WordList(WordTypes.NOUN_PLURAL, subjectsPluralDefaults));
		defaultWords.put(WordTypes.VERB_PLURAL, new WordList(WordTypes.VERB_PLURAL, verbsPluralDefaults));
		defaultWords.put(WordTypes.PREPOSITION, new WordList(WordTypes.PREPOSITION, prepositionsDefaults));
		defaultWords.put(WordTypes.POSSESSIVE_PRONOUN, new WordList(WordTypes.POSSESSIVE_PRONOUN, possessivePronounsDefaults));
		defaultWords.put(WordTypes.PUNCTUATION, new WordList(WordTypes.PUNCTUATION, punctuationsDefaults));
		defaultStyle = new Style("Default", "<h3>Default theme</h3>", defaultWords);
	}
}
