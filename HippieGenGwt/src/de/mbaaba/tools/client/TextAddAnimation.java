package de.mbaaba.tools.client;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.user.client.ui.TextArea;

public class TextAddAnimation extends Animation {

	private TextArea textElement;
	private String aFullText;

	public TextAddAnimation(TextArea aTextElement, String aFullText) {
		textElement = aTextElement;
		this.aFullText = aFullText;
	}

	@Override
	protected void onUpdate(double progress) {
		Double l = new Double(aFullText.length() * progress);
		textElement.setText(aFullText.substring(0, l.intValue()));
	}

	@Override
	protected void onComplete() {
		super.onComplete();
		textElement.setText(aFullText);
	}

}
