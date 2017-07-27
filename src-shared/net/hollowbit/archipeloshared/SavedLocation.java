package net.hollowbit.archipeloshared;

public class SavedLocation {
	
	public float x = 0;
	public float y = 0;
	public String map = "";
	public int direction = -1;
	
	public SavedLocation() {}

	public SavedLocation(float x, float y, String map, int direction) {
		this.x = x;
		this.y = y;
		this.map = map;
		this.direction = direction;
	}
	
	public SavedLocation(float x, float y, String map) {
		super();
		this.x = x;
		this.y = y;
		this.map = map;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public String getMap() {
		return map;
	}

	public void setMap(String map) {
		this.map = map;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}
	
}
