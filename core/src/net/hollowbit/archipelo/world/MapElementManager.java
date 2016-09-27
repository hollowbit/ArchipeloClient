package net.hollowbit.archipelo.world;

import java.util.HashMap;

public class MapElementManager {
	
	private HashMap<String, Tile> tileMap;
	private HashMap<String, MapElement> elementMap;
	
	public MapElementManager () {
		tileMap = new HashMap<String, Tile>();
		elementMap = new HashMap<String, MapElement>();
	}
	
	public void loadMapElements () {
		MapElementLoader loader = new MapElementLoader();
		tileMap = loader.loadTiles();
		elementMap = loader.loadElements();
	}
	
	public Tile getTile (String id) {
		return tileMap.get(id);
	}
	
	public MapElement getElement (String id) {
		return elementMap.get(id);
	}
	
}
