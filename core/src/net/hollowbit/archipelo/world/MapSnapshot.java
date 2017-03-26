package net.hollowbit.archipelo.world;

import java.util.HashMap;
import java.util.LinkedList;

import net.hollowbit.archipeloshared.SoundPlayData;

public class MapSnapshot {
	
	public String name;
	public String displayName;
	public String[][] tileData;
	public String[][] elementData;
	public LinkedList<SoundPlayData> sounds = new LinkedList<SoundPlayData>();
	public HashMap<String, String> properties;
	
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
	
}
