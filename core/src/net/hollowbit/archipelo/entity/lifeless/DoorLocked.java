package net.hollowbit.archipelo.entity.lifeless;

import net.hollowbit.archipelo.entity.EntityType;
import net.hollowbit.archipelo.entity.components.FlagCollisionEntityComponent;
import net.hollowbit.archipelo.entity.components.FlagRenderEntityComponent;
import net.hollowbit.archipelo.world.Map;
import net.hollowbit.archipeloshared.EntitySnapshot;

public class DoorLocked extends Door {
	
	@Override
	public void create(EntitySnapshot fullSnapshot, Map map, EntityType entityType) {
		super.create(fullSnapshot, map, entityType);
		String unlockFlag = fullSnapshot.getString("unlockFlag", map.getIslandName() + "-" + map.getName() + "-" + this.name + "Unlock");
		components.add(new FlagRenderEntityComponent(this, unlockFlag, "locked", false));
		components.add(new FlagCollisionEntityComponent(this, unlockFlag, "bottom", true));
	}
	
}
