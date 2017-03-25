package net.hollowbit.archipelo.tools;

import com.badlogic.gdx.math.Vector2;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.world.Map;
import net.hollowbit.archipelo.world.World;
import net.hollowbit.archipeloshared.Direction;

public class Location {
	
	public Direction direction;
	public Vector2 pos;
	public Map map;
	
	public Location (Map map, Vector2 pos) {
		this.map = map;
		this.pos = pos;
	}
	
	public Location (Map map, Vector2 pos, Direction direction) {
		this (map, pos);
		this.direction = direction;
	}
	
	public void set (float x, float y, Direction direction) {
		pos.set(x, y);
		this.direction = direction;
	}
	
	public float getX () {
		return pos.x;
	}
	
	public float getY () {
		return pos.y;
	}
	
	public int getTileX () {
		return (int) (pos.x / ArchipeloClient.TILE_SIZE);
	}
	
	public int getTileY () {
		return (int) (pos.y / ArchipeloClient.TILE_SIZE);
	}
	
	public void setX (float x) {
		pos.x = x;
	}
	
	public void setY (float y) {
		pos.y = y;
	}
	
	public Direction getDirection () {
		return direction;
	}
	
	public void setDirection (Direction direction) {
		this.direction = direction;
	}
	
	public Map getMap () {
		return map;
	}
	
	public World getWorld () {
		return map.getWorld();
	}
	
	public void addY (float amount) {
		pos.add(0, amount);
	}
	
	public void addX (float amount) {
		pos.add(amount, 0);
	}
	
}
