package net.hollowbit.archipeloshared;

import java.util.HashMap;

public class PropertyDefinition {
	
	public String name;
	public String type;
	public boolean required = false;
	
	public PropertyDefinition() {}
	
	public PropertyDefinition(String name, String type, boolean required) {
		this.name = name;
		this.type = type;
		this.required = required;
	}
	
	public PropertyType getType() {
		return PropertyDefinition.getType(this);
	}
	
	private static HashMap<String, PropertyType> typeMap;
	
	static {
		typeMap = new HashMap<String, PropertyType>();
		
		//Define all types and all possible aliases here
		typeMap.put("int", PropertyType.INTEGER);
		typeMap.put("integer", PropertyType.INTEGER);
		
		typeMap.put("float", PropertyType.FLOAT);
		typeMap.put("number", PropertyType.FLOAT);
		
		typeMap.put("double", PropertyType.DOUBLE);
		typeMap.put("decimal", PropertyType.DOUBLE);
		
		typeMap.put("point", PropertyType.POINT);
		typeMap.put("coord", PropertyType.POINT);
		typeMap.put("location", PropertyType.POINT);
		typeMap.put("coordinate", PropertyType.POINT);
		
		typeMap.put("loc", PropertyType.LOCATION);
		typeMap.put("location", PropertyType.LOCATION);
		
		typeMap.put("direction", PropertyType.DIRECTION);
		typeMap.put("dir", PropertyType.DIRECTION);
		
		typeMap.put("string", PropertyType.STRING);
		typeMap.put("text", PropertyType.STRING);
		
		typeMap.put("bool", PropertyType.BOOLEAN);
		typeMap.put("boolean", PropertyType.BOOLEAN);
		
		typeMap.put("object", PropertyType.JSON);
		typeMap.put("json", PropertyType.JSON);
		typeMap.put("obj", PropertyType.JSON);
	}
	
	public static PropertyType getType (String type) {
		return typeMap.get(type.toLowerCase());
	}
	
	public static PropertyType getType (PropertyDefinition definition) {
		return getType(definition.type);
	}
	
	private static enum PropertyType {
		INTEGER,
		FLOAT,
		DOUBLE,
		POINT,//Contains an x and y
		LOCATION,
		DIRECTION,
		STRING,
		BOOLEAN,
		JSON
	}
	
}
