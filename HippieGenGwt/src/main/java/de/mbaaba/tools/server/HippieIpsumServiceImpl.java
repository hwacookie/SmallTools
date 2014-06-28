package de.mbaaba.tools.server;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.objectify.ObjectifyService;

import de.mbaaba.tools.client.HippieIpsumService;
import de.mbaaba.tools.shared.DefaultWordLists;
import de.mbaaba.tools.shared.Style;
import de.mbaaba.tools.shared.WordList;
import de.mbaaba.tools.shared.WordTypes;
/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class HippieIpsumServiceImpl extends RemoteServiceServlet implements HippieIpsumService {
	
	Logger logger = Logger.getLogger(HippieIpsumServiceImpl.class);
	
	@Override
	public Style getStyle(String aStyleName) throws IllegalArgumentException {
		String description = getStyleDescription(aStyleName);
		Map<WordTypes, WordList> map = new HashMap<WordTypes, WordList>();

		for (WordTypes wordTypes : WordTypes.values()) {
			String loadedList = loadList(aStyleName, wordTypes);
			WordList wordList = new WordList(wordTypes);
			wordList.parse(loadedList, WordList.NEWLINE);
			map.put(wordTypes, wordList);
		}
		Style style = new Style(aStyleName, description, map);
		return style;
	}
	

	@Override
	public void newStyle(Style aStyle) throws IllegalArgumentException {
		logger.debug("Saving new  style");
		System.out.println("Huch1");
		ObjectifyService.register(Style.class);
		ofy().save().entity(aStyle).now();
	}	

	@Override
	public String[] getStyleNames() throws IllegalArgumentException {
		return new String[] { "Hippie (deutsch)", "Garten (deutsch)", "Startrek (english)" };
	}

	@Override
	public void saveStyle(Style aStyle) throws IllegalArgumentException {
		org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(getClass());
		logger.debug("saving Style "+aStyle.getName()+".");
		System.out.println("Huch2");
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity entity = new Entity("StyleDescriptions", aStyle.getName());
		entity.setProperty("content", aStyle.getDescription());
		putToDatastore(datastore, entity);

		Collection<WordList> values = aStyle.getWordsMap().values();
		for (WordList wordList : values) {
			entity = new Entity("WordList", aStyle.getName() + "." + wordList.getWordType().name());
			entity.setProperty("content", wordList.buildString());
			putToDatastore(datastore, entity);
		}
	}
	
	
	// ------------------------------------------------------------------------------

	private String loadList(String aTheme, WordTypes aWordType) throws IllegalArgumentException {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		String name = aWordType.name();
		Key guestbookKey = KeyFactory.createKey("WordList", aTheme + "." + name);
		Entity datastoreEntry;
		String wordsAsString = null;
		try {
			datastoreEntry = getFromDatastore(datastore, guestbookKey);
			wordsAsString = (String) datastoreEntry.getProperty("content");
		} catch (EntityNotFoundException e) {
			System.out.println("No wordlist \"" + name + "\" found for theme \"" + aTheme + "\"");
		}
		if (wordsAsString == null) {
			Style defaultStyle = DefaultWordLists.defaultStyle;
			Map<WordTypes, WordList> wordsMap = defaultStyle.getWordsMap();
			WordList wordList = wordsMap.get(aWordType);
			if (wordList!=null) {
				wordsAsString = wordList.buildString();
			} else {
				wordsAsString = "Fehler!";
			}
		}
		return wordsAsString;

	}

	private Entity getFromDatastore(DatastoreService datastore, Key key) throws EntityNotFoundException {
		fakeASlowDB();
		return datastore.get(key);
	}

	private void putToDatastore(DatastoreService datastore, Entity entity) {
		fakeASlowDB();
		datastore.put(entity);
	}

	private void fakeASlowDB() {
//		try {
//			Thread.sleep(random.nextInt(5000) + 250);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	private String getStyleDescription(String aStyleName) throws IllegalArgumentException {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Key key = KeyFactory.createKey("StyleDescriptions", aStyleName);
		try {
			Entity datastoreEntry = getFromDatastore(datastore, key);
			String description = (String) datastoreEntry.getProperty("content");
			return description;
		} catch (EntityNotFoundException e) {
			return aStyleName;
		}
	}



	
	

}
