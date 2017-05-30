package net.hollowbit.archipelo.world;

import java.util.ArrayList;
import java.util.Collections;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.audio.MapAudioManager;
import net.hollowbit.archipelo.entity.Entity;
import net.hollowbit.archipelo.particles.Particle;
import net.hollowbit.archipelo.particles.ParticleManager;
import net.hollowbit.archipelo.tools.rendering.RenderableGameWorldObject;
import net.hollowbit.archipelo.tools.rendering.RenderableGameWorldObjectComparator;
import net.hollowbit.archipeloshared.CollisionRect;
import net.hollowbit.archipeloshared.MapSnapshot;
import net.hollowbit.archipeloshared.TileData;

public class Map {
	
	private String name;
	private String displayName;
	private String islandName;
	private String[][] tileData;
	private String[][] elementData;
	private String music;
	private MapAudioManager audioManager;
	private ParticleManager particleManager;
	
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
		audioManager = new MapAudioManager(this);
		particleManager = new ParticleManager();
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
						int y = row * TileData.COLLISION_MAP_SCALE + tileRow;
						
						//if it is out of bounds, don't apply it.
						if (y < 0 || y >= collisionMap.length || x < 0 || x >= collisionMap[0].length)
							continue;
						
						collisionMap[y][x] = (tile.getCollisionTable()[tileRow][tileCol] ? true: collisionMap[y][x]);
					}
				}
				
				
				MapElement element = ArchipeloClient.getGame().getMapElementManager().getElement(elementData[row][col]);
				
				if (element != null) {
					for (int elementRow = 0; elementRow < element.getCollisionTable().length; elementRow++) {
						for (int elementCol = 0; elementCol < element.getCollisionTable()[0].length; elementCol++) {
							int x = col * TileData.COLLISION_MAP_SCALE + elementCol + element.offsetX;
							int y = row * TileData.COLLISION_MAP_SCALE + elementRow + element.offsetY - (element.getCollisionTable().length - 1) + 1;
							
							//If it is out of bounds, don't apply it.
							if (y < 0 || y >= collisionMap.length || x < 0 || x >= collisionMap[0].length)
								continue;
							
							collisionMap[y][x] = (element.getCollisionTable()[elementRow][elementCol] ? true: collisionMap[y][x]);
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
		int collisionBoxSize = (int) ArchipeloClient.TILE_SIZE / TileData.COLLISION_MAP_SCALE;
		
		//See if collisionrect collides with map
		if (rect.xWithOffset() < 0 || rect.yWithOffset() < 0 || rect.xWithOffset() + rect.width > getPixelWidth() || rect.yWithOffset() + rect.height > getPixelHeight())
			return true;
		
		//See if it collides with tiles and elements
		for (int row = (int) (rect.yWithOffset() / collisionBoxSize); row < Math.ceil((rect.height + rect.yWithOffset()) / collisionBoxSize); row++) {
			for (int col = (int) (rect.xWithOffset() / collisionBoxSize); col < Math.ceil((rect.width + rect.xWithOffset()) / collisionBoxSize); col++) {
				if (row < 0 || row >= collisionMap.length || col < 0 || col >= collisionMap[0].length)//If out of bounds, continue to next
					continue;
				
				if (collisionMap[collisionMap.length - row - 1][col])
					return true;
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
		particleManager.update(deltaTime);
	}
	
	public void render (SpriteBatch batch, ArrayList<Entity> entities) {
		CollisionRect cameraViewRect = ArchipeloClient.getGame().getCamera().getViewRect();
		
		//Render tiles
		//Find minimum amount of tiles to draw to save processing power
		int tileY = tileData.length - (int) ((cameraViewRect.yWithOffset() + cameraViewRect.height) / ArchipeloClient.TILE_SIZE) - 1;
		tileY = tileY < 0 ? 0 : tileY;
		int tileX = (int) (cameraViewRect.xWithOffset() / ArchipeloClient.TILE_SIZE);
		tileX = tileX < 0 ? 0 : tileX;
		
		int tileY2 = tileData.length - (int) ((cameraViewRect.yWithOffset()) / ArchipeloClient.TILE_SIZE);
		tileY2 = tileY2 > tileData.length ? tileData.length : tileY2;
		int tileX2 = (int) ((cameraViewRect.xWithOffset() + cameraViewRect.width) / ArchipeloClient.TILE_SIZE) + 1;
		tileX2 = tileX2 > tileData[0].length ? tileData[0].length : tileX2;
		
		for (int r = tileY; r < tileY2; r++) {
			for (int c = tileX; c < tileX2; c++) {
				ArchipeloClient.getGame().getMapElementManager().getTile(tileData[r][c]).draw(batch, c * ArchipeloClient.TILE_SIZE, (tileData.length - r - 1) * ArchipeloClient.TILE_SIZE);
			}
		}
		
		for (int r = 0; r < elementData.length; r++) {
			//Render elements in this row, by column
			for (int c = 0; c < elementData[0].length; c++) {
				MapElement element = ArchipeloClient.getGame().getMapElementManager().getElement(elementData[r][c]);
				if (element != null) {
					float x = c * ArchipeloClient.TILE_SIZE;
					float y = (elementData.length - r - 1) * ArchipeloClient.TILE_SIZE;
					if (cameraViewRect.collidesWith(element.getViewRect(x, y)))
						element.draw(batch, x, y);
				}
			}
			
			//Render objects for row
			ArrayList<RenderableGameWorldObject> objectsInThisTileRow = new ArrayList<RenderableGameWorldObject>();
			
			//Add entities
			for (Entity entity : entities) {
				float y = entity.getRenderY();
				if (y > (elementData.length - r - 2) * ArchipeloClient.TILE_SIZE && y <= (elementData.length - r - 1) * ArchipeloClient.TILE_SIZE) {
					objectsInThisTileRow.add(entity);
				}
			}
			
			//Add particles
			for (Particle particle : particleManager.getParticles()) {
				float y = particle.getRenderY();
				if (y > (elementData.length - r - 2) * ArchipeloClient.TILE_SIZE && y <= (elementData.length - r - 1) * ArchipeloClient.TILE_SIZE) {
					objectsInThisTileRow.add(particle);
				}
			}
			
			Collections.sort(objectsInThisTileRow, new RenderableGameWorldObjectComparator());
			
			//Render entities in tile row
			for (RenderableGameWorldObject object : objectsInThisTileRow) {
				if (cameraViewRect.collidesWith(object.getViewRect()))
					object.renderObject(batch);
			}
		}
		
		if (ArchipeloClient.SHOW_COLLISION_RECTS) {
			for (int r = 0; r < collisionMap.length; r++) {
				for (int c = 0; c < collisionMap[0].length; c++) {
					if (collisionMap[r][c]) {
						batch.draw(ArchipeloClient.getGame().getAssetManager().getTexture("invalid"), c * ArchipeloClient.TILE_SIZE / TileData.COLLISION_MAP_SCALE, (collisionMap.length - r - 1) * ArchipeloClient.TILE_SIZE / TileData.COLLISION_MAP_SCALE, ArchipeloClient.TILE_SIZE / TileData.COLLISION_MAP_SCALE, ArchipeloClient.TILE_SIZE / TileData.COLLISION_MAP_SCALE);
					}
				}
			}
			
			//Render collision texture for hard entity rects
			for (Entity entity : entities) {
				for (CollisionRect rect : entity.getCollisionRects()) {
					if (rect.hard && !entity.ignoreHardnessOfCollisionRects(world.getPlayer(), rect.name))
						batch.draw(ArchipeloClient.getGame().getAssetManager().getTexture("invalid"), rect.xWithOffset(), rect.yWithOffset(), rect.width, rect.height);
				}
			}
		}
	}
	
	public void applyChangesSnapshot (MapSnapshot snapshot) {
		displayName = snapshot.getString("display-name", displayName);
		audioManager.applyChangesSnapshot(snapshot);
		particleManager.applyChangesSnapshot(snapshot);
		if (snapshot.tileData != null)
			tileData = snapshot.tileData;
		
		if (snapshot.elementData != null)
			elementData = snapshot.elementData;
	}
	
	public Tile getTileTypeAtLocation (int x, int y) {
		if (x < 0 || x >= tileData[0].length || y < 0 || y >= tileData.length)
			return null;
		
		return ArchipeloClient.getGame().getMapElementManager().getTile(tileData[tileData.length - y - 1][x]);
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
