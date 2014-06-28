package de.mbaaba.tools.shared;

import java.util.HashMap;

public class Properties {

	private HashMap<String, String> data = new HashMap<String, String>();

	public String getProperty(String name) {
		return data.get(name.toLowerCase());
	}

	public void load(String text) {
		String[] split = text.split(WordList.NEWLINE);
		for (String string : split) {
			if (string.indexOf("=") > 0) {
				String[] split2 = string.split("=");
				String key = split2[0].toLowerCase();
				String value = split2[1];
				data.put(key,  value);
			}
		}
	}

}
