package net.hollowbit.archipelo.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipeloshared.CollisionRect;
import net.hollowbit.archipeloshared.TileData;

public abstract class MapElement {
	
	protected String id;
	protected String name;
	protected int width;
	protected int height;
	protected boolean[][] collisionTable;
	protected boolean flipX, flipY;
	protected int rotation;
	protected int offsetX, offsetY;
	
	public MapElement(String id, String name, int width, int height, boolean[][] collisionTable, boolean flipX, boolean flipY, int rotation, int offsetX, int offsetY) {
		this.id = id;
		this.name = name;
		this.width = width;
		this.height = height;
		this.collisionTable = collisionTable;
		this.flipX = flipX;
		this.flipY = flipY;
		this.rotation = rotation;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
	}
	
	public abstract void draw (SpriteBatch batch, float x, float y);

	public String getId () {
		return id;
	}

	public String getName () {
		return name;
	}
	
	public int getWidth () {
		return width;
	}
	
	public int getHeight () {
		return height;
	}

	public boolean[][] getCollisionTable() {
		return collisionTable;
	}
	
	public int getOffsetX () {
		return offsetX;
	}
	
	public int getOffsetY (){
		return offsetY;
	}
	
	protected float getDrawX (float x) {
		return x + (flipX ? width * ArchipeloClient.TILE_SIZE:0) + offsetX * (ArchipeloClient.TILE_SIZE / TileData.COLLISION_MAP_SCALE);
	}
	
	protected float getDrawY (float y) {
		return y + (flipY ? height * ArchipeloClient.TILE_SIZE:0) + offsetY * (ArchipeloClient.TILE_SIZE / TileData.COLLISION_MAP_SCALE);
	}
	
	protected float getDrawWidth () {
		return (flipX ? -1:1) * width * ArchipeloClient.TILE_SIZE;
	}
	
	protected float getDrawHeight () {
		return (flipY ? -1:1) * height * ArchipeloClient.TILE_SIZE;
	}
	
	protected float getOriginX () {
		return (flipX ? -1:1) * width * ArchipeloClient.TILE_SIZE / 2;
	}
	
	protected float getOriginY () {
		return (flipY ? -1:1) * height * ArchipeloClient.TILE_SIZE / 2;
	}
	
	public CollisionRect getViewRect (float x, float y) {
		return new CollisionRect(getDrawX(x), getDrawY(y), 0, 0, getDrawWidth(), getDrawHeight());
	}
	
}
