package net.hollowbit.archipeloshared;

import java.util.HashMap;
import java.util.LinkedList;

public class MapSnapshot {
	
	public String name;
	public String displayName;
	public LinkedList<SoundPlayData> sounds = new LinkedList<SoundPlayData>();
	public LinkedList<ParticlesData> particles = new LinkedList<ParticlesData>();
	public HashMap<String, String> properties;
	
	public MapSnapshot(){}
	
	public MapSnapshot (String name, String displayName) {
		this.name = name;
		this.displayName = displayName;
		properties = new HashMap<String, String>();
		sounds = new LinkedList<SoundPlayData>();
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
	
	public float getFloat(String key, float defaultValue) {
		if (!properties.containsKey(key))
			return defaultValue;
		
		try {
			return Float.parseFloat(properties.get(key));
		} catch(Exception e) {
			return defaultValue;
		}
	}
	
	public String getString(String key, String defaultValue) {
		if (!properties.containsKey(key))
			return defaultValue;
		else
			return properties.get(key);
	}
	
	public int getInt(String key, int defaultValue) {
		if (!properties.containsKey(key))
			return defaultValue;
		
		try {
			return Integer.parseInt(properties.get(key));
		} catch(Exception e) {
			return defaultValue;
		}
	}
	
	public boolean getBoolean(String key, boolean defaultValue) {
		if (!properties.containsKey(key))
			return defaultValue;
		
		try {
			return Boolean.parseBoolean(properties.get(key));
		} catch(Exception e) {
			return defaultValue;
		}
	}
	
	public void clear () {
		properties.clear();
		particles.clear();
		sounds.clear();
	}
	
	public void addSound(String sound, int tileX, int tileY) {
		sounds.add(new SoundPlayData(sound, tileX, tileY));
	}
	
}
