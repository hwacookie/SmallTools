package de.mbaaba.tools.client;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.media.client.Audio;
import com.google.gwt.user.client.ui.TextArea;

public class TextAddAnimation extends Animation {

	private static final int AUDIO_FADE_TIME = 2000;
	private TextArea textElement;
	private String aFullText;
	private Audio audio;
	private long fadeStartAt;
	private long fadeEndAt;

	public TextAddAnimation(TextArea aTextElement, String aFullText) {
		textElement = aTextElement;
		this.aFullText = aFullText;
	}

	@Override
	protected void onUpdate(double progress) {
		Double l = new Double(aFullText.length() * progress);
		textElement.setText(aFullText.substring(0, l.intValue()));

		if (audio != null) {
			long now = System.currentTimeMillis();
			if (now > fadeStartAt) {
				long timeLeft = fadeEndAt - now;
				if (timeLeft <= 0) {
					audio.setVolume(0.0);
				} else {
					double volume = timeLeft / AUDIO_FADE_TIME;
					audio.setVolume(volume);
				}
			}
		}

	}

	@Override
	protected void onComplete() {
		super.onComplete();
		if (audio != null) {
			audio.pause();
		}
		textElement.setText(aFullText);
	}

	@Override
	public void run(int duration) {
		super.run(duration);
		audio = Audio.createIfSupported();
		if (audio != null) {
			audio.setSrc("http://scottshuster.com/music/TheTypewriter-1950.mp3");
			audio.play();
			audio.setVolume(1.0);
			long now = System.currentTimeMillis();
			fadeStartAt = now + duration - AUDIO_FADE_TIME;
			fadeEndAt = now + duration;
		}
	}

}
