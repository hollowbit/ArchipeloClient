package net.hollowbit.archipeloshared;

public class ChunkData {
	
	public static final int SIZE = 128;
	
	public int x, y;
	public String collisionData;
	public String[][] tiles;
	public String[][] elements;
	
	public ChunkData() {}

	public ChunkData(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}
	
}
