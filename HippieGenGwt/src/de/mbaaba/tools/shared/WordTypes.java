package de.mbaaba.tools.shared;

public enum WordTypes {
	PUNCTUATION("Punctuation"),
	POSSESSIVE_PRONOUN("Possessive pronoun"),
	PREPOSITION("Preposition"),

	VERB_PLURAL("Verb (plural)"),
	NOUN_PLURAL("Nouns (plural)"),
	NUMBER_PLURAL("Articles and numerals (plural)"),

	VERB_SINGULAR("Verb (singular)"),
	NOUN_SINGULAR("Noun (singular)"),
	NUMBER_SINGULAR("Articles and numerals (singular)"),
	ATTRIBUTES("Attributes");

	private String userName;

	private WordTypes(String aUserName) {
		userName = aUserName;
	}

	@Override
	public String toString() {
		return userName;
	}
};
