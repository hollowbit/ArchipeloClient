package net.hollowbit.archipelo.entity.lifeless;

import net.hollowbit.archipelo.entity.EntityType;
import net.hollowbit.archipelo.entity.LifelessEntity;
import net.hollowbit.archipelo.world.Map;
import net.hollowbit.archipeloshared.EntitySnapshot;

public class Computer extends LifelessEntity {
	
	protected boolean on;
	
	@Override
	public void create(EntitySnapshot fullSnapshot, Map map, EntityType entityType) {
		super.create(fullSnapshot, map, entityType);
		this.on = fullSnapshot.getBoolean("on", false);
	}
	
	@Override
	public void applyChangesSnapshot(EntitySnapshot snapshot) {
		super.applyChangesSnapshot(snapshot);
		on = snapshot.getBoolean("on", on);
	}
	
}
