package net.hollowbit.archipeloshared;

public class CollisionRect {
	
	public String name;
	private float x, y;
	public float offsetX, offsetY;
	public float width, height;
	public boolean hard = false;
	
	public CollisionRect (CollisionRectData data) {
		this.name = data.name;
		this.offsetX = data.offsetX;
		this.offsetY = data.offsetY;
		this.width = data.width;
		this.height = data.height;
		this.hard = data.hard;
	}
	
	public CollisionRect (float x, float y, float width, float height) {
		this(x, y, 0, 0, width, height, false);
	}
	
	public CollisionRect (float x, float y, float offsetX, float offsetY, float width, float height) {
		this(x, y, offsetX, offsetY, width, height, false);
	}
	
	public CollisionRect (float x, float y, float offsetX, float offsetY, float width, float height, boolean hard) {
		this.x = x;
		this.y = y;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.width = width;
		this.height = height;
		this.hard = hard;
	}
	
	/**
	 * Duplictes a collision rect
	 * @param rect
	 */
	public CollisionRect (CollisionRect rect) {
		this.name = rect.name;
		this.offsetX = rect.offsetX;
		this.offsetY = rect.offsetY;
		this.width = rect.width;
		this.height = rect.height;
		this.hard = rect.hard;
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
