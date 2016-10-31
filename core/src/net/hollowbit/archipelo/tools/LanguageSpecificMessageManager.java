package net.hollowbit.archipelo.tools;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipeloshared.MessageDatas;

public class LanguageSpecificMessageManager {
	
	//Language, Category, ID
	private HashMap<Language, HashMap<Cat, HashMap<String, String>>> messages;
	
	@SuppressWarnings("unchecked")
	public LanguageSpecificMessageManager () {
		messages = new HashMap<Language, HashMap<Cat, HashMap<String, String>>>();
		
		Json json = new Json();
		
		//Load all messages and put in maps
		for (Language language : Language.values()) {
			HashMap<Cat, HashMap<String, String>> categories = new HashMap<Cat, HashMap<String, String>>();
			for (Cat category : Cat.values()) {
				HashMap<String, String> messages = new HashMap<String, String>();
				try {
					HashMap<String, String> tempMessages = ((MessageDatas) json.fromJson(ClassReflection.forName("net.hollowbit.archipeloshared.MessageDatas"), Gdx.files.internal("messages/" + language.getId() + "/" + category.getId() + ".json"))).messages;
					
					//Loop though map and add them to messages with upper case keys
					Iterator<Map.Entry<String, String>> it = tempMessages.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry<String, String> pair = (Map.Entry<String, String>) it.next();
						messages.put(pair.getKey().toUpperCase(), pair.getValue());
						it.remove();
					}
				} catch (ReflectionException e) {}
				categories.put(category, messages);//Add messages to category map
			}
			messages.put(language, categories);//Add categories to corresponding language map
		}
	}
	
	/**
	 * Get a message from a specified category using the chosen language. Returns error string if key not found.
	 * @param category
	 * @param id Case insensitive
	 * @return
	 */
	public String getMessage (Cat category, String id) {
		if (messages.get(ArchipeloClient.getGame().getPrefs().getChosenLanguage()).get(category).containsKey(id.toUpperCase()))
			return messages.get(ArchipeloClient.getGame().getPrefs().getChosenLanguage()).get(category).get(id.toUpperCase());//Uses toUpperCase to make it case insensitivity
		else
			return "KEY NOT FOUND!";
	}
	
	public enum Language {
		ENGLISH("english", "English");
		
		private String id;
		private String name;
		
		private Language (String id, String name) {
			this.id = id;
			this.name = name;
		}
		
		public String getId () {
			return id;
		}
		
		public String getName () {
			return name;
		}
		
	}
	
	public enum Cat {
		UI("ui"),
		ERROR("error");
		
		private String id;
		
		private Cat (String id) {
			this.id = id;
		}
		
		public String getId () {
			return id;
		}
		
	}
	
}
