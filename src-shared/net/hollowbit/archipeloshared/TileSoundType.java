package net.hollowbit.archipeloshared;

import java.util.HashMap;

public enum TileSoundType {
	
	GRASS("grass"),
	GRAVEL("gravel"),
	STONE("stone"),
	WOOD("wood");
	
	private String id;

	private TileSoundType(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
	
	private static HashMap<String, TileSoundType> tileSoundTypes;
	
	static {
		tileSoundTypes = new HashMap<String, TileSoundType>();
		for (TileSoundType type : TileSoundType.values())
			tileSoundTypes.put(type.getId(), type);
	}
	
	public static TileSoundType getById (String id) {
		return tileSoundTypes.get(id);
	}
	
}
