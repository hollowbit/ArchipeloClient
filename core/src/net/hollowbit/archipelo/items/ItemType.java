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
import net.hollowbit.archipelo.tools.LM;
import net.hollowbit.archipeloshared.Direction;
import net.hollowbit.archipeloshared.ItemTypeData;

public enum ItemType {
	
	BODY("body"),
	PANTS_BASIC("pants_basic"),
	BOOTS_BASIC("boots_basic"),
	SHIRT_BASIC("shirt_basic"),
	GLOVES_BASIC("gloves_basic"),
	SHOULDERPADS_BASIC("shoulderpads_basic"),
	HAIR1("hair1"),
	FACE1("face1")/*,
	SWORD("sword")*/;

	public static final int NO_EQUIP_TYPE = -1;
	public static final int EQUIP_INDEX_USABLE = 9;
	
	public static final float WALK_ANIMATION_LENGTH = 0.15f;
	public static final float ROLL_ANIMATION_LENGTH = 0.08f;
	public static final float SPRINT_ANIMATION_LENGTH = 0.11f;
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
	}
	
	private void loadImages (TextureRegion[][] iconMap) {
		this.icon = iconMap[iconY][iconX];
		
		//Load images depending on conditions
		if (equipType != NO_EQUIP_TYPE) {
			walkAnimation = new Animation[Direction.TOTAL][numOfStyles];
			sprintAnimation = new Animation[Direction.TOTAL][numOfStyles];
			rollAnimation = new Animation[Direction.TOTAL][numOfStyles];
			for (int style = 0; style < numOfStyles; style++) {
				TextureRegion[][] walkSheet = TextureRegion.split(new Texture("items/" + id + "/walk_" + style + ".png"), WEARABLE_SIZE, WEARABLE_SIZE);
				for (int direction = 0; direction < Direction.TOTAL; direction++) {
					walkAnimation[direction][style] = new Animation(WALK_ANIMATION_LENGTH, walkSheet[direction]);
					sprintAnimation[direction][style] = new Animation(SPRINT_ANIMATION_LENGTH, walkSheet[direction]);//Load sprint now since it is the same image as walk, just faster
				}
				
				TextureRegion[][] rollSheet = TextureRegion.split(new Texture("items/" + id + "/roll_" + style + ".png"), WEARABLE_SIZE, WEARABLE_SIZE);
				for (int direction = 0; direction < Direction.TOTAL; direction++) {
					rollAnimation[direction][style] = new Animation(ROLL_ANIMATION_LENGTH, rollSheet[direction]);
				}
			}
			
			useAnimation = new Animation[Direction.TOTAL][numOfStyles][numOfUseAnimations];
			for (int style = 0; style < numOfStyles; style++) {
				for (int useAnim = 0; useAnim < numOfUseAnimations; useAnim++) {
					TextureRegion[][] useSheet = TextureRegion.split(new Texture("items/" + id + "/use_" + style + "_" + useAnim + ".png"), WEARABLE_SIZE, WEARABLE_SIZE);
					for (int direction = 0; direction < Direction.TOTAL; direction++) {
						useAnimation[direction][style][useAnim] = new Animation(useAnimationLength, useSheet[direction]);
					}
				}
			}
		}
	}
	
	public TextureRegion getWalkFrame (Direction direction, float statetime) {
		return getWalkFrame(direction, statetime, 0);
	}
	
	public TextureRegion getWalkFrame (Direction direction, float statetime, int style) {
		return walkAnimation[direction.ordinal()][style].getKeyFrame(statetime, true);
	}
	
	public TextureRegion getSprintFrame (Direction direction, float statetime) {
		return getSprintFrame(direction, statetime, 0);
	}
	
	public TextureRegion getSprintFrame (Direction direction, float statetime, int style) {
		return sprintAnimation[direction.ordinal()][style].getKeyFrame(statetime, true);
	}
	
	public TextureRegion getRollFrame (Direction direction, float statetime) {
		return getRollFrame(direction, statetime, 0);
	}
	
	public TextureRegion getRollFrame (Direction direction, float statetime, int style) {
		return rollAnimation[direction.ordinal()][style].getKeyFrame(statetime, true);
	}
	
	public TextureRegion getUseFrame (Direction direction, float statetime, int useStyle) {
		return getUseFrame(direction, statetime, useStyle, 0);
	}
	
	public TextureRegion getUseFrame (Direction direction, float statetime, int useStyle, int style) {
		return useAnimation[direction.ordinal()][style][useStyle].getKeyFrame(statetime, false);
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
	
	public static void loadAllImages () {
		TextureRegion[][] iconMap = TextureRegion.split(new Texture("items/icons.png"), ArchipeloClient.TILE_SIZE, ArchipeloClient.TILE_SIZE);
		for (ItemType type : ItemType.values())
			type.loadImages(iconMap);
	}
	
	public static ItemType getItemTypeByItem (Item item) {
		return itemTypes.get(item.id);
	}
	
}
