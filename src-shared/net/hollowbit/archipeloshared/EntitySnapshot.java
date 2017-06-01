package net.hollowbit.archipeloshared;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.utils.Json;

public class EntitySnapshot {
	
	private static Json json = new Json();
	
	public String name;
	public String type;
	public String footSound = "";
	public float footPitch = 1;
	public ArrayList<String> sounds = new ArrayList<String>();
	public ArrayList<String> usounds = new ArrayList<String>();
	public HashMap<String, String> properties = new HashMap<String, String>();
	
	public EntitySnapshot () {}
	
	public EntitySnapshot (String name, String entityTypeId, boolean ignoreType) {
		this.name = name;
		if (ignoreType)
			this.type = null;
		else
			this.type = entityTypeId;
		properties = new HashMap<String, String>();
	}
	
	/**
	 * Clone constructor
	 * @param snapshot
	 */
	public EntitySnapshot (EntitySnapshot snapshot) {
		this.name = snapshot.name;
		this.type = snapshot.type;
		this.footSound = snapshot.footSound;
		this.footPitch = snapshot.footPitch;
		
		for (String sound : snapshot.sounds)
			this.sounds.add(sound);
		
		for (String usound : snapshot.usounds)
			this.usounds.add(usound);
		
		for (HashMap.Entry<String, String> property : snapshot.properties.entrySet())
			this.properties.put(property.getKey(), property.getValue());
	}
	
	public void putFloat (String key, float value) {
		properties.put(key, "" + value);
	}
	
	public void putString (String key, String value) {
		properties.put(key, value);
	}
	
	public void putInt (String key, int value) {
		properties.put(key, "" + value);
	}
	
	public void putBoolean (String key, boolean value) {
		properties.put(key, "" + value);
	}
	
	public void putObject(String key, Object value) {
		properties.put(key, json.toJson(value));
	}
	
	public float getFloat (String key, float currentValue) {
		if (!properties.containsKey(key))
			return currentValue;
		try {
			return Float.parseFloat(properties.get(key));
		} catch (Exception e) {
			return currentValue;
		}
	}
	
	public String getString (String key, String currentValue) {
		if (!properties.containsKey(key))
			return currentValue;
		
		return properties.get(key);
	}

	public int getInt (String key, int currentValue) {
		if (!properties.containsKey(key))
			return currentValue;
		try {
			return Integer.parseInt(properties.get(key));
		} catch (Exception e) {
			return currentValue;
		}
	}
	
	public boolean getBoolean (String key, boolean currentValue) {
		if (!properties.containsKey(key))
			return currentValue;
		try {
			return Boolean.parseBoolean(properties.get(key));
		} catch (Exception e) {
			return currentValue;
		}
	}
	
	public <T> T getObject(String key, T currentValue, Class<T> type) {
		if (!properties.containsKey(key)) 
			return currentValue;
		
		try {
			return json.fromJson(type, properties.get(key));
		} catch (Exception e) {
			return currentValue;
		}
	}
	
	public boolean doesPropertyExist(String property) {
		return properties.containsKey(property);
	}
	
	public void setSound(String sound) {
		this.footSound = sound;
	}
	
	public void addSound(String sound) {
		sounds.add(sound);
	}
	
	public void addUnsafeSound(String sound) {
		usounds.add(sound);
	}
	
	public void clear () {
		properties.clear();
		sounds.clear();
		usounds.clear();
	}
	
	public boolean isEmpty () {
		return properties.isEmpty() && sounds.isEmpty() && usounds.isEmpty();
	}
	
}
