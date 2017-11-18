package net.hollowbit.archipeloshared;

import java.util.HashMap;

public enum Direction {
	
	UP, LEFT, DOWN, RIGHT, UP_LEFT, UP_RIGHT, DOWN_LEFT, DOWN_RIGHT;
	
	private static HashMap<String, Direction> directionMap;
	
	static {
		directionMap = new HashMap<String, Direction>();
		for (Direction d : Direction.values())
			directionMap.put(d.name().replaceAll("_", ""), d);
	}
	
	public static Direction getDirectionByName(String direction) {
		//Clean direction string to allow for many possibilities
		direction = direction.replaceAll("_", "");
		direction = direction.replaceAll("-", "");
		direction = direction.toUpperCase();
		
		return directionMap.get(direction);
	}
	
	/**
	 * a.k.a. Is not diagonal.
	 * @return
	 */
	public boolean isStraight() {
		return this == UP || this == DOWN || this == LEFT || this == RIGHT;
	}
	
	public boolean isDiagonal() {
		return this == UP_LEFT || this == UP_RIGHT || this == DOWN_LEFT || this == DOWN_RIGHT;
	}
	
	public Direction opposite() {
		return opposite(this);
	}
	
	public static final int TOTAL = 8;
	public static final Direction[] IN_A_ROW = {DOWN, DOWN_RIGHT, RIGHT, UP_RIGHT, UP, UP_LEFT, LEFT, DOWN_LEFT};
	public static final Direction[] UPWARDS_DIRECTIONS = {UP, UP_LEFT, UP_RIGHT};
	public static final Direction[] DOWNWARDS_DIRECTIONS = {DOWN, DOWN_RIGHT, RIGHT, LEFT, DOWN_LEFT};
	public static final Direction[] DIAGONALS = {UP_LEFT, UP_RIGHT, DOWN_LEFT, DOWN_RIGHT};
	public static final Direction[] STRAIGHTS = {UP, LEFT, DOWN, RIGHT};
	
	public static final Direction[] DIRECTIONS_FOR_ENTITY_MAX_1 = {DOWN};
	public static final Direction[] DIRECTIONS_FOR_ENTITY_MAX_4 = {UP, LEFT, DOWN, RIGHT};
	
	public static Direction opposite(Direction direction) {
		switch (direction) {
		case DOWN:
			return UP;
		case UP:
			return DOWN;
		case DOWN_LEFT:
			return UP_RIGHT;
		case DOWN_RIGHT:
			return UP_LEFT;
		case LEFT:
			return RIGHT;
		case RIGHT:
			return LEFT;
		case UP_LEFT:
			return DOWN_RIGHT;
		case UP_RIGHT:
			return DOWN_LEFT;
		}
		return direction;
	}
	
	/**
	 * This is for when entities are only allowed to go in either 1, 4 or 8 directions.
	 * 
	 * This method will return a list of the allowed directions whether it is 1, 4 or 8.
	 * Any invalid maxDirectionsAllowed will return the same value as with 4.
	 * @param maxDirectionsAllowed
	 * @return
	 */
	public static Direction[] getAllowedDirectionsEntityMax(int maxDirectionsAllowed) {
		if (maxDirectionsAllowed == 1)
			return DIRECTIONS_FOR_ENTITY_MAX_1;
		else if (maxDirectionsAllowed == 8)
			return Direction.values();
		else//Includes the value if only 4 directions are allowed
			return DIRECTIONS_FOR_ENTITY_MAX_4;
	}

}
