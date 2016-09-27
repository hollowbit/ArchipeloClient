package net.hollowbit.archipelo.entity;

import java.util.HashMap;

public class EntitySnapshot {
	
	public String name;
	public String entityType;
	public int style = 0;
	public HashMap<String, String> properties;
	
	public EntitySnapshot () {}
	
	public EntitySnapshot (String name, String entityType, int style, HashMap<String, String> properties) {
		this.name = name;
		this.entityType = entityType;
		this.style = style;
		this.properties = properties;
	}
	
	public EntitySnapshot (String name, EntityType entityType, int style, HashMap<String, String> properties) {
		this(name, entityType.getId(), style, properties);
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
	
	public boolean doesPropertyExist (String key) {
		return properties.containsKey(key);
	}
	
}
