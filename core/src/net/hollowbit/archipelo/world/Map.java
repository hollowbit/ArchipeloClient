package net.hollowbit.archipelo.world;

import java.util.ArrayList;
import java.util.Collections;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.entity.Entity;
import net.hollowbit.archipelo.entity.EntityHeightComparator;
import net.hollowbit.archipeloshared.CollisionRect;
import net.hollowbit.archipeloshared.TileData;

public class Map {
	
	private String name;
	private String displayName;
	private String islandName;
	private String[][] tileData;
	private String[][] elementData;
	private String music;
	
	boolean[][] collisionMap;
	World world;
	
	public Map (MapSnapshot fullSnapshot, World world) {
		this.name = fullSnapshot.name;
		this.world = world;
		this.displayName = fullSnapshot.getString("display-name", name);
		this.music = fullSnapshot.getString("music", "main-theme");//Default song is main theme
		this.islandName = fullSnapshot.getString("island-name", "Archipelo");
		tileData = fullSnapshot.tileData;
		elementData = fullSnapshot.elementData;
		generateCollisionMap();
	}
	
	private void generateCollisionMap () {
		collisionMap = new boolean[tileData.length * TileData.COLLISION_MAP_SCALE][tileData[0].length * TileData.COLLISION_MAP_SCALE];
		for (int row = 0; row < tileData.length; row++) {
			for (int col = 0; col < tileData[0].length; col++) {
				//Apply tile collision map
				Tile tile = ArchipeloClient.getGame().getMapElementManager().getTile(tileData[row][col]);
				for (int tileRow = 0; tileRow < tile.getCollisionTable().length; tileRow++) {
					for (int tileCol = 0; tileCol < tile.getCollisionTable()[0].length; tileCol++) {
						int x = col * TileData.COLLISION_MAP_SCALE + tileCol;
						int y = collisionMap.length - row * TileData.COLLISION_MAP_SCALE + tileRow;
						
						//if it is out of bounds, don't apply it.
						if (y < 0 || y >= collisionMap.length || x < 0 || x >= collisionMap[0].length)
							continue;
						
						collisionMap[y][x] = (tile.getCollisionTable()[tile.getCollisionTable().length - tileRow - 1][tileCol] ? true: collisionMap[y][x]);
					}
				}
				
				
				MapElement element = ArchipeloClient.getGame().getMapElementManager().getElement(elementData[row][col]);
				
				if (element != null) {
					for (int elementRow = 0; elementRow < element.getCollisionTable().length; elementRow++) {
						for (int elementCol = 0; elementCol < element.getCollisionTable()[0].length; elementCol++) {
							int x = col * TileData.COLLISION_MAP_SCALE + elementCol + element.offsetX;
							int y = collisionMap.length - row * TileData.COLLISION_MAP_SCALE + elementRow + element.offsetY;
							
							//If it is out of bounds, don't apply it.
							if (y < 0 || y >= collisionMap.length || x < 0 || x >= collisionMap[0].length)
								continue;
							
							collisionMap[y][x] = (element.getCollisionTable()[element.getCollisionTable().length - elementRow - 1][elementCol] ? true: collisionMap[y][x]);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Checks if entity and rect collide with map and world
	 * @param rect
	 * @param entity
	 * @return
	 */
	public boolean collidesWithMap (CollisionRect rect, Entity entity) {
		//See if collisionrect collides with map
		if (rect.x < - ArchipeloClient.TILE_SIZE || rect.y < + ArchipeloClient.TILE_SIZE || rect.x + rect.width > getPixelWidth() - ArchipeloClient.TILE_SIZE || rect.y + rect.height > getPixelHeight() + ArchipeloClient.TILE_SIZE - rect.height)
			return true;
		
		//See if it collides with tiles and elements
		int collisionBoxSize = (int) ArchipeloClient.TILE_SIZE / TileData.COLLISION_MAP_SCALE;
		CollisionRect tileRect = new CollisionRect(0, 0, 0, 0, collisionBoxSize, collisionBoxSize);
		for (int row = (int) (rect.y / collisionBoxSize) - 1; row < (int) (rect.height / collisionBoxSize) + (rect.y / collisionBoxSize) + 2; row++) {
			for (int col = (int) (rect.x / collisionBoxSize) - 1; col < (int) (rect.width / collisionBoxSize) + (rect.x / collisionBoxSize) + 2; col++) {
				if (row < 0 || row >= collisionMap.length || col < 0 || col >= collisionMap[0].length)//If out of bounds, continue to next
					continue;
				
				if (collisionMap[row][col]) {
					tileRect.move(col * tileRect.width, row * tileRect.height);
					if (tileRect.collidesWith(rect))
						return true;
				}
			}
		}
		
		if (world.collidesWithWorld(rect, entity))
			return true;
		
		return false;
	}
	
	public String getName () {
		return name;
	}
	
	public void update (float deltaTime) {
		
	}
	
	public void render (SpriteBatch batch, ArrayList<Entity> entities) {
		CollisionRect cameraViewRect = ArchipeloClient.getGame().getCamera().getViewRect();
		
		//Render tiles
		//Find minimum amount of tiles to draw to save processing power
		int tileY = tileData.length - (int) ((cameraViewRect.y + cameraViewRect.height) / ArchipeloClient.TILE_SIZE);
		tileY = tileY < 0 ? 0 : tileY;
		int tileX = (int) (cameraViewRect.x / ArchipeloClient.TILE_SIZE);
		tileX = tileX < 0 ? 0 : tileX;
		
		int tileY2 = tileData.length - (int) ((cameraViewRect.y) / ArchipeloClient.TILE_SIZE) + 1;
		tileY2 = tileY2 > tileData.length ? tileData.length : tileY2;
		int tileX2 = (int) ((cameraViewRect.x + cameraViewRect.width) / ArchipeloClient.TILE_SIZE) + 1;
		tileX2 = tileX2 > tileData[0].length ? tileData[0].length : tileX2;
		
		for (int r = tileY; r < tileY2; r++) {
			for (int c = tileX; c < tileX2; c++) {
				ArchipeloClient.getGame().getMapElementManager().getTile(tileData[r][c]).draw(batch, c * ArchipeloClient.TILE_SIZE, tileData.length * ArchipeloClient.TILE_SIZE - r * ArchipeloClient.TILE_SIZE);
			}
		}
		
		for (int r = 0; r < elementData.length; r++) {
			//Render elements in this row, by column
			for (int c = 0; c < elementData[0].length; c++) {
				MapElement element = ArchipeloClient.getGame().getMapElementManager().getElement(elementData[r][c]);
				if (element != null) {
					float x = c * ArchipeloClient.TILE_SIZE;
					float y = elementData.length * ArchipeloClient.TILE_SIZE - r * ArchipeloClient.TILE_SIZE;
					if (cameraViewRect.collidesWith(element.getViewRect(x, y)))
						element.draw(batch, x, y);
				}
			}
			
			//Render entities for row
			ArrayList<Entity> entitiesInThisTileRow = new ArrayList<Entity>();
			for (Entity entity : entities) {
				float y = entity.getDrawOrderY();
				if (y >= elementData.length * ArchipeloClient.TILE_SIZE - r * ArchipeloClient.TILE_SIZE - ArchipeloClient.TILE_SIZE - 1 && y < elementData.length * ArchipeloClient.TILE_SIZE - r * ArchipeloClient.TILE_SIZE + 1) {
					entitiesInThisTileRow.add(entity);
				}
			}
			
			Collections.sort(entitiesInThisTileRow, new EntityHeightComparator());
			
			//Render entities in tile row
			for (Entity entity : entitiesInThisTileRow) {
				if (cameraViewRect.collidesWith(entity.getViewRect()))
					entity.renderStart(batch);
			}
		}
		
		/*for (int r = 0; r < collisionMap.length; r++) {
			for (int c = 0; c < collisionMap[0].length; c++) {
				if (collisionMap[r][c]) {
					batch.draw(ArchipeloClient.getGame().getAssetManager().getTexture("invalid"), c * ArchipeloClient.TILE_SIZE / TileData.COLLISION_MAP_SCALE, r * ArchipeloClient.TILE_SIZE / TileData.COLLISION_MAP_SCALE, ArchipeloClient.TILE_SIZE / TileData.COLLISION_MAP_SCALE, ArchipeloClient.TILE_SIZE / TileData.COLLISION_MAP_SCALE);
				}
			}
		}*/
	}
	
	public void applyChangesSnapshot (MapSnapshot snapshot) {
		displayName = snapshot.getString("display-name", displayName);
		if (snapshot.tileData != null)
			tileData = snapshot.tileData;
		
		if (snapshot.elementData != null)
			elementData = snapshot.elementData;
	}
	
	public String getDisplayName () {
		return displayName;
	}
	
	public String getIslandName () {
		return islandName;
	}
	
	public World getWorld () {
		return world;
	}
	
	public int getWidth () {
		return tileData[0].length;
	}
	
	public int getHeight () {
		return tileData.length;
	}
	
	public int getPixelWidth () {
		return getWidth() * ArchipeloClient.TILE_SIZE;
	}
	
	public int getPixelHeight () {
		return getHeight() * ArchipeloClient.TILE_SIZE;
	}

	public String getMusic() {
		return music;
	}
	
}
