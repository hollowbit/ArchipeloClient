package net.hollowbit.archipelo.entity;

import com.badlogic.gdx.math.Vector2;

import net.hollowbit.archipelo.world.Map;

public abstract class LivingEntity extends Entity {
	
	public static final float MOVEMENT_STATETIME_START = 0.9f / 8;
	public static final double DIAGONAL_FACTOR = Math.sqrt(2);
	
	protected Vector2 goal;
	
	@Override
	public void create(EntitySnapshot fullSnapshot, Map map, EntityType entityType) {
		super.create(fullSnapshot, map, entityType);
		goal = new Vector2(fullSnapshot.getFloat("x", 0), fullSnapshot.getFloat("y", 0));
	}
	
	public void update (float deltatime, float timeUntilNextInterp) {
		super.update(deltatime, timeUntilNextInterp);
		location.pos.lerp(goal, timeUntilNextInterp);
	}
	
	@Override
	public void applyInterpSnapshot(long timeStamp, EntitySnapshot snapshot1, EntitySnapshot snapshot2, float fraction) {
		super.applyInterpSnapshot(timeStamp, snapshot1, snapshot2, fraction);
		Vector2 packet1Pos = new Vector2(snapshot1.getFloat("x", location.getX()), snapshot1.getFloat("y", location.getY()));
		Vector2 packet2Pos = new Vector2(snapshot2.getFloat("x", location.getX()), snapshot2.getFloat("y", location.getY()));
		this.goal.set(packet1Pos.lerp(packet2Pos, fraction));
	}
	
	public boolean isMoving() {
		return !location.pos.epsilonEquals(goal, 1);
	}
	
	@Override
	public boolean isPlayer() {
		return false;
	}
	
	@Override
	public boolean isAlive() {
		return true;
	}
	
}
