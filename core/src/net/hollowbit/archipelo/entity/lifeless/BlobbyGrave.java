package net.hollowbit.archipelo.entity.lifeless;

import net.hollowbit.archipelo.entity.EntityType;
import net.hollowbit.archipelo.entity.LifelessEntity;
import net.hollowbit.archipelo.entity.components.FlagRenderEntityComponent;
import net.hollowbit.archipelo.world.Map;
import net.hollowbit.archipeloshared.EntitySnapshot;

public class BlobbyGrave extends LifelessEntity {
	
	@Override
	public void create(EntitySnapshot fullSnapshot, Map map, EntityType entityType) {
		super.create(fullSnapshot, map, entityType);
		components.add(new FlagRenderEntityComponent(this, "blobbyGraveCrushed", "crushed"));
	}
	
}
