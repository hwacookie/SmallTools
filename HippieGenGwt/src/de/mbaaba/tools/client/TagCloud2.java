package de.mbaaba.tools.client;

import gdurelle.tagcloud.client.tags.Tag;
import gdurelle.tagcloud.client.tags.WordTag;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineHTML;

public class TagCloud2 extends Composite {

	private FlowPanel cloud;
	private Map<WordTag, InlineHTML> tags;
	private int maxNumberOfTags;// the number of tags shown in the cloud.
	private double minOccurences, maxOccurences, step;
	private boolean isColored;

	private static final int STEP_NUMBER = 10;

	public TagCloud2() {
		cloud = new FlowPanel();
		tags = new HashMap<WordTag, InlineHTML>();
		maxNumberOfTags = 20;
		minOccurences = 1;
		maxOccurences = 1;
		step = 1;// 'average' difference between each occurence
		isColored = true;
		DecoratorPanel dec = new DecoratorPanel();
		dec.setWidget(cloud);
		initWidget(dec);
	}

	public void animateWord(String aWord) {
		Element element = getElement(aWord);
		if (element != null) {
			FadeAnimation fadeAnimation = new FadeAnimation(element);
			fadeAnimation.fade(2000, 1.0);
		}

	}

	private Element getElement(String aWord) {
		for (WordTag tag : tags.keySet()) {
			if (tag.getWord().equals(aWord)) {
				InlineHTML inlineHTML = tags.get(tag);
				return inlineHTML.getElement();
			}
		}
		return null;
	}

	/**
	 * Refresh the display of the tagcloud. Usually used after an adding or
	 * deletion of word.
	 */
	public void refresh() {
		cloud.clear();
		if (tags != null && !tags.isEmpty()) {
			// recalculate max and min of all occurences
			for (Tag w : tags.keySet()) {
				if (w.getNumberOfOccurences() > maxOccurences)
					maxOccurences = w.getNumberOfOccurences();
				if (w.getNumberOfOccurences() < minOccurences)
					minOccurences = w.getNumberOfOccurences();
			}

			// a step correspond to a css style.
			step = (maxOccurences - minOccurences) / STEP_NUMBER;

			for (WordTag w : tags.keySet()) {
				InlineHTML inline = tags.get(w);
				if (inline == null) {
					inline = getInlineHTML((WordTag) w);
					tags.put(w, inline);
				}
				inline.getElement().getStyle().setOpacity(0.3);
				cloud.add(inline);
			}
		}
	}

	/**
	 * Create the 'CSS' aspect of the given word thanks the whole minimum,
	 * maximum, and average number of occurences of all words. It create a link
	 * in a span with the appropriate font style/size
	 * 
	 * @param w
	 *            The Word object to display
	 * @return The InlinHTML object that fits in the cloud
	 */
	private InlineHTML getInlineHTML(WordTag w) {
		int nboc = w.getNumberOfOccurences();

		InlineHTML inline = new InlineHTML(" <a>" + w.getWord() + "</a>&nbsp;");
		inline.addStyleName("tag");

		if (w.getOrientation() == Tag.VERTICAL_LEFT)
			inline.addStyleName("verticalL");
		else if (w.getOrientation() == Tag.VERTICAL_RIGHT)
			inline.addStyleName("verticalR");

		// Apply the good style corersponding to the number of occurences
		if (nboc >= (maxOccurences - step)) {
			inline.addStyleName("tag10");
		} else if (nboc >= (maxOccurences - (step * 2))) {
			inline.addStyleName("tag9");
		} else if (nboc >= (maxOccurences - (step * 3))) {
			inline.addStyleName("tag8");
		} else if (nboc >= (maxOccurences - (step * 4))) {
			inline.addStyleName("tag7");
		} else if (nboc >= (maxOccurences - (step * 5))) {
			inline.addStyleName("tag6");
		} else if (nboc >= (maxOccurences - (step * 6))) {
			inline.addStyleName("tag5");
		} else if (nboc >= (maxOccurences - (step * 7))) {
			inline.addStyleName("tag4");
		} else if (nboc >= (maxOccurences - (step * 8))) {
			inline.addStyleName("tag3");
		} else if (nboc >= (maxOccurences - (step * 9))) {
			inline.addStyleName("tag2");
		} else if (nboc >= (maxOccurences - (step * 10))) {
			inline.addStyleName("tag1");
		}

		inline.getElement().getStyle().setOpacity(1.0);

		inline.addStyleName(w.getColor());

		return inline;
	}

	public int getMaxNumberOfWords() {
		return maxNumberOfTags;
	}

	public void setMaxNumberOfWords(int numberOfWords) {
		this.maxNumberOfTags = numberOfWords;
	}

	public boolean isColored() {
		return isColored;
	}

	public void removeAllTags() {
		for (Entry<WordTag, InlineHTML> wordTag : tags.entrySet()) {
			cloud.remove(wordTag.getValue());
		}
		tags.clear();
	}

	public void addWord(WordTag wordTag) {
		tags.put(wordTag, null);
	}
}
