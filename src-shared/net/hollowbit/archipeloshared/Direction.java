package net.hollowbit.archipeloshared;

public enum Direction {
	
	UP, LEFT, DOWN, RIGHT, UP_LEFT, UP_RIGHT, DOWN_LEFT, DOWN_RIGHT;
	
	public static final int TOTAL = 8;
	public static final Direction[] IN_A_ROW = {DOWN, DOWN_RIGHT, RIGHT, UP_RIGHT, UP, UP_LEFT, LEFT, DOWN_LEFT};

}
