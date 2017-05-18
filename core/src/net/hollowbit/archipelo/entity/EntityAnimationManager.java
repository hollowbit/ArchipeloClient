package net.hollowbit.archipelo.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.hollowbit.archipelo.tools.StaticTools;
import net.hollowbit.archipeloshared.EntityAnimationData;
import net.hollowbit.archipeloshared.EntitySnapshot;

public class EntityAnimationManager {
	
	private Entity entity;
	private String id;
	private EntityAnimationData data;
	private float stateTime;
	private String meta;
	private float animationLength;
	
	public EntityAnimationManager (Entity entity, String animation, float stateTime, String animationMeta) {
		this.entity = entity;
		this.id = animation;
		this.stateTime = stateTime;
		this.meta = animationMeta;
		this.data = entity.getEntityType().getEntityAnimation(animation).getData();
		animationLength = data.totalRuntime;
	}
	
	/**
	 * Renders the animation frame at the entities location
	 * @param batch
	 */
	public void render (SpriteBatch batch) {
		batch.draw(entity.getEntityType().getAnimationFrame(id, entity.getLocation().getDirection(), stateTime, entity.getStyle()), entity.getLocation().getX(), entity.getLocation().getY());
	}
	
	/**
	 * Updates time of animation. Only call if using client-side prediction
	 * @param deltaTime
	 */
	public void update (float deltaTime) {
		stateTime += deltaTime;
		
		//If this animation doesn't loop and is over time limit, call change event on entities to get a new animation to replace it.
		if (data.finiteLength && stateTime > animationLength) {
			EntityAnimationObject newAnim = entity.animationCompleted(id);
					
			//Set new animation if not null
			if (newAnim != null)
				this.change(newAnim.animationId, newAnim.animationMeta);
			else//If null, use the default animation for this entity
				this.change(entity.getEntityType().getDefaultAnimationId());
		}
	}
	
	/**
	 * Updates goalStateTime of the animation and changes the animations if needed
	 * @param animation
	 * @param stateTime
	 */
	public void change (long timeStamp, EntitySnapshot snapshot1, EntitySnapshot snapshot2, float fraction) {
		if (!snapshot2.anim.equals(id) || !snapshot1.anim.equals(snapshot2.anim))
			this.stateTime = 0;
		
		this.id = snapshot2.anim;
		this.meta = snapshot2.animMeta;
		float snapshot1Time = (snapshot1.anim.equals(snapshot2.anim) ? snapshot1.animTime : 0);
		this.stateTime = StaticTools.singleDimensionLerp(snapshot1Time, snapshot2.animTime, fraction);
	}
	
	public void change (String animationId) {
		this.change(animationId, "");
	}
	
	public void change (String animationId, String animationMeta) {
		this.change(animationId, animationMeta, entity.getEntityType().getEntityAnimation(animationId).getTotalRuntime());
	}
	
	public void change (String animationId, String animationMeta, float customAnimationLength) {
		if (entity.getEntityType().hasAnimation(animationId)) {
			if (!animationId.equals(id))
				this.stateTime = 0;
			this.id = animationId;
			this.data = entity.getEntityType().getEntityAnimation(animationId).getData();
			this.meta = animationMeta;
			this.animationLength = customAnimationLength;
		}
	}
	
	public String getAnimationId() {
		return id;
	}

	public float getStateTime() {
		return stateTime;
	}
	
	/**
	 * Used to determine if the current animation is an animation of a player using something.
	 * @return
	 */
	public boolean isUseAnimation () {
		return id.equals("use") || id.equals("usewalk") || id.equals("thrust");
	}
	
	/**
	 * Animation meta is extra data required to render the animation correctly. Used by player for when items are used to say which item was used
	 * @return
	 */
	public String getAnimationMeta() {
		return meta;
	}
	
	public static class EntityAnimationObject {
		
		public String animationId;
		public String animationMeta;
		
		public EntityAnimationObject(String animationId) {
			this(animationId, "");
		}
		
		public EntityAnimationObject(String animationId, String animationMeta) {
			this.animationId = animationId;
			this.animationMeta = animationMeta;
		}
		
	}
	
}
