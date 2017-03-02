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

import net.hollowbit.archipelo.entity.lifeless.BlobbyGrave;
import net.hollowbit.archipelo.entity.lifeless.Computer;
import net.hollowbit.archipelo.entity.lifeless.Door;
import net.hollowbit.archipelo.entity.lifeless.DoorLocked;
import net.hollowbit.archipelo.entity.lifeless.Sign;
import net.hollowbit.archipelo.entity.lifeless.Teleporter;
import net.hollowbit.archipelo.entity.living.Player;
import net.hollowbit.archipelo.tools.AssetManager;
import net.hollowbit.archipelo.world.Map;
import net.hollowbit.archipeloshared.CollisionRect;
import net.hollowbit.archipeloshared.Direction;
import net.hollowbit.archipeloshared.EntityAnimationData;
import net.hollowbit.archipeloshared.EntityTypeData;

@SuppressWarnings("rawtypes")
public enum EntityType {
	
	PLAYER ("player", Player.class),
	TELEPORTER ("teleporter", Teleporter.class),
	DOOR ("door", Door.class),
	DOOR_LOCKED ("door-locked", DoorLocked.class),
	SIGN ("sign", Sign.class),
	BLOBBY_GRAVE ("blobby_grave", BlobbyGrave.class),
	COMPUTER ("computer", Computer.class);
	
	private String id;
	private Class entityClass;
	private HashMap<String, EntityAnimation> animations;
	private int numberOfStyles;
	private boolean hittable;
	private float speed;
	
	private float drawOrderOffsetY;
	
	private int imgWidth;
	private int imgHeight;
	
	private String defaultAnimationId;
	
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
		this.speed = data.speed;
		
		this.drawOrderOffsetY = data.drawOrderOffsetY;
		
		this.imgWidth = data.imgWidth;
		this.imgHeight = data.imgHeight;
		
		this.viewRect = new CollisionRect(0, 0, data.viewRectOffsetX, data.viewRectOffsetY, data.viewRectWidth, data.viewRectHeight);
		
		this.collRects = new CollisionRect[data.collisionRects.length];
		for (int i = 0; i < collRects.length; i++) {
			this.collRects[i] = new CollisionRect(data.collisionRects[i]);
		}
		
		//Load animations
		animations = new HashMap<String, EntityAnimation>();
		boolean first = true;
		for (EntityAnimationData animationData : data.animations) {
			animations.put(animationData.id, new EntityAnimation(animationData));
			
			if (first) {
				defaultAnimationId = animationData.id;
				first = false;
			}
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
	
	public float getSpeed () {
		return speed;
	}
	
	public float getDrawOrderY (float y) {
		return y + drawOrderOffsetY;
	}
	
	public float getDrawOrderOffsetY() {
		return drawOrderOffsetY;
	}
	
	public TextureRegion getAnimationFrame (String id, Direction direction, float stateTime, int style) {
		return animations.get(id).getAnimationFrame(direction, stateTime, style);
	}
	
	public EntityAnimation getEntityAnimation (String id) {
		return animations.get(id);
	}
	
	public String getDefaultAnimationId() {
		return defaultAnimationId;
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
	
	public boolean hasAnimation(String animationId) {
		return animations.containsKey(animationId);
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
		return entityTypeMap.get(fullSnapshot.type).getNewEntityOfType(fullSnapshot, map);
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
	
	public class EntityAnimation {
		
		Animation[][] animations;
		EntityAnimationData entityAnimationData;
		Texture[] srcTextures;
		boolean loaded;
		
		public EntityAnimation (EntityAnimationData entityAnimationData) {
			this.entityAnimationData = entityAnimationData;
			loaded = false;
		}
		
		/**
		 * Loads images of this animation
		 * @param entityId
		 * @param numberOfStyles
		 * @param imgWidth
		 * @param imgHeight
		 */
		public void loadImages (String entityId, int numberOfStyles, int imgWidth, int imgHeight) {
			if (!loaded) {
				loaded = true;
				animations = new Animation[numberOfStyles][entityAnimationData.numberOfDirections];
				srcTextures = new Texture[numberOfStyles];
				for (int i = 0; i < numberOfStyles; i++) {
					String fileName = entityAnimationData.fileName.equals("") ? entityAnimationData.id : entityAnimationData.fileName;
					srcTextures[i] = new Texture("entities/" + entityId + "/" + fileName + "_" + i + ".png");
					TextureRegion[][] animationRegion = AssetManager.fixBleedingSpriteSheet(TextureRegion.split(srcTextures[i], imgWidth, imgHeight));
					for (int u = 0; u < entityAnimationData.numberOfDirections; u++) {
						animations[i][u] = new Animation(entityAnimationData.totalRuntime / animationRegion[u].length, animationRegion[u]);
					}
				}
			}
		}
		
		/**
		 * Disposes of textures for this animation
		 */
		public void unloadImages () {
			if (loaded) {
				loaded = false;
				for (Texture texture : srcTextures) {
					texture.dispose();
				}
			}
		}
		
		/**
		 * Get frame for this animation
		 * @param direction
		 * @param stateTime
		 * @param style
		 * @return
		 */
		public TextureRegion getAnimationFrame (Direction direction, float stateTime, int style) {
			if (entityAnimationData.totalRuntime <= 0)//Special rule to make animation with no runtime only render first frame
				stateTime = 0;
			
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
		
		public float getTotalRuntime () {
			return entityAnimationData.totalRuntime;
		}
		
		public EntityAnimationData getData () {
			return entityAnimationData;
		}
		
	}
	
}
