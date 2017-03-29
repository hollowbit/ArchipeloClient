package net.hollowbit.archipeloshared;

import java.util.ArrayList;

public class MapData {
	
	public String displayName = "";
	public int type = 0;
	public int climat = 0;
	public boolean naturalLighting = false;
	public String music;
	public String[][] tileData = new String[1][1];
	public String[][] elementData = new String[1][1];
	public ArrayList<EntitySnapshot> entitySnapshots = new ArrayList<EntitySnapshot>();
	
	public MapData () {}
	
	/*public MapData (Map map) {
		this.displayName = map.getDisplayName();
		this.tileData = map.getTileData();
		this.elementData = map.getElementData();
		this.climat = map.getClimat();
		this.type = map.getType();
		this.naturalLighting = map.hasNaturalLighting();
		this.music = map.getMusic();
		this.entitySnapshots = new ArrayList<EntitySnapshot>();
		for (Entity entity : map.getEntities()) {
			entitySnapshots.add(entity.getSaveSnapshot());
		}
	}*/
	
}
