package net.hollowbit.archipeloshared;

public class ElementData {
	
	public String id = "element";
	public int x = 0, y = 0;
	public String name = "Element";
	public boolean animated = false;
	public float animationTime = 0.2f;
	public int animationFrames = 4;
	public int width = 1;
	public int height = 1;
	public boolean[][] collisionTable = new boolean[height * TileData.COLLISION_MAP_SCALE][width * TileData.COLLISION_MAP_SCALE];
	public boolean flipX = false;
	public boolean flipY = false;
	public int rotation = 0;
	public int offsetX = 0;
	public int offsetY = 0;
	
}
