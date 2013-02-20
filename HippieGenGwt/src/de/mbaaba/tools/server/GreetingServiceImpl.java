package de.mbaaba.tools.server;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import de.mbaaba.tools.client.GreetingService;
import de.mbaaba.tools.shared.DefaultWordLists;
import de.mbaaba.tools.shared.Style;
import de.mbaaba.tools.shared.WordList;
import de.mbaaba.tools.shared.WordTypes;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements
		GreetingService {

	@Override
	public Style getStyle(String aStyleName) throws IllegalArgumentException {
		String description = getStyleDescription(aStyleName);
		Map<WordTypes, WordList> map = new HashMap<WordTypes, WordList>();

		for (WordTypes wordTypes : WordTypes.values()) {
			String loadedList = loadList(aStyleName, wordTypes);
			WordList wordList = new WordList(wordTypes);
			wordList.parse(loadedList);
			map.put(wordTypes, wordList);
		}
		Style style = new Style(aStyleName, description, map);
		return style;
	}

	@Override
	public String[] getStyleNames() throws IllegalArgumentException {
		return new String[] { "Default", "Hippie", "Garden", "Startrek" };
	}

	@Override
	public void saveStyle(Style aStyle) throws IllegalArgumentException {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		Entity entity = new Entity("StyleDescriptions", aStyle.getName());
		entity.setProperty("content", aStyle.getDescription());
		putToDatastore(datastore, entity);

		Collection<WordList> values = aStyle.getWordsMap().values();
		for (WordList wordList : values) {
			entity = new Entity("WordList", aStyle.getName() + "."
					+ wordList.getWordType());
			entity.setProperty("content", wordList.buildString());
			putToDatastore(datastore, entity);
		}
	}

	// ------------------------------------------------------------------------------

	private java.util.Random random = new java.util.Random();

	private String loadList(String aTheme, WordTypes aWordType)
			throws IllegalArgumentException {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		Key guestbookKey = KeyFactory.createKey("WordList", aTheme + "."
				+ aWordType);
		Entity greeting;
		String wordsAsString = null;
		try {
			greeting = getFromDatastore(datastore, guestbookKey);
			wordsAsString = (String) greeting.getProperty("content");
		} catch (EntityNotFoundException e) {
			System.out.println("No wordlist \"" + aWordType
					+ "\" found for theme \"" + aTheme + "\"");
		}
		if (wordsAsString == null) {
			wordsAsString = DefaultWordLists.defaultStyle.getWordsMap()
					.get(aWordType).buildString();
		}
		return wordsAsString;

	}

	private Entity getFromDatastore(DatastoreService datastore, Key key)
			throws EntityNotFoundException {
		// fakeASlowDB();
		return datastore.get(key);
	}

	private void putToDatastore(DatastoreService datastore, Entity entity) {
		// fakeASlowDB();
		datastore.put(entity);
	}

	private void fakeASlowDB() {
		try {
			Thread.sleep(random.nextInt(5000) + 250);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String getStyleDescription(String aStyleName)
			throws IllegalArgumentException {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		Key key = KeyFactory.createKey("StyleDescriptions", aStyleName);
		// try {
		// Entity greeting = getFromDatastore(datastore, key);
		// String description = (String) greeting.getProperty("content");
		return "Some description";// description;
		// } catch (EntityNotFoundException e) {
		// throw new IllegalArgumentException("No description found for " +
		// aStyleName);
		// }
	}

}
