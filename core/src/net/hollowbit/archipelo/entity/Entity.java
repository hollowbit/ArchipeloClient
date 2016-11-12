package net.hollowbit.archipelo.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import net.hollowbit.archipelo.tools.Location;
import net.hollowbit.archipelo.world.Map;
import net.hollowbit.archipelo.entity.living.Player;
import net.hollowbit.archipeloshared.CollisionRect;
import net.hollowbit.archipeloshared.Direction;

public abstract class Entity {
	
	protected String name;
	protected Location location;
	protected EntityType entityType;
	protected int style;
	
	public Entity () {}
	
	public void render (SpriteBatch batch) {
		//CollisionRect collRect = this.getCollisionRect();
		//batch.draw(ArchipeloClient.getGame().getAssetManager().getTexture("invalid"), collRect.x, collRect.y, collRect.width, collRect.height);
	}
	
	public void create (EntitySnapshot fullSnapshot, Map map, EntityType entityType) {
		this.name = fullSnapshot.name;
		this.entityType = entityType;
		this.style = fullSnapshot.style;
		this.location = new Location(map, new Vector2(fullSnapshot.getFloat("x", 0), fullSnapshot.getFloat("y", 0)), Direction.values()[fullSnapshot.getInt("direction", 0)]);
	}
	
	public abstract void update (float deltaTime);
	
	public boolean isPlayer () {
		return false;
	}
	
	public boolean isAlive () {
		return false;
	}
	
	public void load () {}
	
	public void unload () {}
	
	public void applyInterpSnapshot (double timeStamp, EntitySnapshot snapshot) {}
	
	public void applyChangesSnapshot (EntitySnapshot snapshot) {
		location.set(snapshot.getFloat("x", location.getX()), snapshot.getFloat("y", location.getY()), Direction.values()[snapshot.getInt("direction", location.direction.ordinal())]);
	}
	
	public String getName () {
		return name;
	}
	
	public Location getLocation () {
		return location;
	}
	
	public void setLocation (Location location) {
		this.location = location;
	}
	
	public EntityType getEntityType () {
		return entityType;
	}

	public CollisionRect[] getCollisionRects () {
		return entityType.getCollisionRects(location.getX(), location.getY());
	}
	
	public CollisionRect[] getCollisionRects (Vector2 potentialPosition) {
		return entityType.getCollisionRects(potentialPosition.x, potentialPosition.y);
	}
	
	public CollisionRect getViewRect () {
		return entityType.getViewRect(location.getX(), location.getY());
	}
	
	/**
	 * This is used by certain entities which don't always want a collision rect to be hard.
	 * Ex: Like a locked door that becomes unlocked for some players.
	 * @param player
	 * @param rectName
	 * @return
	 */
	public boolean ignoreHardnessOfCollisionRects (Player player, String rectName) {
		return false;
	}
	
}
