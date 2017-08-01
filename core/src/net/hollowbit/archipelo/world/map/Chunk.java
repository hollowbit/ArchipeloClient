package net.hollowbit.archipelo.world.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.world.Map;
import net.hollowbit.archipelo.world.MapElement;
import net.hollowbit.archipelo.world.Tile;
import net.hollowbit.archipeloshared.ChunkData;

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
		
		int i = 0;
        for (int r = 0; r < collisionMap.length; r++) {
            for (int c = 0; c < collisionMap[0].length; c++) {
            	byte val = (byte) bytes[i / Byte.SIZE];
            	int pos = i % Byte.SIZE;
            	collisionMap[r][c] = ((val >> pos) & 1) == 1;
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
	
	public Map getMap() {
		return map;
	}
	
	public boolean[][] getCollisionMap() {
		return collisionMap;
	}
	
	public void renderTiles(SpriteBatch batch) {
		for (int r = 0; r < tiles.length; r++) {
			for (int c  = 0; c < tiles[0].length; c++) {
				@SuppressWarnings("unused")
				Tile tile = ArchipeloClient.getGame().getMapElementManager().getTile(tiles[r][c]);
			}
		}
	}
	
	public void renderElements(SpriteBatch batch, int row) {
		for (int c  = 0; c < tiles[0].length; c++) {
			@SuppressWarnings("unused")
			MapElement element = ArchipeloClient.getGame().getMapElementManager().getElement(elements[row][c]);
		}
	}
	
}
