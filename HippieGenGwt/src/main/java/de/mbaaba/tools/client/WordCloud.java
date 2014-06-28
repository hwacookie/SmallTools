/* --------------------------------------------------------------------------
 * @author walden_h1
 * @created 21.05.2014
 * Copyright 2014 by Biotronik SE & Co. KG
 * All rights reserved.
 * --------------------------------------------------------------------------
 */

package de.mbaaba.tools.client;

import gdurelle.tagcloud.client.tags.WordTag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import com.google.gwt.user.client.ui.FlowPanel;

import de.mbaaba.tools.shared.Style;
import de.mbaaba.tools.shared.WordList;
import de.mbaaba.tools.shared.WordTypes;

/**
 * 
 * 
 * @author walden_h1
 * @created 21.05.2014
 */
public class WordCloud extends FlowPanel {

	private Style currentStyle;
	private TagCloud2 tagCloud;

	public WordCloud() {
		super();

		NotificationManager.getInstance().addListener(
				new TypedListener<StyleEvent>() {

					@Override
					public void notifyMe(StyleEvent aResult) {
						switch (aResult.action) {
						case CHANGED:
							setCurrentStyle(aResult.style);
						default:
							break;
						}
					}

					@Override
					public void notifyFail(Throwable aCaught) {
					}
				});

		NotificationManager.getInstance().addSelectionListener(
				new TypedListener<SelectionEvent>() {

					@Override
					public void notifyMe(SelectionEvent aResult) {
						tagCloud.animateWord(aResult.word);
					}

					@Override
					public void notifyFail(Throwable aCaught) {
					}
				});

		tagCloud = new TagCloud2();
		tagCloud.setWidth("300px");
		add(tagCloud);
	}

	private void setCurrentStyle(Style aStyle) {
		Random random = new Random();
		currentStyle = aStyle;
		tagCloud.removeAllTags();

		List<WordTag> allWordTags = new ArrayList<WordTag>();
		if (currentStyle != null) {
			Set<Entry<WordTypes, WordList>> entrySet = currentStyle
					.getWordsMap().entrySet();

			for (Entry<WordTypes, WordList> entry : entrySet) {
				String color = getWordColor(entry.getKey());
				WordList wordList = entry.getValue();
				List<String> words = wordList.getWords();
				for (String string : words) {
					WordTag wordTag = new WordTag();
					wordTag.setColor(color);
					wordTag.setNumberOfOccurences(random.nextInt(5));
					wordTag.setWord(string);
					allWordTags.add(wordTag);
				}
				
			}
		}

		while (allWordTags.size() > 0) {
			int index = random.nextInt(allWordTags.size());
			WordTag wordTag = allWordTags.get(index);
			tagCloud.addWord(wordTag);
			allWordTags.remove(index);
		}
		tagCloud.refresh();

	}

	private String getWordColor(WordTypes key) {
		switch (key) {
		case NUMBER_SINGULAR:
			return "orange";
		case NOUN_SINGULAR:
			return "red";
		case VERB_SINGULAR:
			return "green";
		case NUMBER_PLURAL:
			return "lightblue";
		case NOUN_PLURAL:
			return "purple";
		case VERB_PLURAL:
			return "blue";
		case ATTRIBUTES:
			return "pink";
		case PREPOSITION:
			return "brown";
		case POSSESSIVE_PRONOUN:
			return "lightgrey";
		case PUNCTUATION:
			return "grey";
		default:
			return "darkgrey";
		}
	}

}
