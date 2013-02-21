package de.mbaaba.tools.client;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gwt.user.client.ui.DecoratedStackPanel;
import com.google.gwt.user.client.ui.TextArea;

import de.mbaaba.tools.shared.Style;
import de.mbaaba.tools.shared.WordList;
import de.mbaaba.tools.shared.WordTypes;

public class WordListPanel extends DecoratedStackPanel {

	private Map<WordTypes, TextArea> areas = new HashMap<WordTypes, TextArea>();
	private Style currentStyle;

	public WordListPanel() {
		super();
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

	private void createWordGroup(DecoratedStackPanel wordListStackPanel, final WordTypes aWordType) {
		// WordList wordList =
		// hippieGen.getCurrentStyle().getWordsMap().get(aWordType);
		final TextArea txtArea = new TextArea();
		txtArea.setVisibleLines(14);
		wordListStackPanel.add(txtArea, aWordType.toString(), false);
		txtArea.setSize("300px\r\n", "300px");
		txtArea.setText("");
		areas.put(aWordType, txtArea);
	}

	public void setCurrentStyle(Style aStyle) {
		currentStyle = aStyle;
		Collection<TextArea> textAreas = areas.values();
		for (TextArea textArea : textAreas) {
			textArea.setText("");
		}
		if (aStyle != null) {
			Set<Entry<WordTypes, WordList>> entrySet = aStyle.getWordsMap().entrySet();
			for (Entry<WordTypes, WordList> entry : entrySet) {
				TextArea textArea = areas.get(entry.getKey());
				textArea.setText(entry.getValue().buildString());
			}
		}

	}

	public void reparseWordLists() {
		Set<Entry<WordTypes, TextArea>> entrySet = areas.entrySet();
		for (Entry<WordTypes, TextArea> entry : entrySet) {
			WordList wordList = currentStyle.getWordsMap().get(entry.getKey());
			wordList.parse(entry.getValue().getText());
		}

	}

}
