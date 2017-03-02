package net.hollowbit.archipelo.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.entity.Entity;
import net.hollowbit.archipelo.entity.EntitySnapshot;
import net.hollowbit.archipelo.entity.EntityType;
import net.hollowbit.archipelo.entity.living.CurrentPlayer;
import net.hollowbit.archipelo.entity.living.Player;
import net.hollowbit.archipelo.network.Packet;
import net.hollowbit.archipelo.network.PacketHandler;
import net.hollowbit.archipelo.network.PacketType;
import net.hollowbit.archipelo.network.packets.EntityAddPacket;
import net.hollowbit.archipelo.network.packets.EntityRemovePacket;
import net.hollowbit.archipelo.network.packets.TeleportPacket;
import net.hollowbit.archipelo.screen.screens.GameScreen;
import net.hollowbit.archipelo.screen.screens.gamescreen.MapTagPopupText;
import net.hollowbit.archipelo.tools.FlagsManager;
import net.hollowbit.archipelo.tools.StaticTools;
import net.hollowbit.archipelo.tools.WorldSnapshotManager;
import net.hollowbit.archipeloshared.CollisionRect;
import net.hollowbit.archipeloshared.Direction;

public class World implements PacketHandler {

	private static final int FADE_COLOR_WHITE = 0;
	private static final int FADE_COLOR_BLACK = 1;
	
	private static final float FADE_TIME = 0.3f;
	
	public static final int TIME_SPEED = 20;//Time moves at 20 ticks per second.
	
	private float time;
	private float goalTime;
	private ArrayList<Entity> entities;
	private Map map;
	private MapSnapshot nextMapSnapshot;
	private HashMap<String, EntitySnapshot> nextEntitySnapshots;
	private TeleportPacket teleportPacket;
	private CurrentPlayer player;
	private Color fadeColor;
	private float fadeTimer;
	private GameScreen gameScreen;
	private boolean firstTimeLoading;
	FlagsManager flagsManager;
	
	private float timeSinceLastInterp = 0;
	
	public World (GameScreen gameScreen) {
		this.gameScreen = gameScreen;
		entities = new ArrayList<Entity>();
		nextMapSnapshot = null;
		time = 0;
		fadeTimer = 0;
		firstTimeLoading = true;
		fadeColor = getFadeColor(FADE_COLOR_BLACK);
		goalTime = time;
		flagsManager = new FlagsManager();
		ArchipeloClient.getGame().getNetworkManager().addPacketHandler(this);
	}
	
	public synchronized void update (float deltaTime) {
		timeSinceLastInterp += deltaTime;
		
		if (time < goalTime) 
			time += deltaTime * TIME_SPEED;
		
		if (map != null)
			map.update(deltaTime);
		
		//Calculate fraction of time until next interp snapshot
		float timeUntilNextInterp = timeSinceLastInterp / WorldSnapshotManager.TIME_BETWEEN_UPDATES;
		if (timeUntilNextInterp > 1)//Can cause bugs if over 1 aka 100%
			timeUntilNextInterp = 1;
		
		for (Entity entity : entities) {
			entity.update(deltaTime, timeUntilNextInterp);
		}
		
		//Fade map in
		if (fadeTimer < 0) {
			fadeTimer += deltaTime;
			
			//When done, load map and get ready to fade out
			if (fadeTimer > 0) {
				loadMap();
				fadeTimer = FADE_TIME * (firstTimeLoading ? 3 : 1);
				if (teleportPacket != null) {
					player.getLocation().set(teleportPacket.x, teleportPacket.y, Direction.values()[teleportPacket.direction]);
					teleportPacket = null;
				}
			}
		}
		
		//Fade out
		if (fadeTimer > 0) {
			fadeTimer -= deltaTime;
			
			//When done, put popup text
			if (fadeTimer <= 0) {
				fadeTimer = 0;
				firstTimeLoading = false;
				if (gameScreen != null)
					gameScreen.getPopupTextManager().addPopupText(new MapTagPopupText(map.getDisplayName(), gameScreen.getPopupTextManager()));
			}
		}
	}
	
	public synchronized void render (SpriteBatch batch) {
		if (map != null)
			map.render(batch, entities);//Entities is passed because they are drawn by the map. This allows map elements to appear in front of entities if they belong there.
	}
	
	/**
	 * Properly dispose of game world
	 */
	public void dispose () {
		this.flagsManager.dispose();
		ArchipeloClient.getGame().getNetworkManager().removePacketHandler(this);
	}
	
	public boolean allowPositionCorrection () {//Don't allow position correction if about to teleport to new map
		return teleportPacket == null;
	}
	
	public void renderUi (SpriteBatch batch) {
		if (fadeTimer < 0)
			fadeColor.a = 1 - (1 / FADE_TIME / (firstTimeLoading ? 3 : 1)) * Math.abs(fadeTimer);
		
		if (fadeTimer > 0)
			fadeColor.a = (1 / FADE_TIME / (firstTimeLoading ? 3 : 1)) * Math.abs(fadeTimer);
		
		if (fadeTimer != 0) {
			batch.setColor(fadeColor);
			batch.draw(ArchipeloClient.getGame().getAssetManager().getTexture("blank"), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		}
		fadeColor.a = 1;//Reset fade of color
		batch.setColor(1, 1, 1, 1);
	}
	
	public synchronized void applyInterpWorldSnapshot (long timeStamp, WorldSnapshot snapshot1, WorldSnapshot snapshot2, float fraction) {
		goalTime = StaticTools.singleDimensionLerp(snapshot1.time, snapshot2.time, fraction);
		
		if (teleportPacket != null)//Don't apply interp if client is teleporting
			return;
		
		for (Entity entity : entities) {
			EntitySnapshot entitySnapshot1 = snapshot1.entitySnapshots.get(entity.getName());
			EntitySnapshot entitySnapshot2 = snapshot2.entitySnapshots.get(entity.getName());
			
			if (entitySnapshot1 != null && entitySnapshot2 != null)
				entity.applyInterpSnapshot(timeStamp, entitySnapshot1, entitySnapshot2, fraction);
		}
		
		timeSinceLastInterp = 0;
	}
	
	public synchronized void applyChangesWorldSnapshot (WorldSnapshot snapshot) {
		if (map == null || teleportPacket != null)
			return;
		
		map.applyChangesSnapshot(snapshot.mapSnapshot);
		
		for (Entity entity : entities) {
			EntitySnapshot entitySnapshot = snapshot.entitySnapshots.get(entity.getName());
			if (entitySnapshot != null)
				entity.applyChangesSnapshot(entitySnapshot);
		}
	}
	
	//Full world snapshots mean that the player is on a new map
	public void applyFullWorldSnapshot (WorldSnapshot snapshot) {	
		this.fadeColor = getFadeColor(snapshot.mapSnapshot.getInt("fade-color", FADE_COLOR_BLACK));
		this.nextMapSnapshot = snapshot.mapSnapshot;
		this.nextEntitySnapshots = snapshot.entitySnapshots;
		
		if (firstTimeLoading)
			fadeTimer = -0.0001f;
		else
			fadeTimer = -FADE_TIME;
	}
	
	private void loadMap () {
		map = new Map(nextMapSnapshot, this);
		for (Entity entity : entities)
			entity.unload();
		entities.clear();
		
		for (Entry<String, EntitySnapshot> entitySnapshot : nextEntitySnapshots.entrySet()) {
			Entity entity = null;
			
			//If it is the current player, use a custom creator, else use the default one
			if (entitySnapshot.getValue().name.equals(ArchipeloClient.getGame().getPlayerName())) {
				player = new CurrentPlayer();
				player.create(entitySnapshot.getValue(), map, EntityType.PLAYER);
				ArchipeloClient.getGame().getCamera().focusOnEntityFast(player);
				entity = player;
			} else
				entity = EntityType.createEntityBySnapshot(entitySnapshot.getValue(), map);
			entity.load();
			entities.add(entity);
		}
		nextMapSnapshot = null;
		nextEntitySnapshots = null;
	}
	
	public CurrentPlayer getPlayer () {
		return player;
	}
	
	public Player getPlayer (String name) {
		for (Entity entity : entities) {
			if (entity.getName().equals(name) && entity.isPlayer())
				return (Player) entity;
		}
		return null;
	}
	
	public Map getMap () {
		return map;
	}
	
	private Entity getEntity (String name) {
		for (Entity entity : entities) {
			if (entity.getName().equals(name))
				return entity;
		}
		return null;
	}
	
	public FlagsManager getFlagsManager () {
		return flagsManager;
	}
	
	/**
	 * Checks if rect collides with entities
	 * @param rect
	 * @return
	 */
	public boolean collidesWithWorld (CollisionRect rect, Entity testEntity) {
		boolean isPlayer = testEntity instanceof Player;
		//Check collisions with entities
		for (Entity entity : entities) {
			if (entity == testEntity)
				continue;
			
			for (CollisionRect entityRect : entity.getCollisionRects()) {
				if (!entityRect.hard)
					continue;
				
				if (isPlayer && entity.ignoreHardnessOfCollisionRects((Player) testEntity, entityRect.name))
					continue;
				
				if (entityRect.collidesWith(rect))
					return true;
			}
		}
		
		return false;
	}
	
	public synchronized ArrayList<Entity> cloneEntitiesList() {
		ArrayList<Entity> entitiesClone = new ArrayList<Entity>();
		entitiesClone.addAll(entities);
		return entitiesClone;
	}

	@Override
	public boolean handlePacket (Packet packet) {
		if (packet.packetType == PacketType.ENTITY_ADD) {
			EntityAddPacket entityAddPacket = (EntityAddPacket) packet;
			Entity entity = EntityType.createEntityBySnapshot(new EntitySnapshot(entityAddPacket.name, entityAddPacket.type, entityAddPacket.properties, entityAddPacket.anim, entityAddPacket.animMeta, entityAddPacket.animTime), map);
			entities.add(entity);
			entity.load();
			
			/*This shouldn't be an issue. The current player should never be added into the map using Entity Add packet
			if (entity.getName().equals(ArchipeloClient.getGame().getPlayerName())) {
				player = (Player) entity;
				player.setIsCurrentPlayer(true);
				player.createCurrentPlayer();
				ArchipeloClient.getGame().getCamera().focusOnEntityFast(player);
			}*/
			return true;
		} else
		if (packet.packetType == PacketType.ENTITY_REMOVE) {
			Entity entity = getEntity(((EntityRemovePacket) packet).name);
			entity.unload();
			entities.remove(entity);
			return true;
		} else
		if (packet.packetType == PacketType.TELEPORT) {
			TeleportPacket tpPacket = (TeleportPacket) packet;
			Entity entity = getEntity(tpPacket.username);
			if (entity == player && tpPacket.newMap) {
				teleportPacket = tpPacket;
			} else {
				if (entity != null)
					entity.getLocation().set(tpPacket.x, tpPacket.y, Direction.values()[tpPacket.direction]);
			}
			return true;
		}
		return false;
	}
	
	private Color getFadeColor (int fadeColor) {
		switch (fadeColor) {
		case FADE_COLOR_WHITE:
			return new Color(Color.WHITE);
		case FADE_COLOR_BLACK:
			return new Color(Color.BLACK);
		}
		return Color.BLACK;
	}
	
}
