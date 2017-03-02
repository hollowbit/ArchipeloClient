package net.hollowbit.archipelo.items;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.entity.living.Player;
import net.hollowbit.archipelo.tools.AssetManager;
import net.hollowbit.archipelo.tools.LM;
import net.hollowbit.archipeloshared.Direction;
import net.hollowbit.archipeloshared.ItemTypeData;

public enum ItemType {
	
	BODY("body"),
	PANTS_BASIC("pants_basic"),
	BOOTS_BASIC("boots_basic"),
	SHIRT_BASIC("shirt_basic"),
	/*GLOVES_BASIC("gloves_basic"),
	SHOULDERPADS_BASIC("shoulderpads_basic"),*/
	HAIR1("hair1"),
	FACE1("face1"),
	BLOBBY_ASHES("blobby_ashes")/*,
	SWORD("sword")*/;

	public static final int NO_EQUIP_TYPE = -1;
	public static final int EQUIP_INDEX_USABLE = 9;
	
	public static final float WALK_ANIMATION_LENGTH = 0.2f;
	public static final float ROLL_ANIMATION_LENGTH = 0.08f;
	public static final float SPRINT_ANIMATION_LENGTH = 0.16f;
	public static final int WEARABLE_SIZE = 32;
	
	public String id;
	public int iconX, iconY;
	public int maxStackSize;
	public int durability;
	public int equipType;
	public boolean buff;
	public boolean ammo;
	public boolean consumable;
	public boolean material;
	public int numOfStyles;
	public int numOfUseAnimations;
	public float useAnimationLength;
	public boolean useThrust;
	public boolean renderUsingColor;
	
	public int minDamage;
	public int maxDamage;
	public int defense;
	public float damageMultiplier = 1;
	public float defenseMultiplier = 1;
	public float speedMultiplier = 1;
	public float critMultiplier;
	public int critChance;
	
	private TextureRegion icon;
	private Animation[][] walkAnimation = null;//Not null if wearable
	private Animation[][] sprintAnimation = null;//Not null if wearable
	private Animation[][] rollAnimation = null;//Not null if wearable
	private Animation[][][] useAnimation = null;//Not null if usable or wearable
	private Animation[][][] thrustAnimation = null;//Not null if usable or wearable
	
	@SuppressWarnings("unchecked")
	private ItemType (String id) {
		Json json = new Json();
		ItemTypeData data = null;
		try {
			data = json.fromJson(ClassReflection.forName("net.hollowbit.archipeloshared.ItemTypeData"), Gdx.files.internal("shared/items/" + id + ".json").readString());
		} catch (ReflectionException e) {
		}
		
		this.id = data.id;
		this.iconX = data.iconX;
		this.iconY = data.iconY;
		this.minDamage = data.minDamage;
		this.maxDamage = data.maxDamage;
		this.defense = data.defense;
		this.damageMultiplier = data.damageMultiplier;
		this.defenseMultiplier = data.defenseMultiplier;
		this.speedMultiplier = data.speedMultiplier;
		this.maxStackSize = data.maxStackSize;
		this.critMultiplier = data.critMultiplier;
		this.critChance = data.critChance;
		this.durability = data.durability;
		this.equipType = data.equipType;
		this.buff = data.buff;
		this.ammo = data.ammo;
		this.consumable = data.consumable;
		this.material = data.material;
		this.numOfStyles = data.numOfStyles;
		this.numOfUseAnimations = data.numOfUseAnimations;
		this.useAnimationLength = data.useAnimationLength;
		this.useThrust = data.useThrust;
		this.renderUsingColor = data.renderUsingColor;
	}
	
	private void loadImages (TextureRegion[][] iconMap) {
		this.icon = iconMap[iconY][iconX];
		
		//Load images depending on conditions
		if (equipType != NO_EQUIP_TYPE && equipType != EQUIP_INDEX_USABLE) {
			if (equipType != Player.EQUIP_INDEX_USABLE) {//Don't get animations for usable items
				walkAnimation = new Animation[Direction.TOTAL][numOfStyles];
				sprintAnimation = new Animation[Direction.TOTAL][numOfStyles];
				rollAnimation = new Animation[Direction.TOTAL][numOfStyles];
				for (int style = 0; style < numOfStyles; style++) {
					TextureRegion[][] walkSheet = AssetManager.fixBleedingSpriteSheet(TextureRegion.split(new Texture("items/" + id + "/walk_" + style + ".png"), WEARABLE_SIZE, WEARABLE_SIZE));
					for (int direction = 0; direction < Direction.TOTAL; direction++) {
						walkAnimation[direction][style] = new Animation(WALK_ANIMATION_LENGTH, walkSheet[direction]);
						sprintAnimation[direction][style] = new Animation(SPRINT_ANIMATION_LENGTH, walkSheet[direction]);//Load sprint now since it is the same image as walk, just faster
					}
					
					TextureRegion[][] rollSheet = AssetManager.fixBleedingSpriteSheet(TextureRegion.split(new Texture("items/" + id + "/roll_" + style + ".png"), WEARABLE_SIZE, WEARABLE_SIZE));
					for (int direction = 0; direction < Direction.TOTAL; direction++) {
						rollAnimation[direction][style] = new Animation(ROLL_ANIMATION_LENGTH, rollSheet[direction]);
					}
				}
			}
			
			useAnimation = new Animation[Direction.TOTAL][numOfStyles][numOfUseAnimations];
			thrustAnimation = new Animation[Direction.TOTAL][numOfStyles][numOfUseAnimations];
			for (int style = 0; style < numOfStyles; style++) {
				TextureRegion[][] useSheet = AssetManager.fixBleedingSpriteSheet(TextureRegion.split(new Texture("items/" + id + "/use_" + style + ".png"), WEARABLE_SIZE, WEARABLE_SIZE));
				for (int direction = 0; direction < Direction.TOTAL; direction++) {
					useAnimation[direction][style][0] = new Animation(useAnimationLength, useSheet[direction]);
				}
				
				TextureRegion[][] thrustSheet = AssetManager.fixBleedingSpriteSheet(TextureRegion.split(new Texture("items/" + id + "/thrust_" + style + ".png"), WEARABLE_SIZE, WEARABLE_SIZE));
				for (int direction = 0; direction < Direction.TOTAL; direction++) {
					thrustAnimation[direction][style][0] = new Animation(useAnimationLength, thrustSheet[direction]);
				}
			}
		} else if (equipType != NO_EQUIP_TYPE) {//Usable item
			useAnimation = new Animation[Direction.TOTAL][numOfStyles][numOfUseAnimations];
			thrustAnimation = new Animation[Direction.TOTAL][numOfStyles][numOfUseAnimations];
			for (int style = 0; style < numOfStyles; style++) {
				for (int useAnim = 0; useAnim < numOfUseAnimations; useAnim++) {
					if (!useThrust) {//If it doesn't use thrust, don't load thrust animation
						TextureRegion[][] useSheet = AssetManager.fixBleedingSpriteSheet(TextureRegion.split(new Texture("items/" + id + "/use_" + style + "_" + useAnim + ".png"), WEARABLE_SIZE, WEARABLE_SIZE));
						for (int direction = 0; direction < Direction.TOTAL; direction++) {
							useAnimation[direction][style][useAnim] = new Animation(useAnimationLength, useSheet[direction]);
						}
					} else {//If it doesn't use "use", then oly load thrust animation
						TextureRegion[][] thrustSheet = AssetManager.fixBleedingSpriteSheet(TextureRegion.split(new Texture("items/" + id + "/thrust_" + style + "_" + useAnim + ".png"), WEARABLE_SIZE, WEARABLE_SIZE));
						for (int direction = 0; direction < Direction.TOTAL; direction++) {
							thrustAnimation[direction][style][useAnim] = new Animation(useAnimationLength, thrustSheet[direction]);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Gets animation frame for a usable item on use animations
	 * @param animationId
	 * @param direction
	 * @param statetime
	 * @param style
	 * @param useStyle
	 * @return
	 */
	public TextureRegion getAnimationFrameForUsable (String animationId, Direction direction, float statetime, int style, int useStyle, float totalRuntime) {
		if (animationId.equals("use"))
			return getUseFrame(direction, 0, useStyle, style, totalRuntime);
		else if (animationId.equals("usewalk"))
			return getUseFrame(direction, statetime, useStyle, style, totalRuntime);
		else if (animationId.equals("thrust"))
			return getThrustFrame(direction, statetime, useStyle, style, totalRuntime);
		else
			return null;
	}
	
	/**
	 * Get animation frames for items
	 * @param animationId
	 * @param direction
	 * @param statetime
	 * @param style
	 * @return
	 */
	public TextureRegion getAnimationFrame (String animationId, Direction direction, float statetime, int style, float totalRuntime) {
		if (animationId.equals("default"))
			return getWalkFrame(direction, 0, style, totalRuntime);//Uses 0 statetime to get first frame
		else if (animationId.equals("walk"))
			return getWalkFrame(direction, statetime, style, totalRuntime);
		else if (animationId.equals("sprint"))
			return getSprintFrame(direction, statetime, style, totalRuntime);
		else if (animationId.equals("roll"))
			return getRollFrame(direction, statetime, style, totalRuntime);
		else if (animationId.equals("use"))
			return getUseFrame(direction, 0, 0, style, totalRuntime);
		else if (animationId.equals("usewalk"))
			return getUseFrame(direction, statetime, 0, style, totalRuntime);
		else if (animationId.equals("thrust"))
			return getThrustFrame(direction, statetime, 0, style, totalRuntime);
		else
			return null;
	}
	
	public TextureRegion getWalkFrame (Direction direction, float statetime, int style, float totalRuntime) {
		Animation animation = walkAnimation[direction.ordinal()][style];
		animation.setFrameDuration(totalRuntime / animation.getKeyFrames().length);
		return animation.getKeyFrame(statetime, true);
	}
	
	public TextureRegion getSprintFrame (Direction direction, float statetime, int style, float totalRuntime) {
		Animation animation = sprintAnimation[direction.ordinal()][style];
		animation.setFrameDuration(totalRuntime / animation.getKeyFrames().length);
		return animation.getKeyFrame(statetime, true);
	}
	
	public TextureRegion getRollFrame (Direction direction, float statetime, int style, float totalRuntime) {
		Animation animation = rollAnimation[direction.ordinal()][style];
		animation.setFrameDuration(totalRuntime / animation.getKeyFrames().length);
		return animation.getKeyFrame(statetime, true);
	}
	
	public TextureRegion getUseFrame (Direction direction, float statetime, int useStyle, int style, float totalRuntime) {
		Animation animation = useAnimation[direction.ordinal()][style][useStyle];
		animation.setFrameDuration(totalRuntime / animation.getKeyFrames().length);
		return animation.getKeyFrame(statetime, true);
	}
	
	public TextureRegion getThrustFrame (Direction direction, float statetime, int useStyle, int style, float totalRuntime) {
		Animation animation = thrustAnimation[direction.ordinal()][style][useStyle];
		animation.setFrameDuration(totalRuntime / animation.getKeyFrames().length);
		return animation.getKeyFrame(statetime, true);
	}
	
	public TextureRegion getIcon () {
		return icon;
	}
	
	@Override
	public String toString() {
		return id;
	}
	
	public String getDisplayName () {
		return LM.items(id + "Name");
	}
	
	public String getDescription () {
		return LM.items(id + "Desc");
	}
	
	private static HashMap<String, ItemType> itemTypes;
	static {
		itemTypes = new HashMap<String, ItemType>();
		
		for (ItemType type : ItemType.values())
			itemTypes.put(type.id, type);
	}
	
	/**
	 * Loads all icon images
	 */
	public static void loadAllImages () {
		ArchipeloClient.getGame().getAssetManager().putTextureMap("item-icons", "items/icons.png", ArchipeloClient.TILE_SIZE, ArchipeloClient.TILE_SIZE);
		TextureRegion[][] iconMap = ArchipeloClient.getGame().getAssetManager().getTextureMap("item-icons");
		for (ItemType type : ItemType.values())
			type.loadImages(iconMap);
	}
	
	public static ItemType getItemTypeByItem (Item item) {
		return itemTypes.get(item.id);
	}
	
	public static ItemType getItemTypeById (String id) {
		return itemTypes.get(id);
	}
	
}
