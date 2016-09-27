package net.hollowbit.archipeloshared;

public class CollisionRect {
	
	public String name;
	public float x, y;
	public float offsetX, offsetY;
	public float width, height;
	
	public CollisionRect (CollisionRectData data) {
		this.name = data.name;
		this.offsetX = data.offsetX;
		this.offsetY = data.offsetY;
		this.width = data.width;
		this.height = data.height;
	}
	
	public CollisionRect (float x, float y, float offsetX, float offsetY, float width, float height) {
		this.x = x;
		this.y = y;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.width = width;
		this.height = height;
	}
	
	public CollisionRect move (float x, float y) {
		this.x = x;
		this.y = y;
		return this;
	}
	
	//Calculates if rects are overlapping	
	public boolean collidesWith (CollisionRect rect) {
		return this.xWithOffset() < rect.xWithOffset() + rect.width && this.xWithOffset() + this.width > rect.xWithOffset() && this.yWithOffset() < rect.yWithOffset() + rect.height && this.yWithOffset() + this.height > rect.yWithOffset();
	}
	
	public float xWithOffset () {
		return x + offsetX;
	}
	
	public float yWithOffset () {
		return y + offsetY;
	}
	
}
