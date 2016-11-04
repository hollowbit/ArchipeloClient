package net.hollowbit.archipelo.tools;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.screen.screens.ErrorScreen;
import net.hollowbit.archipelo.tools.npcdialogs.NpcDialog;
import net.hollowbit.archipelo.tools.npcdialogs.NpcDialogs;
import net.hollowbit.archipelo.tools.npcdialogs.NpcDialogsList;
import net.hollowbit.archipeloshared.MessageDatas;

public class LanguageSpecificMessageManager {
	
	//Language, Category, ID
	private HashMap<Cat, HashMap<String, String>> messages;
	private HashMap<String, NpcDialog> npcDialogs;
	
	@SuppressWarnings("unchecked")
	public void reloadWithNewLanguage() {
		messages = new HashMap<Cat, HashMap<String, String>>();
		Json json = new Json();
		
		//Load all messages and put in maps
		for (Cat category : Cat.values()) {
			HashMap<String, String> catMessages = new HashMap<String, String>();
			try {
				HashMap<String, String> tempMessages = ((MessageDatas) json.fromJson(ClassReflection.forName("net.hollowbit.archipeloshared.MessageDatas"), Gdx.files.internal("languages/" + ArchipeloClient.getGame().getPrefs().getChosenLanguage().getId() + "/" + category.getId() + ".json"))).messages;
				
				//Loop though map and add them to messages with upper case keys
				Iterator<Map.Entry<String, String>> it = tempMessages.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<String, String> pair = (Map.Entry<String, String>) it.next();
					catMessages.put(pair.getKey().toUpperCase(), pair.getValue());
					it.remove();
				}
			} catch (ReflectionException e) {
				ArchipeloClient.getGame().getScreenManager().setScreen(new ErrorScreen("Unable to load NPC Dialogs", e));
			}
			messages.put(category, catMessages);//Add messages to category map
		}
		
		//Load Npc dialogs
		npcDialogs = new HashMap<String, NpcDialog>();
		try {
			for (String dialogName : ((NpcDialogsList) json.fromJson(ClassReflection.forName("net.hollowbit.archipelo.tools.npcdialogs.NpcDialogsList"), Gdx.files.internal("npc_dialogs_list.json"))).npcDialogs) {
				for (NpcDialog dialog : ((NpcDialogs) json.fromJson(ClassReflection.forName("net.hollowbit.archipelo.tools.npcdialogs.NpcDialogs"), Gdx.files.internal("languages/" + ArchipeloClient.getGame().getPrefs().getChosenLanguage().getId() + "/npc_dialogs/" + dialogName + ".json"))).dialogs)
					npcDialogs.put(dialog.id.toUpperCase(), dialog);
			}
		} catch (ReflectionException e) {
			ArchipeloClient.getGame().getScreenManager().setScreen(new ErrorScreen("Unable to load NPC Dialogs", e));
		}
	}
	
	/**
	 * Get a message from a specified category using the chosen language. Returns error string if key not found.
	 * @param category
	 * @param id Case insensitive
	 * @return
	 */
	public String getMessage (Cat category, String id) {
		if (messages.get(category).containsKey(id.toUpperCase()))
			return messages.get(category).get(id.toUpperCase());//Uses toUpperCase to make it case insensitivity
		else
			return "KEY NOT FOUND!";
	}
	
	/**
	 * Gets an NPC dialog by id
	 * @param id
	 * @return
	 */
	public NpcDialog getNpcDialogById (String id) {
		if (npcDialogs.containsKey(id.toUpperCase()))
			return npcDialogs.get(id.toUpperCase());
		else
			return new NpcDialog();
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
