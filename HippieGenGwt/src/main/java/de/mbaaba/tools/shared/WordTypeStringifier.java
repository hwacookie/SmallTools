package de.mbaaba.tools.shared;

import com.googlecode.objectify.stringifier.Stringifier;

public class WordTypeStringifier implements Stringifier<WordTypes> {
	@Override
	public String toString(WordTypes obj) {
		return obj.name();
	}

	@Override
	public WordTypes fromString(String str) {
		return WordTypes.valueOf(str);
	}
}
