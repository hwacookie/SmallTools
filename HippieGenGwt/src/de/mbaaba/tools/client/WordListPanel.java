package de.mbaaba.tools.client;

import gdurelle.tagcloud.client.tags.TagCloud;
import gdurelle.tagcloud.client.tags.WordTag;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gwt.user.client.ui.DecoratedStackPanel;

import de.mbaaba.tools.shared.Style;
import de.mbaaba.tools.shared.WordList;
import de.mbaaba.tools.shared.WordTypes;

public class WordListPanel extends DecoratedStackPanel {

	private Map<WordTypes, TagCloud> areas = new HashMap<WordTypes, TagCloud>();
	private Style currentStyle;

	public WordListPanel() {
		super();

		NotificationManager.getInstance().addListener(new TypedListener<StyleEvent>() {

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
		setStyleName("gwt-StackPanel");
		createLists();

	}

	private void createLists() {
		createWordGroup(this, WordTypes.NUMBER_SINGULAR);
		createWordGroup(this, WordTypes.NOUN_SINGULAR);
		createWordGroup(this, WordTypes.VERB_SINGULAR);

		createWordGroup(this, WordTypes.NUMBER_PLURAL);
		createWordGroup(this, WordTypes.NOUN_PLURAL);
		createWordGroup(this, WordTypes.VERB_PLURAL);

		createWordGroup(this, WordTypes.ATTRIBUTES);
		createWordGroup(this, WordTypes.PREPOSITION);
		createWordGroup(this, WordTypes.POSSESSIVE_PRONOUN);
		createWordGroup(this, WordTypes.PUNCTUATION);
	}

	private void createWordGroup(DecoratedStackPanel wordListStackPanel,
			final WordTypes aWordType) {
		TagCloud tagCloud = new TagCloud();
		tagCloud.setColored(true);
		wordListStackPanel.add(tagCloud, aWordType.toString(), false);
		tagCloud.setWidth("300px");
		areas.put(aWordType, tagCloud);
	}

	
	private void setCurrentStyle(Style aStyle) {
		Random random = new Random();
		currentStyle = aStyle;
		Collection<TagCloud> textAreas = areas.values();
		for (TagCloud textArea : textAreas) {
			textArea.getTags().clear();
		}
		
		if (currentStyle != null) {
			Set<Entry<WordTypes, WordList>> entrySet = currentStyle
					.getWordsMap().entrySet();
			for (Entry<WordTypes, WordList> entry : entrySet) {
				TagCloud textArea = areas.get(entry.getKey());
				WordList wordList = entry.getValue();
				
				List<String> words = wordList.getWords();
				
				for (String string : words) {
					WordTag wordTag = new WordTag(string);
					wordTag.setNumberOfOccurences(random.nextInt(5));
					textArea.addWord(wordTag);
				}
			}
		}

	}
//
//	public void reparseWordLists() {
//		Set<Entry<WordTypes, TextArea>> entrySet = areas.entrySet();
//		for (Entry<WordTypes, TextArea> entry : entrySet) {
//			WordList wordList = currentStyle.getWordsMap().get(entry.getKey());
//			wordList.parse(entry.getValue().getText(), NEWLINE);
//		}
//
//	}

}
