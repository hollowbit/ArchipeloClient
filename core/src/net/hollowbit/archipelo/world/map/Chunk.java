package net.hollowbit.archipelo.world.map;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.world.Map;
import net.hollowbit.archipeloshared.ChunkData;
import net.hollowbit.archipeloshared.TileData;

public class Chunk {
	
	private int x, y;
	private String[][] tiles;
	private String[][] elements;
	private boolean[][] collisionMap;
	private Map map;
	
	public Chunk(ChunkData data, Map map) {
		this.x = data.x;
		this.y = data.y;
		this.tiles = data.tiles;
		this.elements = data.elements;
		
		//Deserialize collision data
		char[] bytes = data.collisionData.toCharArray();
		this.collisionMap = new boolean[ChunkData.SIZE * TileData.COLLISION_MAP_SCALE][ChunkData.SIZE * TileData.COLLISION_MAP_SCALE];
		int i = 0;
        for (int r = 0; r < collisionMap.length; r++) {
            for (int c = 0; c < collisionMap[0].length; c++) {
            	collisionMap[r][c] = bytes[i] == '1';
            	i++;
            }
        }
		
		this.map = map;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getPixelX() {
		return x * ChunkData.SIZE * ArchipeloClient.TILE_SIZE;
	}
	
	public int getPixelY() {
		return y * ChunkData.SIZE * ArchipeloClient.TILE_SIZE;
	}
	
	public Map getMap() {
		return map;
	}
	
	public boolean[][] getCollisionMap() {
		return collisionMap;
	}

	public String[][] getTiles() {
		return tiles;
	}

	public String[][] getElements() {
		return elements;
	}
	
}
