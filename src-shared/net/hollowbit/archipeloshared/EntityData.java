package net.hollowbit.archipeloshared;

import java.util.ArrayList;

public class EntityData {
	
	public ArrayList<EntitySnapshot> entities = new ArrayList<EntitySnapshot>();
	
	public void clear () {
		for (EntitySnapshot snapshot: entities) {
			snapshot.clear();
		}
	}
	
}
