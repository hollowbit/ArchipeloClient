package net.hollowbit.archipelo.entity.components;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.entity.Entity;
import net.hollowbit.archipelo.entity.EntityComponent;

/**
 * Used to override rendering of other animations if a player flag is set. Used most commonly for locked doors.
 * @author vedi0boy
 *
 */
public class FlagRenderEntityComponent extends EntityComponent {
	
	private String flag;
	private String animation;
	private boolean requiredValue;
	
	public FlagRenderEntityComponent (Entity entity, String flag, String animation) {
		this(entity, flag, animation, true);
	}
	
	/**
	 * requiredValue can be set to false to render only if the player doesn't have this flag
	 * @param entity
	 * @param flag
	 * @param animation
	 * @param requiredValue
	 */
	public FlagRenderEntityComponent (Entity entity, String flag, String animation, boolean requiredValue) {
		super(entity);
		this.flag = flag;
		this.animation = animation;
		this.requiredValue = requiredValue;
	}
	
	@Override
	public boolean render (SpriteBatch batch, boolean previouslyCanceled) {
		if (previouslyCanceled)
			return false;
		
		boolean condition = ArchipeloClient.getGame().getWorld().getFlagsManager().hasFlag(flag) == requiredValue;
		if (condition)
			render(batch, animation, ArchipeloClient.STATE_TIME);
		
		return condition;
	}

}
