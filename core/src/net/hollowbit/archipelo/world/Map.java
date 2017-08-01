package net.hollowbit.archipelo.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.audio.MapAudioManager;
import net.hollowbit.archipelo.entity.Entity;
import net.hollowbit.archipelo.network.packets.WorldSnapshotPacket;
import net.hollowbit.archipelo.particles.Particle;
import net.hollowbit.archipelo.particles.ParticleManager;
import net.hollowbit.archipelo.tools.rendering.RenderableGameWorldObject;
import net.hollowbit.archipelo.tools.rendering.RenderableGameWorldObjectComparator;
import net.hollowbit.archipelo.world.map.Chunk;
import net.hollowbit.archipelo.world.map.ChunkRow;
import net.hollowbit.archipeloshared.ChunkData;
import net.hollowbit.archipeloshared.CollisionRect;
import net.hollowbit.archipeloshared.MapSnapshot;
import net.hollowbit.archipeloshared.TileData;

public class Map {
	
	private String name;
	private String displayName;
	private String islandName;
	private String music;
	private MapAudioManager audioManager;
	private ParticleManager particleManager;
	private TreeMap<Integer, ChunkRow> chunkRows;
	
	World world;
	
	public Map (MapSnapshot fullSnapshot, ChunkData[] chunks, World world) {
		this.name = fullSnapshot.name;
		this.world = world;
		this.displayName = fullSnapshot.getString("display-name", name);
		this.music = fullSnapshot.getString("music", "main-theme");//Default song is main theme
		audioManager = new MapAudioManager(this);
		particleManager = new ParticleManager();
		chunkRows = new TreeMap<Integer, ChunkRow>();
		
		//Add all chunks to map
		for (ChunkData chunk : chunks) {
			ChunkRow row = chunkRows.get(chunk.y);
			if (row == null) {
				row = new ChunkRow(chunk.y);
				chunkRows.put(chunk.y, row);
			}
			
			row.getChunks().put(chunk.x, new Chunk(chunk, this));
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
	}
	
	public void applyFullSnapshot (ChunkData[] chunkDatas) {
		boolean isNewX = true;
		int newX = 0;
		boolean isNewY = true;
		int newY = 0;
		
		boolean set = false;
		
		for (int i = 0; i < chunkDatas.length; i++) {
			ChunkData chunkData = chunkDatas[i];
			if (chunkData != null) {
				if (set) {
					if (chunkData.x != newX)
						isNewX = false;
					if (chunkData.y != newY)
						isNewY = false;
				} else {
					newX = chunkData.x;
					newY = chunkData.y;
					set = true;
				}
			}
		}
		

		int minX = 0;
		int maxX = 0;
		
		for (ChunkRow row : chunkRows.values()) {
			minX = row.getChunks().firstKey();
			maxX = row.getChunks().lastKey();
		}
		
		int minY = chunkRows.firstKey();
		int maxY = chunkRows.lastKey();
		
		//Determine if new chunk row or column was added
		if (isNewX) {
			//Determine which end a new column was added
			if (newX > maxX) {
				for (int y = minY; y <= maxY; y++) {
					world.unloadEntitiesInChunk(getChunk(minX, y));
					
					ChunkRow row = chunkRows.get(y);
					row.getChunks().remove(minX);

					//Look for an add chunk at newX and y
					for (ChunkData chunk : chunkDatas) {
						if (chunk != null && chunk.x == newX && chunk.y == y) {
							row.getChunks().put(newX, new Chunk(chunk, this));
							break;
						}
					}
				}
			} else if (newX < minX) {
				for (int y = minY; y <= maxY; y++) {
					world.unloadEntitiesInChunk(getChunk(maxX, y));
					
					ChunkRow row = chunkRows.get(y);
					row.getChunks().remove(minX);

					//Look for an add chunk at newX and y
					for (ChunkData chunk : chunkDatas) {
						if (chunk != null && chunk.x == newX && chunk.y == y) {
							row.getChunks().put(newX, new Chunk(chunk, this));
							break;
						}
					}
				}
			}
		} else if (isNewY) {
			//Determine which end a new row was added
			ChunkRow newRow = new ChunkRow(newY);
			chunkRows.put(newY, newRow);
			if (newY > maxY) {
				
				for (int x = minX; x <= maxX; x++) {
					world.unloadEntitiesInChunk(getChunk(x, minY));

					//Look for an add chunk at x and newY
					for (ChunkData chunk : chunkDatas) {
						if (chunk != null && chunk.x == x && chunk.y == newY) {
							newRow.getChunks().put(x, new Chunk(chunk, this));
							break;
						}
					}
				}
				
				chunkRows.remove(minY);
			} else if (newY < minY) {
				for (int x = minX; x <= maxX; x++) {
					world.unloadEntitiesInChunk(getChunk(x, maxY));
					
					//Look for an add chunk at x and newY
					for (ChunkData chunk : chunkDatas) {
						if (chunk != null && chunk.x == x && chunk.y == newY) {
							newRow.getChunks().put(x, new Chunk(chunk, this));
							break;
						}
					}
				}
					
				chunkRows.remove(maxY);
			}
		}
	}
	
	public Chunk getChunk(int x, int y) {
		ChunkRow row = chunkRows.get(y);
		if (row == null)
			return null;
		
		return row.getChunks().get(x);
	}
	
	public Tile getTileTypeAtLocation (int tileX, int tileY) {
		//TODO Rewrite
		/*if (tileX < 0 || tileX >= tileData[0].length || tileY < 0 || tileY >= tileData.length)
			return null;
		
		return ArchipeloClient.getGame().getMapElementManager().getTile(tileData[tileData.length - tileY - 1][tileX]);*/
		return null;
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
	
	/**
	 * Same as height
	 * @return
	 */
	public int getWidth () {
		return WorldSnapshotPacket.NUM_OF_CHUNKS_WIDE * ChunkData.SIZE;
	}
	
	/**
	 * Same as width.
	 * @return
	 */
	public int getHeight () {
		return WorldSnapshotPacket.NUM_OF_CHUNKS_WIDE * ChunkData.SIZE;
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
