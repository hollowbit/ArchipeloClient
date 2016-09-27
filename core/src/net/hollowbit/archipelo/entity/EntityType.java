package net.hollowbit.archipelo.entity;

import java.util.HashMap;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.entity.lifeless.Door;
import net.hollowbit.archipelo.entity.lifeless.Teleporter;
import net.hollowbit.archipelo.entity.living.Player;
import net.hollowbit.archipelo.world.Map;
import net.hollowbit.archipeloshared.CollisionRect;
import net.hollowbit.archipeloshared.Direction;
import net.hollowbit.archipeloshared.EntityAnimationData;
import net.hollowbit.archipeloshared.EntityTypeData;

@SuppressWarnings("rawtypes")
public enum EntityType {
	
	PLAYER ("player", Player.class),
	TELEPORTER ("teleporter", Teleporter.class),
	DOOR ("door", Door.class);
	
	private String id;
	private Class entityClass;
	private HashMap<String, EntityAnimation> animations;
	private int numberOfStyles;
	private boolean hittable;
	private boolean collidable;
	private float speed;
	
	private int imgWidth;
	private int imgHeight;
	
	//Rects
	private CollisionRect viewRect;
	private CollisionRect[] collRects;
	
	private EntityType (String id, Class entityClass) {
		this.id = id;
		this.entityClass = entityClass;
		
		//Load rest of data from file
		Json json = new Json();
		EntityTypeData data = json.fromJson(EntityTypeData.class, Gdx.files.internal("shared/entities/" + id + ".json").readString());
		
		this.numberOfStyles = data.numberOfStyles;
		this.hittable = data.hittable;
		this.collidable = data.collidable;
		this.speed = data.speed;
		
		this.imgWidth = data.imgWidth;
		this.imgHeight = data.imgHeight;
		
		this.viewRect = new CollisionRect(0, 0, data.viewRectOffsetX, data.viewRectOffsetY, data.viewRectWidth, data.viewRectHeight);
		
		this.collRects = new CollisionRect[data.collisionRects.length];
		for (int i = 0; i < collRects.length; i++) {
			this.collRects[i] = new CollisionRect(data.collisionRects[i]);
		}
		
		//Load animations
		animations = new HashMap<String, EntityAnimation>();
		for (EntityAnimationData animationData : data.animations) {
			animations.put(animationData.id, new EntityAnimation(animationData));
		}
	}
	
	@SuppressWarnings("unchecked")
	public Entity getNewEntityOfType (EntitySnapshot fullSnapshot, Map map) {
		Entity entity = null;
		try {
			entity = (Entity) ClassReflection.newInstance(entityClass);
		} catch (ReflectionException e) {
			Gdx.app.log("EntityType", "Could not load new instance of entity type for: " + id + ".");
			Gdx.app.exit();
		}
		entity.create(fullSnapshot, map, this);
		return entity;
	}
	
	public String getId () {
		return id;
	}
	
	public CollisionRect getViewRect (float x, float y) {
		return viewRect.move(x, y);
	}
	
	public CollisionRect[] getCollisionRects (float x, float y) {
		for (CollisionRect rect : collRects) {
			rect.move(x, y);
		}
		return collRects;
	}
	
	public int getNumberOfStyles () {
		return numberOfStyles;
	}
	
	public boolean isHittable () {
		return hittable;
	}
	
	public boolean isCollidable () {
		return collidable;
	}
	
	public float getSpeed () {
		return speed;
	}
	
	public TextureRegion getAnimationFrame (String id) {
		return animations.get(id).getAnimationFrame(Direction.UP, ArchipeloClient.STATE_TIME, 0);
	}
	
	public TextureRegion getAnimationFrame (String id, Direction direction) {
		return animations.get(id).getAnimationFrame(direction, ArchipeloClient.STATE_TIME, 0);
	}
	
	public TextureRegion getAnimationFrame (String id, Direction direction, float stateTime) {
		return animations.get(id).getAnimationFrame(direction, stateTime, 0);
	}
	
	public TextureRegion getAnimationFrame (String id, float stateTime) {
		return animations.get(id).getAnimationFrame(Direction.UP, stateTime, 0);
	}
	
	public TextureRegion getAnimationFrame (String id, float stateTime, int style) {
		return animations.get(id).getAnimationFrame(Direction.UP, stateTime, style);
	}
	
	public TextureRegion getAnimationFrame (String id, int style) {
		return animations.get(id).getAnimationFrame(Direction.UP, ArchipeloClient.STATE_TIME, style);
	}
	
	public TextureRegion getAnimationFrame (String id, Direction direction, float stateTime, int style) {
		return animations.get(id).getAnimationFrame(direction, stateTime, style);
	}
	
	public void loadImages () {
		Iterator it = animations.entrySet().iterator();
		while (it.hasNext()) {
			((EntityAnimation) ((HashMap.Entry)it.next()).getValue()).loadImages(id, numberOfStyles, imgWidth, imgHeight);
		}
	}
	
	public void unloadImages () {
		Iterator it = animations.entrySet().iterator();
		while (it.hasNext()) {
			((EntityAnimation) ((HashMap.Entry)it.next()).getValue()).unloadImages();
		}
	}
	
	//Static
	private static HashMap<String, EntityType> entityTypeMap;
	
	static {
		entityTypeMap = new HashMap<String, EntityType>();
		for (EntityType entityType : EntityType.values()) {
			entityTypeMap.put(entityType.getId(), entityType);
		}
	}
	
	public static Entity createEntityBySnapshot (EntitySnapshot fullSnapshot, Map map) {
		return entityTypeMap.get(fullSnapshot.entityType).getNewEntityOfType(fullSnapshot, map);
	}
	
	public static void loadAllImages () {
		for (EntityType entityType : EntityType.values()) {
			entityType.loadImages();
		}
	}
	
	public static void unloadAllImages () {
		for (EntityType entityType : EntityType.values()) {
			entityType.unloadImages();
		}
	}
	
	private class EntityAnimation {
		
		Animation[][] animations;
		EntityAnimationData entityAnimationData;
		Texture[] srcTextures;
		boolean loaded;
		
		public EntityAnimation (EntityAnimationData entityAnimationData) {
			this.entityAnimationData = entityAnimationData;
			loaded = false;
		}
		
		public void loadImages (String entityId, int numberOfStyles, int imgWidth, int imgHeight) {
			if (!loaded) {
				loaded = true;
				animations = new Animation[numberOfStyles][entityAnimationData.numberOfDirections];
				srcTextures = new Texture[numberOfStyles];
				for (int i = 0; i < numberOfStyles; i++) {
					String fileName = entityAnimationData.fileName.equals("") ? entityAnimationData.id : entityAnimationData.fileName;
					srcTextures[i] = new Texture("entities/" + entityId + "/" + fileName + "_" + i + ".png");
					TextureRegion[][] animationRegion = TextureRegion.split(srcTextures[i], imgWidth, imgHeight);
					for (int u = 0; u < entityAnimationData.numberOfDirections; u++) {
						animations[i][u] = new Animation(entityAnimationData.timeBetweenFrames, animationRegion[u]);
					}
				}
			}
		}
		
		public void unloadImages () {
			if (loaded) {
				loaded = false;
				for (Texture texture : srcTextures) {
					texture.dispose();
				}
			}
		}
		
		public TextureRegion getAnimationFrame (Direction direction, float stateTime, int style) {
			int animationIndex = 0;
			if (direction.ordinal() >= entityAnimationData.numberOfDirections) {
				switch (entityAnimationData.numberOfDirections) {
				case 1:
					animationIndex = 0;
					break;
				case 4:
					animationIndex = direction.ordinal() % 4;
					break;
				}
			} else {
				animationIndex = direction.ordinal();
			}
			return animations[style][animationIndex].getKeyFrame(stateTime, entityAnimationData.looping);
		}
		
	}
	
}
