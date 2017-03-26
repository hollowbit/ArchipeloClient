package net.hollowbit.archipelo.entity;

import java.util.ArrayList;
import java.util.HashMap;

public class EntitySnapshot {
	
	public String name;
	public String type;
	public String anim = "";
	public float animTime = 0;
	public String animMeta = "";
	public String footSound = "";
	public ArrayList<String> sounds = new ArrayList<String>();
	public HashMap<String, String> properties = new HashMap<String, String>();
	
	public EntitySnapshot () {}
	
	public EntitySnapshot (String name, String entityType, HashMap<String, String> properties, String animation, String animationMeta, float animationTime) {
		this.name = name;
		this.type = entityType;
		this.properties = properties;
		this.anim = animation;
		this.animMeta = animationMeta;
		this.animTime = animationTime;
	}
	
	public EntitySnapshot (String name, EntityType entityType, HashMap<String, String> properties, String animation, String animationMeta, float animationTime) {
		this(name, entityType.getId(), properties, animation, animationMeta, animationTime);
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
