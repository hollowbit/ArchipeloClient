package net.hollowbit.archipelo.world;

import java.util.HashMap;

import net.hollowbit.archipeloshared.EntitySnapshot;
import net.hollowbit.archipeloshared.MapSnapshot;

public class WorldSnapshot {
	
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
