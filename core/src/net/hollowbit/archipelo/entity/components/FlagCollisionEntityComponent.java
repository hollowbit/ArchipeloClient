package net.hollowbit.archipelo.entity.components;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.entity.Entity;
import net.hollowbit.archipelo.entity.EntityComponent;
import net.hollowbit.archipelo.entity.living.Player;

public class FlagCollisionEntityComponent extends EntityComponent {
	
	private String flag;
	private String collisionRect;
	private boolean requiredValue;
	
	public FlagCollisionEntityComponent(Entity entity, String flag, String collisionRect) {
		this(entity, flag, collisionRect, true);
	}
	
	public FlagCollisionEntityComponent (Entity entity, String flag, String collisionRect, boolean requiredValue) {
		super(entity);
		this.flag = flag;
		this.collisionRect = collisionRect;
		this.requiredValue = requiredValue;
	}
	
	@Override
	public boolean ignoreHardnessOfCollisionRects(Player player, String rectName) {
		boolean hasFlag = ArchipeloClient.getGame().getWorld().getFlagsManager().hasFlag(flag) == requiredValue && rectName.equals(collisionRect);
		return hasFlag;
	}

}
