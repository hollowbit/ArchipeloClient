package net.hollowbit.archipelo.world;

import java.util.HashMap;

import net.hollowbit.archipeloshared.EntitySnapshot;
import net.hollowbit.archipeloshared.MapSnapshot;

public class WorldSnapshot {
	
	public static final int TYPE_INTERP = 0;
	public static final int TYPE_CHANGES = 1;
	public static final int TYPE_FULL = 2;
	
	public double timeCreatedMillis;
	public int time;
	int type;
	public HashMap<String, EntitySnapshot> entitySnapshots;
	public MapSnapshot mapSnapshot;
	
	public WorldSnapshot (double timeCreatedMillis, int time, int type, HashMap<String, EntitySnapshot> entitySnapshots, MapSnapshot mapSnapshot) {
		this.timeCreatedMillis = timeCreatedMillis;
		this.time = time;
		this.type = type;
		this.entitySnapshots = entitySnapshots;
		this.mapSnapshot = mapSnapshot;
	}
	
}
