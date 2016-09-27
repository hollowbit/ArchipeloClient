package net.hollowbit.archipelo.entity;

import java.util.Comparator;

public class EntityHeightComparator implements Comparator<Entity> {

	@Override
	public int compare(Entity entity1, Entity entity2) {
		return (entity1.location.getY() < entity2.location.getY() ? 1 : -1);
	}

}
