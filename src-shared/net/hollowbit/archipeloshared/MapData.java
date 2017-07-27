package net.hollowbit.archipeloshared;

import java.util.ArrayList;

public class MapData {
	
	public String name = "";
	public String displayName = "";
	public boolean naturalLighting = false;
	public String music;
	public boolean canSave = false;
	public ArrayList<ChunkLocation> chunks = new ArrayList<ChunkLocation>();
	
	/*public MapData () {}*/
	
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
