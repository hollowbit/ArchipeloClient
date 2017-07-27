package net.hollowbit.archipeloshared;

import java.util.ArrayList;

public class ChunkData {
	
	public static final int SIZE = 128;
	
	public int x, y;
	public String[][] tiles;
	public String[][] elements;
	public ArrayList<EntitySnapshot> entities;
	
	public ChunkData() {
		entities = new ArrayList<EntitySnapshot>();
	}

	public ChunkData(int x, int y) {
		super();
		this.x = x;
		this.y = y;
		entities = new ArrayList<EntitySnapshot>();
	}
	
}
