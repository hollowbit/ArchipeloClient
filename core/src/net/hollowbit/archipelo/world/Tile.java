package net.hollowbit.archipelo.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.hollowbit.archipelo.ArchipeloClient;

public abstract class Tile {
	
	protected String id;
	protected String name;
	protected float speedMultiplier;
	protected boolean swimmable;
	protected boolean[][] collisionTable;
	protected boolean flipX, flipY;
	protected int rotation;
	protected String footstepSound;
	
	public Tile (String id, String name, float speedMultiplier, boolean swimmable, boolean[][] collisionTable, boolean flipX, boolean flipY, int rotation, String footstepSound) {
		this.id = id;
		this.name = name;
		this.speedMultiplier = speedMultiplier;
		this.swimmable = swimmable;
		this.collisionTable = collisionTable;
		this.flipX = flipX;
		this.flipY = flipY;
		this.rotation = rotation;
		this.footstepSound = footstepSound;
	}
	
	public abstract void draw (SpriteBatch batch, float x, float y);

	public String getId () {
		return id;
	}

	public String getName () {
		return name;
	}

	public float getSpeedMultiplier () {
		return speedMultiplier;
	}

	public boolean isSwimmable () {
		return swimmable;
	}

	public boolean[][] getCollisionTable () {
		return collisionTable;
	}
	
	protected float getDrawX (float x) {
		return x + (flipX ? ArchipeloClient.TILE_SIZE : 0);
	}
	
	protected float getDrawY (float y) {
		return y + (flipY ? ArchipeloClient.TILE_SIZE : 0);
	}
	
	protected float getOriginX () {
		return ArchipeloClient.TILE_SIZE / 2;
	}
	
	protected float getOriginY () {
		return ArchipeloClient.TILE_SIZE / 2;
	}
	
	protected float getDrawWidth () {
		return (flipX ? -1:1) * ArchipeloClient.TILE_SIZE;
	}
	
	protected float getDrawHeight () {
		return (flipY ? -1:1) * ArchipeloClient.TILE_SIZE;
	}

	public String getFootstepSound() {
		return footstepSound;
	}
	
}
