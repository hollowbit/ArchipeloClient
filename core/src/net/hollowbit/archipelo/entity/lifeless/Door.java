package net.hollowbit.archipelo.entity.lifeless;

import net.hollowbit.archipelo.entity.LifelessEntity;
import net.hollowbit.archipeloshared.EntitySnapshot;

public class Door extends LifelessEntity {
	
	boolean open = false;
	
	@Override
	public void applyChangesSnapshot(EntitySnapshot snapshot) {
		open = snapshot.getBoolean("open", open);
		super.applyChangesSnapshot(snapshot);
	}
	
}
