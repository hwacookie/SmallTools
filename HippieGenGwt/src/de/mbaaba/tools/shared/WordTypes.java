package de.mbaaba.tools.shared;

public enum WordTypes {
	PUNCTUATION("Punctuation"), POSSESSIVE_PRONOUN("Possessive pronoun"), PREPOSITION(
			"Preposition"), ATTRIBUTE_OF_OBJECT("Attribute of object"), VERB_PLURAL(
			"Verb (plural)"), NOUN_PLURAL("Substantiv (plural)"), ATTRIBUTE_OF_SUBJECT_PLURAL(
			"Attribute of subject (plural)"), NUMBER_PLURAL("Number (plural)"), VERB_SINGULAR(
			"Verb (singular)"), NOUN_SINGULAR("Substantiv (singular)"), ATTRIBUTE_OF_SUBJECT_SINGULAR(
			"Attribute of subject (singular)"), NUMBER_SINGULAR(
			"Number (singular)");

	private String userName;

	private WordTypes(String aUserName) {
		userName = aUserName;
	}
	
	@Override
	public String toString() {
		return userName;
	}
};
