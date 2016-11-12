package net.hollowbit.archipelo.entity.lifeless;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.hollowbit.archipelo.entity.EntitySnapshot;
import net.hollowbit.archipelo.entity.EntityType;
import net.hollowbit.archipelo.entity.living.Player;
import net.hollowbit.archipelo.world.Map;

public class DoorLocked extends Door {
	
	String unlockFlag;
	
	@Override
	public void create(EntitySnapshot fullSnapshot, Map map, EntityType entityType) {
		super.create(fullSnapshot, map, entityType);
		this.unlockFlag = fullSnapshot.getString("unlockFlag", map.getIslandName() + "-" + map.getName() + "-" + this.name + "unlock");
		System.out.println(unlockFlag);
	}
	
	@Override
	public void render(SpriteBatch batch) {
		if (location.getWorld().getFlagsManager().hasFlag(unlockFlag))
			super.render(batch);
		else
			batch.draw(entityType.getAnimationFrame("locked", 0, style), location.getX(), location.getY());
		super.renderSuper(batch);
	}
	
	@Override
	public boolean ignoreHardnessOfCollisionRects(Player player, String rectName) {
		if (rectName.equalsIgnoreCase("bottom") && location.getWorld().getFlagsManager().hasFlag(unlockFlag))
			return true;
		else
			return super.ignoreHardnessOfCollisionRects(player, rectName);
	}
	
}
