package de.mbaaba.tools.shared;

import java.util.HashMap;
import java.util.Map;

public class DefaultWordLists {

	static String numbersSingularDefaults = "eine\nein\nmanch\nder\ndie\ndas";
	static String numbersPluralDefaults = "viele\nmanche\ntausend\nmehrere\ndutzende\ndie";
	static String attributesSubjectPluralDefaults = "orange\nblumige\nfreie\nsingende\nlachende\nweinende\nnachdenkliche\ngrübelnde\ntanzende";
	static String attributesSubjectSingularDefaults = "orangenen\nblumigen\nfreien\nsingenden\nlachenden\nweinende\nnachdenkliche\ngrübelnde\ntanzende";
	static String subjectsSingularDefaults = "Hippie\nJoint\nTüte\nKiffer\nBlume\nGras\nPolizist\nProtest\nSoftie\nGitarrist";
	static String subjectsPluralDefaults = "Hippies\nJoints\nTüten\nKiffer\nBlumen\nPolizisten";
	static String verbsSingularDefaults = "raucht\nvögelt\nkifft\nliebt\nschläft\nprotestiert\nträumt\nfühlt\ndenkt\nisst\ntrinkt\ndemonstriert\nstreikt"
			+ "\n" + "schnarcht\nküsst";
	static String verbsPluralDefaults = "rauchen\nvögeln\nkiffen\nlieben\nschlafen\nprotestieren\nträumen\nfühlen\ndenken\nessen\ntrinken";
	static String attributesObjectDefaults = "lebendig\nfröhlich\norange\nblumig\nfrei\nsingend\nlachend\nweinend\nnachdenklich\ngrübelnd";
	static String prepositionsDefaults = "in\nan\nzu\nbei\nauf\nneben\nunter\nam\nvon\nim";
	static String possessivePronounsDefaults = "der\ndie\ndas\ndem\nseiner\nihrer\nmeiner\ndeiner\nunserer\nseine\nihre\nmeine\ndeine\nunsere";
	static String punctuationsDefaults = ".\n.\n.\n.\n!\n!!\n?\n?!\n:\n,\n;\n:\n";
	public static Style defaultStyle;

	static {
		Map<WordTypes, WordList> hippieDefaults = new HashMap<WordTypes, WordList>();
		hippieDefaults.put(WordTypes.NUMBER_SINGULAR, new WordList(WordTypes.NUMBER_SINGULAR, numbersSingularDefaults));
		hippieDefaults.put(WordTypes.ATTRIBUTE_OF_SUBJECT_SINGULAR, new WordList(WordTypes.ATTRIBUTE_OF_SUBJECT_SINGULAR, attributesSubjectSingularDefaults));
		hippieDefaults.put(WordTypes.NOUN_SINGULAR, new WordList(WordTypes.NOUN_SINGULAR, subjectsSingularDefaults));
		hippieDefaults.put(WordTypes.VERB_SINGULAR, new WordList(WordTypes.VERB_SINGULAR, verbsSingularDefaults));
		hippieDefaults.put(WordTypes.NUMBER_PLURAL, new WordList(WordTypes.NUMBER_PLURAL, numbersPluralDefaults));
		hippieDefaults.put(WordTypes.ATTRIBUTE_OF_SUBJECT_PLURAL, new WordList(WordTypes.ATTRIBUTE_OF_SUBJECT_PLURAL, attributesSubjectPluralDefaults));
		hippieDefaults.put(WordTypes.NOUN_PLURAL, new WordList(WordTypes.NOUN_PLURAL, subjectsPluralDefaults));
		hippieDefaults.put(WordTypes.VERB_PLURAL, new WordList(WordTypes.VERB_PLURAL, verbsPluralDefaults));
		hippieDefaults.put(WordTypes.ATTRIBUTE_OF_OBJECT, new WordList(WordTypes.ATTRIBUTE_OF_OBJECT, attributesObjectDefaults));
		hippieDefaults.put(WordTypes.PREPOSITION, new WordList(WordTypes.PREPOSITION, prepositionsDefaults));
		hippieDefaults.put(WordTypes.POSSESSIVE_PRONOUN, new WordList(WordTypes.POSSESSIVE_PRONOUN, possessivePronounsDefaults));
		hippieDefaults.put(WordTypes.PUNCTUATION, new WordList(WordTypes.PUNCTUATION, punctuationsDefaults));

		defaultStyle = new Style("Default", "<h3>Default theme</h3>", hippieDefaults);
	}

//
//	public static String getByType(WordTypes aType) {
//		return hippieDefaults.get(aType);
//	}
}
