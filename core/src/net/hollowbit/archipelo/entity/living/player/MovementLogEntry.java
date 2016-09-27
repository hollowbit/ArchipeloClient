package net.hollowbit.archipelo.entity.living.player;

import net.hollowbit.archipeloshared.Direction;

public class MovementLogEntry {

	public long timeStamp;
	public Direction direction;
	public float speed;
	
	public MovementLogEntry (Direction direction, float speed) {
		this.timeStamp = System.currentTimeMillis();
		this.direction = direction;
		this.speed = speed;
	}
	
}
