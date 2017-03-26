package net.hollowbit.archipelo.entity;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.entity.EntityAnimationManager.EntityAnimationObject;
import net.hollowbit.archipelo.entity.living.Player;
import net.hollowbit.archipelo.tools.Location;
import net.hollowbit.archipelo.world.Map;
import net.hollowbit.archipeloshared.CollisionRect;
import net.hollowbit.archipeloshared.Direction;

public abstract class Entity {
	
	protected String name;
	protected Location location;
	protected EntityType entityType;
	protected int style;
	protected EntityAnimationManager animationManager;
	protected ArrayList<EntityComponent> components;
	protected boolean overrideControls = false;
	protected EntityAudioManager audioManager;
	
	public Entity () {
		components = new ArrayList<EntityComponent>();
	}
	
	public void create (EntitySnapshot fullSnapshot, Map map, EntityType entityType) {
		this.name = fullSnapshot.name;
		this.entityType = entityType;
		this.style = fullSnapshot.getInt("style", 0);
		this.location = new Location(map, new Vector2(fullSnapshot.getFloat("x", 0), fullSnapshot.getFloat("y", 0)), Direction.values()[fullSnapshot.getInt("direction", 0)]);
		animationManager = new EntityAnimationManager(this, fullSnapshot.anim, fullSnapshot.animTime, fullSnapshot.animMeta);
		audioManager = new EntityAudioManager(this, fullSnapshot.footSound);
	}
	
	/**
	 * Begin rendering entity. Will handle cancelled renders and animations.
	 * @param batch
	 */
	public void renderStart (SpriteBatch batch) {
		//CollisionRect collRect = this.getCollisionRect();
		//batch.draw(ArchipeloClient.getGame().getAssetManager().getTexture("invalid"), collRect.x, collRect.y, collRect.width, collRect.height);
		
		//Render components and check if they cancelled further rendering
		boolean renderCancelled = false;
		for (EntityComponent component : components
				) {
			if (component.render(batch, renderCancelled))
				renderCancelled = true;
		}
		
		if (!renderCancelled) {
			animationManager.render(batch);
			render(batch);
		}
		
		renderCancelled = false;
		for (EntityComponent component : components) {
			if (component.renderAfter(batch, renderCancelled))
				renderCancelled = true;
		}
	}
	
	protected void render (SpriteBatch batch) {}
	
	/**
	 * Tick the entity forward in time
	 * @param deltaTime
	 */
	public void update (float deltaTime) {
		for (EntityComponent component : components)
			component.update(deltaTime);
	}
	
	/**
	 * Interpolate the movement or other for this entity.
	 * @param timeStamp
	 * @param snapshotFrom
	 * @param snapshotTo
	 * @param fraction
	 */
	public void interpolate (long timeStamp, EntitySnapshot snapshotFrom, EntitySnapshot snapshotTo, float fraction) {
		animationManager.change(timeStamp, snapshotFrom, snapshotTo, fraction);
		audioManager.change(snapshotTo);
		
		for (EntityComponent component : components)
			component.interpolate(timeStamp, snapshotFrom, snapshotTo, fraction);
	}
	
	/**
	 * Definite and optimized way of knowing if an entity is a player.
	 * @return
	 */
	public boolean isPlayer () {
		return false;
	}
	
	/**
	 * Definite and optimized way of knowing if an entity is living/can move aka instance of {@link LivingEntity} class.
	 * @return
	 */
	public boolean isAlive () {
		return false;
	}
	
	/**
	 * Called when the entity is loaded
	 */
	public void load () {
		for (EntityComponent component : components)
			component.load();
	}
	
	/**
	 * Called when the entity is unloaded
	 */
	public void unload () {
		audioManager.dispose();
		for (EntityComponent component : components)
			component.unload();
	}
	
	/**
	 * Called whenever this entity has moved
	 */
	public void moved() {
		audioManager.moved();
	}
	
	public void teleport(float x, float y, Direction direction) {
		this.location.set(x, y, direction);
		this.moved();
	}
	
	public void applyChangesSnapshot (EntitySnapshot snapshot) {
		style = snapshot.getInt("style", style);
		if (!overrideControls)
			audioManager.handleChanges(snapshot);
		
		if (!overrideControls)
			location.direction = Direction.values()[snapshot.getInt("direction", location.direction.ordinal())];
		for (EntityComponent component : components)
			component.applyChangesSnapshot(snapshot);
	}
	
	public String getName () {
		return name;
	}
	
	public int getStyle () {
		return style;
	}
	
	public Direction getDirection () {
		return location.direction;
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
	
	public EntityAnimationManager getAnimationManager () {
		return animationManager;
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
	
	public float getDrawOrderY () {
		return entityType.getDrawOrderY(location.getY());
	}
	
	/**
	 * Called when the current animation is complete. Only used by entities where animations are overridden.
	 * @param animationId
	 * @return
	 */
	public EntityAnimationObject animationCompleted (String animationId) {
		return null;
	}
	
	/**
	 * This is used by certain entities which don't always want a collision rect to be hard.
	 * Ex: Like a locked door that becomes unlocked for some players.
	 * @param player
	 * @param rectName
	 * @return
	 */
	public boolean ignoreHardnessOfCollisionRects (Player player, String rectName) {
		boolean ignoreHardness = false;
		for (EntityComponent component : components) {
			if (component.ignoreHardnessOfCollisionRects(player, rectName))
				ignoreHardness = true;
		}
		return ignoreHardness;
	}
	
	/**
	 * Exact center point of the entities view rect.
	 * @return
	 */
	public Vector2 getCenterPoint () {
		CollisionRect viewRect = entityType.getViewRect(location.getX(), location.getY());
		return new Vector2(location.getX() + viewRect.width / 2, location.getY() + viewRect.height / 2);
	}
	
	public Vector2 getCenterPointTile () {
		Vector2 centerPoint = getCenterPoint();
		centerPoint.x /= ArchipeloClient.TILE_SIZE;
		centerPoint.y /= ArchipeloClient.TILE_SIZE;
		return centerPoint;
	}
	
}
