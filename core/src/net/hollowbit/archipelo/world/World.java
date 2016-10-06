package net.hollowbit.archipelo.world;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.entity.Entity;
import net.hollowbit.archipelo.entity.EntitySnapshot;
import net.hollowbit.archipelo.entity.EntityType;
import net.hollowbit.archipelo.entity.living.Player;
import net.hollowbit.archipelo.network.Packet;
import net.hollowbit.archipelo.network.PacketHandler;
import net.hollowbit.archipelo.network.PacketType;
import net.hollowbit.archipelo.network.packets.EntityAddPacket;
import net.hollowbit.archipelo.network.packets.EntityRemovePacket;
import net.hollowbit.archipelo.network.packets.TeleportPacket;
import net.hollowbit.archipelo.screen.screens.GameScreen;
import net.hollowbit.archipelo.screen.screens.gamescreen.MapTagPopupText;
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
	private ArrayList<EntitySnapshot> nextEntitySnapshots;
	private TeleportPacket teleportPacket;
	private Player player;
	private Color fadeColor;
	private float fadeTimer;
	private GameScreen gameScreen;
	private boolean firstTimeLoading;
	
	public World () {
		entities = new ArrayList<Entity>();
		nextMapSnapshot = null;
		time = 0;
		fadeTimer = 0;
		firstTimeLoading = true;
		fadeColor = getFadeColor(FADE_COLOR_BLACK);
		goalTime = time;
		ArchipeloClient.getGame().getNetworkManager().addPacketHandler(this);
	}
	
	public void setGameScreen (GameScreen gameScreen) {
		this.gameScreen = gameScreen;
	}
	
	public void removeGameScreen () {
		this.gameScreen = null;
	}
	
	public void update (float deltaTime, boolean[] controls) {
		if (time < goalTime) 
			time += deltaTime * TIME_SPEED;
		
		if (map != null)
			map.update(deltaTime);
		
		if (player != null)
			player.updatePlayer(deltaTime, controls);
		
		for (Entity entity : entities) {
			entity.update(deltaTime);
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
	
	public void render (SpriteBatch batch) {
		if (map != null)
			map.render(batch, entities);//Entities is passed because they are drawn by the map. This allows map elements to appear in front of entities if they belong there.
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
		fadeColor.a = 1;//Reset fade of Color.BLACK or Color.WHITE
		batch.setColor(1, 1, 1, 1);
	}
	
	public void applyInterpWorldSnapshot (WorldSnapshot snapshot) {
		goalTime = snapshot.time;
		for (EntitySnapshot entitySnapshot : snapshot.entitySnapshots) {
			Entity entity = getEntity(entitySnapshot.name);
			if (entity != null) {
				if (entity != player || teleportPacket == null)//Makes sure to only update player if they aren't teleporting
					entity.applyInterpSnapshot(snapshot.timeCreatedMillis, entitySnapshot);
			}
		}
	}
	
	public void applyChangesWorldSnapshot (WorldSnapshot snapshot) {
		if (map == null)
			return;
		
		map.applyChangesSnapshot(snapshot.mapSnapshot);
		for (EntitySnapshot entitySnapshot : snapshot.entitySnapshots) {
			Entity entity = getEntity(entitySnapshot.name);
			if (entity != null) {
				if (entity != player || teleportPacket == null)
					entity.applyChangesSnapshot(entitySnapshot);
			}
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
		for (Entity entity : entities) {
			entity.unload();
		}
		entities.clear();
		
		for (EntitySnapshot entitySnapshot : nextEntitySnapshots) {
			Entity entity = EntityType.createEntityBySnapshot(entitySnapshot, map);
			entity.load();
			entities.add(entity);
			if (entity.getName().equals(ArchipeloClient.getGame().getPrefs().getUsername())) {
				player = (Player) entity;
				player.setIsCurrentPlayer(true);
				player.createCurrentPlayer();
				ArchipeloClient.getGame().getCamera().focusOnEntityFast(player);
			}
		}
		nextMapSnapshot = null;
		nextEntitySnapshots = null;
	}
	
	public Player getPlayer () {
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

	@Override
	public boolean handlePacket (Packet packet) {
		if (packet.packetType == PacketType.ENTITY_ADD) {
			EntityAddPacket entityAddPacket = (EntityAddPacket) packet;
			Entity entity = EntityType.createEntityBySnapshot(new EntitySnapshot(entityAddPacket.username, entityAddPacket.type, entityAddPacket.style, entityAddPacket.properties), map);
			entities.add(entity);
			entity.load();
			if (entity.getName().equals(ArchipeloClient.getGame().getPrefs().getUsername())) {
				player = (Player) entity;
				player.setIsCurrentPlayer(true);
				player.createCurrentPlayer();
				ArchipeloClient.getGame().getCamera().focusOnEntityFast(player);
			}
			return true;
		} else
		if (packet.packetType == PacketType.ENTITY_REMOVE) {
			Entity entity = getEntity(((EntityRemovePacket) packet).username);
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
			return Color.WHITE;
		case FADE_COLOR_BLACK:
			return Color.BLACK;
		}
		return Color.BLACK;
	}
	
}
