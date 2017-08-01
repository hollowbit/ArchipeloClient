package net.hollowbit.archipelo.world;

import net.hollowbit.archipeloshared.ChunkData;
import net.hollowbit.archipeloshared.MapSnapshot;

public class WorldSnapshot {
	
	public static final int TYPE_INTERP = 0;
	public static final int TYPE_CHANGES = 1;
	public static final int TYPE_FULL = 2;
	
	public double timeCreatedMillis;
	public boolean newMap;
	public int time;
	int type;
	public ChunkData[] chunks;
	public MapSnapshot mapSnapshot;
	
	public WorldSnapshot (double timeCreatedMillis, boolean newMap, int time, int type, ChunkData[] chunks, MapSnapshot mapSnapshot) {
		this.timeCreatedMillis = timeCreatedMillis;
		this.newMap = newMap;
		this.time = time;
		this.type = type;
		this.chunks = chunks;
		this.mapSnapshot = mapSnapshot;
	}
	
}
