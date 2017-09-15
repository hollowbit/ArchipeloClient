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
import net.hollowbit.archipeloshared.EntityData;
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
			if (chunk == null)
				continue;
			
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
		/*if (rect.xWithOffset() < 0 || rect.yWithOffset() < 0 || rect.xWithOffset() + rect.width > getPixelWidth() || rect.yWithOffset() + rect.height > getPixelHeight())
			return true;*/
		
		//See if it collides with tiles and elements
		for (int row = (int) (rect.yWithOffset() / collisionBoxSize); row < Math.ceil((rect.height + rect.yWithOffset()) / collisionBoxSize) - 1; row++) {
			for (int col = (int) (rect.xWithOffset() / collisionBoxSize) - 1; col < Math.ceil((rect.width + rect.xWithOffset()) / collisionBoxSize); col++) {
				if (getTileCollisionAtPos(col, row))
					return true;
			}
		}
		
		if (world.collidesWithWorld(rect, entity))
			return true;
		
		return false;
	}
	
	private boolean getTileCollisionAtPos(int x, int y) {
		int chunkX = (int) Math.floor((float) x / (ChunkData.SIZE * TileData.COLLISION_MAP_SCALE));
		int chunkY = (int) Math.floor((float) y / (ChunkData.SIZE * TileData.COLLISION_MAP_SCALE));
		
		int xWithinChunk = x % (ChunkData.SIZE * TileData.COLLISION_MAP_SCALE);
		if (x < 0)
			xWithinChunk = (ChunkData.SIZE * TileData.COLLISION_MAP_SCALE) - (Math.abs(x + 1) % (ChunkData.SIZE * TileData.COLLISION_MAP_SCALE)) - 1;
		
		int yWithinChunk = y % (ChunkData.SIZE * TileData.COLLISION_MAP_SCALE);
		if (y < 0)
			yWithinChunk = (ChunkData.SIZE * TileData.COLLISION_MAP_SCALE) - (Math.abs(y + 1) % (ChunkData.SIZE * TileData.COLLISION_MAP_SCALE)) - 1;
		
		Chunk chunk = getChunk(chunkX, chunkY);
		if (chunk != null)
			return chunk.getCollisionMap()[yWithinChunk][xWithinChunk];
		return true;
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
		int tileY = (int) ((cameraViewRect.yWithOffset() + cameraViewRect.height) / ArchipeloClient.TILE_SIZE);
		//tileY = tileY < 0 ? 0 : tileY;
		int tileX = (int) (cameraViewRect.xWithOffset() / ArchipeloClient.TILE_SIZE);
		//tileX = tileX < 0 ? 0 : tileX;
		
		int tileY2 = (int) ((cameraViewRect.yWithOffset()) / ArchipeloClient.TILE_SIZE);
		//tileY2 = tileY2 > getHeight() ? getHeight() : tileY2;
		int tileX2 = (int) ((cameraViewRect.xWithOffset() + cameraViewRect.width) / ArchipeloClient.TILE_SIZE) + 1;
		//tileX2 = tileX2 > getWidth() ? getWidth() : tileX2;
		
		int chunkY1 = (int) Math.floor((float) tileY / ChunkData.SIZE) + 1;
		int chunkX1 = (int) Math.floor((float) tileX / ChunkData.SIZE) - 1;
		int chunkY2 = (int) Math.floor((float) tileY2 / ChunkData.SIZE) - 1;
		int chunkX2 = (int) Math.floor((float) tileX2 / ChunkData.SIZE);
		
		//System.out.println("Map.java  " + chunkY1 + "," + chunkX1 + " : " + chunkY2 + "," + chunkX2);
		for (int chunkY = chunkY1; chunkY >= chunkY2; chunkY--) {
			for (int chunkX = chunkX1; chunkX <= chunkX2; chunkX++) {
				ChunkRow row = chunkRows.get(chunkY);
				if (row == null)
					continue;
				
				Chunk chunk = row.getChunks().get(chunkX);
				if (chunk != null) {
					System.out.println("Map.java  " + chunkX + "," + chunkY);
					//Define inter-tile rendering limits
					int tileRenderY1 = ChunkData.SIZE;
					if (chunkY == chunkY1) {
						if (tileY >= 0)
							tileRenderY1 = tileY % ChunkData.SIZE;
						else
							tileRenderY1 = ChunkData.SIZE - (Math.abs(tileY) % (ChunkData.SIZE + 1));
					}
					
					int tileRenderY2 = 0;
					if (chunkY == chunkY2) {
						if (tileY2 >= 0)
							tileRenderY2 = tileY2 % ChunkData.SIZE;
						else
							tileRenderY2 = ChunkData.SIZE - (Math.abs(tileY2) % (ChunkData.SIZE + 1));
					}
					
					int tileRenderX1 = 0;
					if (chunkX == chunkX1) {
						if (tileX >= 0)
							tileRenderX1 = tileX % ChunkData.SIZE;
						else
							tileRenderX1 = ChunkData.SIZE - (Math.abs(tileX) % (ChunkData.SIZE + 1));
					}
					
					int tileRenderX2 = ChunkData.SIZE;
					if (chunkX == chunkX2) {
						if (tileX2 >= 0)
							tileRenderX2 = tileX2 % ChunkData.SIZE;
						else
							tileRenderX2 = ChunkData.SIZE - (Math.abs(tileX2) % (ChunkData.SIZE + 1));
					}
					
					//Make sure y is descending
					/*if (tileRenderY1 < tileRenderY2) {
						//Swap if in wrong order
						tileRenderY1 += tileRenderY2;
						tileRenderY2 = tileRenderY1 - tileRenderY2;
						tileRenderY1 = tileRenderY1 - tileRenderY2;
					}
					
					//Make sure x is ascending
					if (tileRenderX1 > tileRenderX2) {
						//Swap if in wrong order
						tileRenderX1 += tileRenderX2;
						tileRenderX2 = tileRenderX1 - tileRenderX2;
						tileRenderX1 = tileRenderX1 - tileRenderX2;
					}*/
					
					//System.out.println("Map.java   " + tileRenderX1 + "," + tileRenderX2 + "  :  " + tileRenderY1 + "," + tileRenderY2);
					for (int r = tileRenderY1 - 1; r >= tileRenderY2; r--) {
						int y = r * ArchipeloClient.TILE_SIZE + chunk.getPixelY();
						//System.out.println("Map.java  " + y);
						for (int c = tileRenderX1; c < tileRenderX2; c++) {
							/*if (r == tileRenderY1 - 1)
								System.out.println("Map.java  " + c);*/
							Tile tile = ArchipeloClient.getGame().getMapElementManager().getTile(chunk.getTiles()[r][c]);
							if (tile != null)
								tile.draw(batch, c * ArchipeloClient.TILE_SIZE + chunk.getPixelX(), y);
						}
					}
				}
				
			}
		}
		

		
		for (int chunkY = chunkY1; chunkY >= chunkY2; chunkY--) {
			for (int chunkX = chunkX1; chunkX <= chunkX2; chunkX++) {
				ChunkRow row = chunkRows.get(chunkY);
				if (row == null)
					continue;
				
				Chunk chunk = row.getChunks().get(chunkX);
				if (chunk != null) {
					//Define inter-tile rendering limits
					int tileRenderY1 = ChunkData.SIZE;
					if (chunkY == chunkY1) {
						if (tileY >= 0)
							tileRenderY1 = tileY % ChunkData.SIZE;
						else
							tileRenderY1 = ChunkData.SIZE - (Math.abs(tileY) % (ChunkData.SIZE + 1));
					}
					
					int tileRenderY2 = 0;
					if (chunkY == chunkY2) {
						if (tileY2 >= 0)
							tileRenderY2 = tileY2 % ChunkData.SIZE;
						else
							tileRenderY2 = ChunkData.SIZE - (Math.abs(tileY2) % (ChunkData.SIZE + 1));
					}
					
					int tileRenderX1 = 0;
					if (chunkX == chunkX1) {
						if (tileX >= 0)
							tileRenderX1 = tileX % ChunkData.SIZE;
						else
							tileRenderX1 = ChunkData.SIZE - (Math.abs(tileX) % (ChunkData.SIZE + 1));
					}
					
					int tileRenderX2 = ChunkData.SIZE;
					if (chunkX == chunkX2) {
						if (tileX2 >= 0)
							tileRenderX2 = tileX2 % ChunkData.SIZE;
						else
							tileRenderX2 = ChunkData.SIZE - (Math.abs(tileX2) % (ChunkData.SIZE + 1));
					}
					
					for (int r = tileRenderY1 - 1; r >= tileRenderY2; r--) {
						int y = r * ArchipeloClient.TILE_SIZE + chunk.getPixelY();
						for (int c = tileRenderX1; c < tileRenderX2; c++) {
							MapElement element = ArchipeloClient.getGame().getMapElementManager().getElement(chunk.getElements()[r][c]);
							
							if (element != null) {
								float x = c * ArchipeloClient.TILE_SIZE + chunk.getPixelX();
								if (cameraViewRect.collidesWith(element.getViewRect(c * ArchipeloClient.TILE_SIZE + chunk.getPixelX(), y)))
									element.draw(batch, x, y);
							}
						}
						
						//Render objects for row
						ArrayList<RenderableGameWorldObject> objectsInThisTileRow = new ArrayList<RenderableGameWorldObject>();
						
						//Add entities
						for (Entity entity : entities) {
							float y2 = entity.getRenderY();
							if (y2 > (r - 1) * ArchipeloClient.TILE_SIZE + chunk.getPixelY() && y2 <= r * ArchipeloClient.TILE_SIZE + chunk.getPixelY()) {
								objectsInThisTileRow.add(entity);
							}
						}
						
						//Add particles
						for (Particle particle : particleManager.getParticles()) {
							float y2 = particle.getRenderY();
							if (y2 > (r - 1) * ArchipeloClient.TILE_SIZE + chunk.getPixelY() && y2 <= r * ArchipeloClient.TILE_SIZE + chunk.getPixelY()) {
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
				}
			}
		}
		
		if (ArchipeloClient.SHOW_COLLISION_RECTS) {
			for (int chunkY = chunkY1; chunkY >= chunkY2; chunkY--) {
				for (int chunkX = chunkX1; chunkX <= chunkX2; chunkX++) {
					ChunkRow row = chunkRows.get(chunkY);
					if (row == null)
						continue;
					
					Chunk chunk = row.getChunks().get(chunkX);
					if (chunk != null) {
						int tileRenderY1 = ChunkData.SIZE;
						if (chunkY == chunkY1) {
							if (tileY >= 0)
								tileRenderY1 = tileY % ChunkData.SIZE;
							else
								tileRenderY1 = ChunkData.SIZE - (Math.abs(tileY) % (ChunkData.SIZE + 1));
						}
						
						int tileRenderY2 = 0;
						if (chunkY == chunkY2) {
							if (tileY2 >= 0)
								tileRenderY2 = tileY2 % ChunkData.SIZE;
							else
								tileRenderY2 = ChunkData.SIZE - (Math.abs(tileY2) % (ChunkData.SIZE + 1));
						}
						
						int tileRenderX1 = 0;
						if (chunkX == chunkX1) {
							if (tileX >= 0)
								tileRenderX1 = tileX % ChunkData.SIZE;
							else
								tileRenderX1 = ChunkData.SIZE - (Math.abs(tileX) % (ChunkData.SIZE + 1));
						}
						
						int tileRenderX2 = ChunkData.SIZE;
						if (chunkX == chunkX2) {
							if (tileX2 >= 0)
								tileRenderX2 = tileX2 % ChunkData.SIZE;
							else
								tileRenderX2 = ChunkData.SIZE - (Math.abs(tileX2) % (ChunkData.SIZE + 1));
						}
						
						for (int r = (tileRenderY1 - 1) * TileData.COLLISION_MAP_SCALE; r >= tileRenderY2 * TileData.COLLISION_MAP_SCALE; r--) {
							for (int c = tileRenderX1 * TileData.COLLISION_MAP_SCALE; c < tileRenderX2 * TileData.COLLISION_MAP_SCALE; c++) {
								if (chunk.getCollisionMap()[r][c]) {
									batch.draw(ArchipeloClient.getGame().getAssetManager().getTexture("invalid"), c * ArchipeloClient.TILE_SIZE / TileData.COLLISION_MAP_SCALE + chunk.getPixelX(), r * ArchipeloClient.TILE_SIZE / TileData.COLLISION_MAP_SCALE + chunk.getPixelY(), ArchipeloClient.TILE_SIZE / TileData.COLLISION_MAP_SCALE, ArchipeloClient.TILE_SIZE / TileData.COLLISION_MAP_SCALE);
								}
							}
						}
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
	
	public void applyFullSnapshot (ChunkData[] chunkDatas, EntityData[] entityDatas) {
		for (int i = 0; i < chunkDatas.length; i++) {
			ChunkData data = chunkDatas[i];
			if (data == null)
				continue;
			
			Chunk chunk = getChunk(data.x, data.y);
			if (chunk == null) {//Chunk not already loaded, so load it in using data
				EntityData entityData = entityDatas[i];
				
				ChunkRow row = chunkRows.get(data.y);
				if (row == null) {//Add chunk row if it doesn't exist
					row = new ChunkRow(data.y);
					chunkRows.put(data.y, row);
				}
				
				//Load chunk and add it to row along with entities
				row.getChunks().put(data.x, new Chunk(data, this));
				world.addEntitiesFromChunk(entityData);
			}
		}
		
		//Find chunks to unload
		ArrayList<Chunk> chunksToRemove = new ArrayList<Chunk>();
		for (ChunkRow row : chunkRows.values()) {
			for (Chunk chunk : row.getChunks().values()) {
				boolean remove = true;
				
				//Loop through chunks adjacent to player
				for (int r = -1 * (WorldSnapshotPacket.NUM_OF_CHUNKS_WIDE / 2); r <= WorldSnapshotPacket.NUM_OF_CHUNKS_WIDE / 2; r++) {
					for (int c = -1 * (WorldSnapshotPacket.NUM_OF_CHUNKS_WIDE / 2); c <= WorldSnapshotPacket.NUM_OF_CHUNKS_WIDE / 2; c++) {
						if (chunk.getX() == c + world.getPlayer().getLocation().getChunkX() && chunk.getY() == r + world.getPlayer().getLocation().getChunkY()) {
							remove = false;
							break;
						}
					}
				}
				
				//if chunk is not adjacent, add it to list to remove
				if (remove)
					chunksToRemove.add(chunk);
			}
		}
		
		//Unload found unadjacent chunks
		for (Chunk chunk : chunksToRemove) {
			System.out.println("Map.java removing: " + chunk.getX() + "," + chunk.getY());
			ChunkRow row = chunkRows.get(chunk.getY());
			row.getChunks().remove(chunk.getX());
			world.unloadEntitiesInChunk(chunk);
			
			if (row.getChunks().isEmpty())//Remove empty rows
				chunkRows.remove(chunk.getY());
		}
		/*
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
					for (int i = 0; i < WorldSnapshotPacket.NUM_OF_CHUNKS; i++) {
						ChunkData chunk = chunkDatas[i];
						if (chunk != null && chunk.x == newX && chunk.y == y) {
							row.getChunks().put(newX, new Chunk(chunk, this));
							world.addEntitiesFromChunk(entityDatas[i]);
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
					for (int i = 0; i < WorldSnapshotPacket.NUM_OF_CHUNKS; i++) {
						ChunkData chunk = chunkDatas[i];
						if (chunk != null && chunk.x == newX && chunk.y == y) {
							row.getChunks().put(newX, new Chunk(chunk, this));
							world.addEntitiesFromChunk(entityDatas[i]);
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
					for (int i = 0; i < WorldSnapshotPacket.NUM_OF_CHUNKS; i++) {
						ChunkData chunk = chunkDatas[i];
						if (chunk != null && chunk.x == x && chunk.y == newY) {
							newRow.getChunks().put(x, new Chunk(chunk, this));
							world.addEntitiesFromChunk(entityDatas[i]);
							break;
						}
					}
				}
				
				chunkRows.remove(minY);
			} else if (newY < minY) {
				for (int x = minX; x <= maxX; x++) {
					world.unloadEntitiesInChunk(getChunk(x, maxY));

					//Look for an add chunk at x and newY
					for (int i = 0; i < WorldSnapshotPacket.NUM_OF_CHUNKS; i++) {
						ChunkData chunk = chunkDatas[i];
						if (chunk != null && chunk.x == x && chunk.y == newY) {
							newRow.getChunks().put(x, new Chunk(chunk, this));
							world.addEntitiesFromChunk(entityDatas[i]);
							break;
						}
					}
				}
					
				chunkRows.remove(maxY);
			}
		}*/
	}
	
	public Chunk getChunk(int x, int y) {
		ChunkRow row = chunkRows.get(y);
		if (row == null)
			return null;
		
		return row.getChunks().get(x);
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
	
	public Tile getTile(int chunkX, int chunkY, int xWithinChunk, int yWithinChunk) {
		ChunkRow row = chunkRows.get(chunkY);
		if (row != null) {
			Chunk chunk = row.getChunks().get(chunkX);
			if (chunk != null)
				return ArchipeloClient.getGame().getMapElementManager().getTile(chunk.getTiles()[yWithinChunk][xWithinChunk]);
		}
		
		//Chunk at location not found, return null
		return null;
	}

	public Tile getTileTypeAtLocation(int tileX, int tileY) {
		int chunkX = (int) Math.floor((float) tileX / ChunkData.SIZE);
		int chunkY = (int) Math.floor((float) tileY / ChunkData.SIZE);

		int xWithinChunk = tileX % ChunkData.SIZE;
		if (tileX < 0)
			xWithinChunk = ChunkData.SIZE - (Math.abs(tileX + 1) % (ChunkData.SIZE)) - 1;
		int yWithinChunk = tileY % ChunkData.SIZE;
		if (tileY < 0)
			yWithinChunk = ChunkData.SIZE - (Math.abs(tileY + 1) % (ChunkData.SIZE)) - 1;
		
		return getTile(chunkX, chunkY, xWithinChunk, yWithinChunk);
	}
	
}
