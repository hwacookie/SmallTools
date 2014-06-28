package de.mbaaba.tools.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.DataResource;

public interface MyResources extends ClientBundle {
	public static final MyResources INSTANCE = GWT.create(MyResources.class);

	@Source("typewritersong.mp3")
	public DataResource typewritersong();

}