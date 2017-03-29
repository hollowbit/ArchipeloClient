package net.hollowbit.archipelo.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.hollowbit.archipelo.entity.living.Player;
import net.hollowbit.archipeloshared.EntitySnapshot;

public abstract class EntityComponent {
	
	protected Entity entity;
	
	public EntityComponent (Entity entity) {
		this.entity = entity;
	}
	
	/**
	 * Update this entity component.
	 * @param deltaTime
	 */
	public void update (float deltaTime) {}
	
	/**
	 * Render anything necessary for this entity component. Returns true if all rendering after it should be overridden.
	 * @param batch
	 * @return
	 */
	public boolean render (SpriteBatch batch, boolean previouslyCancelled) {
		return false;
	}
	
	/**
	 * Renders after the main render method and the entity default animation render calls
	 * @param batch
	 * @param previouslyCancelled
	 * @return
	 */
	public boolean renderAfter (SpriteBatch batch, boolean previouslyCancelled) {
		return false;
	}
	
	/**
	 * Allows to quickly render an entity using a different animation than the current.
	 * @param animation
	 * @param stateTime
	 */
	protected void render (SpriteBatch batch, String animation, float stateTime) {
		batch.draw(entity.getEntityType().getAnimationFrame(animation, entity.getLocation().getDirection(), stateTime, entity.getStyle()), entity.getLocation().getX(), entity.getLocation().getY());;
	}
	
	public void interpolate (long timeStamp, EntitySnapshot snapshot1, EntitySnapshot snapshot2, float fraction) {}
	
	public void applyChangesSnapshot (EntitySnapshot snapshot) {}
	
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
	
	/**
	 * Called when the entity is loaded
	 */
	public void load () {}
	
	/**
	 * Called when the entity is unloaded
	 */
	public void unload () {}
	
}
