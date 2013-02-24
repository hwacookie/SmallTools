package de.mbaaba.tools.client;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.user.client.ui.DecoratedStackPanel;
import com.google.gwt.user.client.ui.TextArea;

import de.mbaaba.tools.shared.Style;
import de.mbaaba.tools.shared.WordList;
import de.mbaaba.tools.shared.WordTypes;

public class WordListPanel extends DecoratedStackPanel {

	private static final String NEWLINE = "\\n";
	private Map<WordTypes, TextArea> areas = new HashMap<WordTypes, TextArea>();
	private Style currentStyle;

	public WordListPanel() {
		super();

		StyleManager.getInstance().addListener(new TypedListener<StyleEvent>() {

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
		// WordList wordList =
		// hippieGen.getCurrentStyle().getWordsMap().get(aWordType);
		final TextArea txtArea = new TextArea();
		txtArea.setVisibleLines(14);
		wordListStackPanel.add(txtArea, aWordType.toString(), false);
		txtArea.setSize("300px", "300px");
		txtArea.setText("");
		txtArea.addBlurHandler(new BlurHandler() {

			@Override
			public void onBlur(BlurEvent event) {
				// automatically reparse word list when textArea looses focus
				if (currentStyle != null) {
					WordList wordList = currentStyle.getWordsMap().get(
							aWordType);
					wordList.parse(txtArea.getText(), NEWLINE);
				}
			}
		});
		areas.put(aWordType, txtArea);
	}

	private void setCurrentStyle(Style aStyle) {
		currentStyle = aStyle;
		Collection<TextArea> textAreas = areas.values();
		for (TextArea textArea : textAreas) {
			textArea.setText("");
		}
		if (currentStyle != null) {
			Set<Entry<WordTypes, WordList>> entrySet = currentStyle
					.getWordsMap().entrySet();
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
			wordList.parse(entry.getValue().getText(), NEWLINE);
		}

	}

}
