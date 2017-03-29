package net.hollowbit.archipeloshared;

public class SavedLocation {
	
	public float x = 0;
	public float y = 0;
	public String map = "";
	public String island = "";
	public int direction = -1;
	
	public SavedLocation() {}

	public SavedLocation(float x, float y, String map, String island, int direction) {
		this.x = x;
		this.y = y;
		this.map = map;
		this.island = island;
		this.direction = direction;
	}
	
}
