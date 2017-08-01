package net.hollowbit.archipelo.world.map;

import java.util.TreeMap;

public class ChunkRow {
	
	private int y;
	private TreeMap<Integer, Chunk> chunks;
	
	public ChunkRow(int y) {
		this.y = y;
		chunks = new TreeMap<Integer, Chunk>();
	}
	
	public int getY() {
		return y;
	}
	
	public TreeMap<Integer, Chunk> getChunks() {
		return chunks;
	}
	
}
