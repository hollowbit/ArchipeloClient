package net.hollowbit.archipelo.entity;

import com.badlogic.gdx.math.Vector2;

import net.hollowbit.archipelo.world.Map;
import net.hollowbit.archipeloshared.EntitySnapshot;

public abstract class LivingEntity extends Entity {
	
	public static final double DIAGONAL_FACTOR = Math.sqrt(2);
	
	protected boolean isMoving;
	protected float speed;
	
	@Override
	public void create(EntitySnapshot fullSnapshot, Map map, EntityType entityType) {
		super.create(fullSnapshot, map, entityType);
		isMoving = false;
		this.speed = fullSnapshot.getFloat("speed", entityType.getSpeed());
	}
	
	@Override
	public void interpolate(long timeStamp, EntitySnapshot snapshotFrom, EntitySnapshot snapshotTo, float fraction) {
		super.interpolate(timeStamp, snapshotFrom, snapshotTo, fraction);
		Vector2 oldPos = new Vector2(location.pos);
		
		Vector2 packet1Pos = new Vector2(snapshotFrom.getFloat("x", location.getX()), snapshotFrom.getFloat("y", location.getY()));
		Vector2 packet2Pos = new Vector2(snapshotTo.getFloat("x", location.getX()), snapshotTo.getFloat("y", location.getY()));
		
		location.pos.set(packet1Pos.lerp(packet2Pos, fraction));
		
		isMoving = !location.pos.epsilonEquals(oldPos, 1);
		if (isMoving)
			moved();
	}
	
	@Override
	public void applyChangesSnapshot(EntitySnapshot snapshot) {
		this.speed = snapshot.getFloat("speed", speed);
		super.applyChangesSnapshot(snapshot);
	}
	
	public boolean isMoving() {
		return isMoving;
	}
	
	@Override
	public boolean isPlayer() {
		return false;
	}
	
	@Override
	public boolean isAlive() {
		return true;
	}
	
	public float getSpeed() {
		return speed;
	}
	
}
