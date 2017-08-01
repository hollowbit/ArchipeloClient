package net.hollowbit.archipeloshared;

import java.util.HashMap;

public class ChunkData {
	
	public static final int SIZE = 128;
	
	public int x, y;
	public HashMap<String, EntitySnapshot> entities;
	public String collisionData;
	public String overrideCollisionData;
	public String[][] tiles;
	public String[][] elements;
	
	public ChunkData() {
		entities = new HashMap<String, EntitySnapshot>();
	}

	public ChunkData(int x, int y) {
		super();
		this.x = x;
		this.y = y;
		entities = new HashMap<String, EntitySnapshot>();
	}
	
}
