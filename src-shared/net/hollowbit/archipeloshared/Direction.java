package net.hollowbit.archipeloshared;

public enum Direction {
	
	UP, LEFT, DOWN, RIGHT, UP_LEFT, UP_RIGHT, DOWN_LEFT, DOWN_RIGHT;
	
	/**
	 * i.e. Is not diagonal.
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

}
