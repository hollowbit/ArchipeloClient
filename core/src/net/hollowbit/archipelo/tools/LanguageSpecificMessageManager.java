package net.hollowbit.archipelo.tools;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipeloshared.MessageDatas;

public class LanguageSpecificMessageManager {
	
	//Language, Category, ID
	private HashMap<Language, HashMap<Category, HashMap<String, String>>> messages;
	
	@SuppressWarnings("unchecked")
	public LanguageSpecificMessageManager () {
		messages = new HashMap<Language, HashMap<Category, HashMap<String, String>>>();
		
		Json json = new Json();
		
		//Load all messages and put in maps
		for (Language language : Language.values()) {
			HashMap<Category, HashMap<String, String>> categories = new HashMap<Category, HashMap<String, String>>();
			for (Category category : Category.values()) {
				HashMap<String, String> messages = null;
				try {
					messages = ((MessageDatas) json.fromJson(ClassReflection.forName("net.hollowbit.archipeloshared.MessageDatas"), Gdx.files.internal("messages/" + language.getId() + "/" + category.getId() + ".json"))).messages;
				} catch (ReflectionException e) {}
				categories.put(category, messages);
			}
			messages.put(language, categories);
		}
	}
	
	public String getMessage (Category category, String id) {
		return messages.get(ArchipeloClient.getGame().getPrefs().getChosenLanguage()).get(category).get(id);
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
	
	public enum Category {
		UI("ui");
		
		private String id;
		
		private Category (String id) {
			this.id = id;
		}
		
		public String getId () {
			return id;
		}
		
	}
	
}
