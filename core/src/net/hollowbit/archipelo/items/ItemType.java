package net.hollowbit.archipelo.items;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.items.ItemUseAnimation.IllegalItemUseAnimationDataException;
import net.hollowbit.archipelo.items.usetypes.BasicWeaponUseType;
import net.hollowbit.archipelo.tools.AssetManager;
import net.hollowbit.archipelo.tools.LM;
import net.hollowbit.archipeloshared.Direction;
import net.hollowbit.archipeloshared.ItemTypeData;
import net.hollowbit.archipeloshared.ItemUseAnimationData;

public enum ItemType {
	
	GOLDEN_BAND,
	SILVER_BAND,
	RUBY_RING,
	SAPPHIRE_RING,
	EMERALD_RING,
	RING_OF_NATURE,
	RING_OF_WATER,
	RING_OF_HOPE,
	STONE_OF_MANA,
	STONE_OF_LIFE,
	STONE_OF_TREASURE,
	BODY,
	PANTS_BASIC,
	BOOTS_BASIC,
	SHIRT_BASIC,
	/*GLOVES_BASIC,
	SHOULDERPADS_BASIC,*/
	HAIR1,
	FACE1,
	BLOBBY_ASHES,
	SPEAR_BASIC(new BasicWeaponUseType()),
	ASSISTANT_GENERAL(new BasicWeaponUseType()),
	SOUL_DISRUPTOR(new BasicWeaponUseType()),
	SPIRIT_DISRUPTOR(new BasicWeaponUseType()),
	DEMONS_TONGUE(new BasicWeaponUseType()),
	FANN_KATANA(new BasicWeaponUseType()),
	HYLIAN_BROADSWORD(new BasicWeaponUseType()),
	LITTLE_RED(new BasicWeaponUseType()),
	FALLEN_ANGEL_BLADE(new BasicWeaponUseType()),
	TRAINING_SWORD(new BasicWeaponUseType()),
	ANGEL_BLADE(new BasicWeaponUseType()),
	ANGELIC_CLAYMORE(new BasicWeaponUseType()),
	BEAST_FURY(new BasicWeaponUseType()),
	DESERT_RAPIER(new BasicWeaponUseType()),
	DRAGONS_BREATH(new BasicWeaponUseType()),
	IRON_BULDGE(new BasicWeaponUseType()),
	IRON_HATCHET(new BasicWeaponUseType()),
	IRON_SCEPTER(new BasicWeaponUseType()),
	MIGHTY_HAMMER(new BasicWeaponUseType()),
	THE_FAIRY_MAN(new BasicWeaponUseType()),
	WORLD_BREAKER(new BasicWeaponUseType()),
	TIN_ORE,
	COPPER_ORE,
	IRON_ORE,
	VANIUM_ORE,
	AMBITE_ORE,
	SPELLSTONE_ORE,
	ELEMENTIUM_ORE,
	GOLD_ORE,
	BLOODSTONE_ORE,
	RADIUM_ORE,
	TORMENTIUM_ORE,
	SUNSTONE_ORE,
	MOONSTONE_ORE,
	TIN_BAR,
	COPPER_BAR,
	IRON_BAR,
	VANIUM_BAR,
	AMBITE_BAR,
	SPELLSTONE_BAR,
	ELEMENTIUM_BAR,
	GOLD_BAR,
	BLOODSTONE_BAR,
	RADIUM_BAR,
	TORMENTIUM_BAR,
	ECLIPSE_BAR,
	OAKWOOD_LOG,
	DARK_OAKWOOD_LOG,
	BALSAM_LOG,
	MAGEWOOD_LOG,
	REDWOOD_LOG,
	FAEWOOD_LOG,
	WRAITHWOOD_LOG,
	OAKWOOD_PLANKS,
	DARK_OAKWOOD_PLANKS,
	BALSAM_PLANKS,
	MAGEWOOD_PLANKS,
	REDWOOD_PLANKS,
	FAEWOOD_PLANKS,
	WRAITHWOOD_PLANKS,
	BROWN_FUR,
	MAROON_FUR,
	GREEN_FUR,
	GREY_FUR,
	UNKNOWN_FUR,
	DARK_FUR,
	UNIQUE_FUR,
	SMALL_HEALTH_POTION,
	SMALL_MANA_POTION,
	HEALTH_POTION,
	MANA_POTION,
	BLOCK_OF_ICE,
	CHUNK_OF_METEOR,
	PIECE_OF_METEOR,
		
	;
	public static final int NO_EQUIP_TYPE = -1;
	public static final int EQUIP_INDEX_USABLE = 9;
	
	public static final float WALK_ANIMATION_LENGTH = 0.2f;
	public static final float ROLL_ANIMATION_LENGTH = 0.08f;
	public static final float SPRINT_ANIMATION_LENGTH = 0.16f;
	
	public static TextureRegion invalidIconTexture = null;;
	
	public String id;
	public int iconSize;
	public int maxStackSize;
	public int durability;
	public int equipType;
	public boolean buff;
	public boolean ammo;
	public boolean consumable;
	public boolean material;
	public int numOfStyles;
	public String[][] sounds;
	public ItemUseAnimationData[] useableAnimationData;

	public int knockback;
	public int minDamage;
	public int maxDamage;
	public int defense;
	public float damageMultiplier = 1;
	public float defenseMultiplier = 1;
	public float speedMultiplier = 1;
	public float critMultiplier;
	public int critChance;
	
	private TextureRegion[] icon;
	private Animation[][] walkAnimation = null;//Not null if wearable
	private Animation[][] sprintAnimation = null;//Not null if wearable
	private Animation[][] rollAnimation = null;//Not null if wearable
	private Animation[][] useAnimation = null;//Not null if usable or wearable
	private Animation[][] thrustAnimation = null;//Not null if usable or wearable
	
	private ItemUseAnimation[] usableItemAnimations = null;//Not null if usable
	
	private UseType useType;
	
	private ItemType () {
		this(null);
	}

	@SuppressWarnings("unchecked")
	private ItemType (UseType useType) {
		this.id = this.name().toLowerCase();
		Json json = new Json();
		ItemTypeData data = null;
		try {
			data = json.fromJson(ClassReflection.forName("net.hollowbit.archipeloshared.ItemTypeData"), Gdx.files.internal("shared/items/" + id + ".json").readString());
		} catch (ReflectionException e) {
		}
		
		this.iconSize = data.iconSize;
		this.knockback = data.knockback;
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
		this.sounds = data.sounds;
		this.useableAnimationData = data.useAnimData;
		
		if (equipType == EQUIP_INDEX_USABLE)
			this.useType = useType;
		else
			useType = null;
	}
	
	public String getSoundById(int style, int id) {
		return sounds[style % numOfStyles][id % sounds[0].length];
	}
	
	private void loadSounds() {
		for (int i = 0; i < numOfStyles; i++) {
			for (int u = 0; u < sounds[0].length; u++)
				ArchipeloClient.getGame().getSoundManager().loadSound(sounds[i][u]);
		}
	}
	
	private void loadImages () {
		FileHandle iconFile = Gdx.files.internal("items/" + id + "/icon.png");
		if (iconFile.exists()) {
			Texture texture = new Texture(iconFile);
			this.icon = TextureRegion.split(texture, iconSize, iconSize)[0]; 
		}
		
		if (invalidIconTexture == null)
			invalidIconTexture = new TextureRegion(ArchipeloClient.getGame().getAssetManager().getTexture("invalid"));
		
		//Load images depending on conditions
		if (equipType != NO_EQUIP_TYPE && equipType != EQUIP_INDEX_USABLE) {
			walkAnimation = new Animation[Direction.TOTAL][numOfStyles];
			sprintAnimation = new Animation[Direction.TOTAL][numOfStyles];
			rollAnimation = new Animation[Direction.TOTAL][numOfStyles];
			for (int style = 0; style < numOfStyles; style++) {
				TextureRegion[][] walkSheet = AssetManager.fixBleedingSpriteSheet(TextureRegion.split(new Texture("items/" + id + "/walk_" + style + ".png"), ArchipeloClient.PLAYER_SIZE, ArchipeloClient.PLAYER_SIZE));
				for (int direction = 0; direction < Direction.TOTAL; direction++) {
					walkAnimation[direction][style] = new Animation(WALK_ANIMATION_LENGTH, walkSheet[direction]);
					sprintAnimation[direction][style] = new Animation(SPRINT_ANIMATION_LENGTH, walkSheet[direction]);//Load sprint now since it is the same image as walk, just faster
				}
				
				TextureRegion[][] rollSheet = AssetManager.fixBleedingSpriteSheet(TextureRegion.split(new Texture("items/" + id + "/roll_" + style + ".png"), ArchipeloClient.PLAYER_SIZE, ArchipeloClient.PLAYER_SIZE));
				for (int direction = 0; direction < Direction.TOTAL; direction++) {
					rollAnimation[direction][style] = new Animation(ROLL_ANIMATION_LENGTH, rollSheet[direction]);
				}
			}
			
			useAnimation = new Animation[Direction.TOTAL][numOfStyles];
			thrustAnimation = new Animation[Direction.TOTAL][numOfStyles];
			for (int style = 0; style < numOfStyles; style++) {
				TextureRegion[][] useSheet = AssetManager.fixBleedingSpriteSheet(TextureRegion.split(new Texture("items/" + id + "/use_" + style + ".png"), ArchipeloClient.PLAYER_SIZE, ArchipeloClient.PLAYER_SIZE));
				for (int direction = 0; direction < Direction.TOTAL; direction++) {
					useAnimation[direction][style] = new Animation(0.1f, useSheet[direction]);
				}
				
				TextureRegion[][] thrustSheet = AssetManager.fixBleedingSpriteSheet(TextureRegion.split(new Texture("items/" + id + "/thrust_" + style + ".png"), ArchipeloClient.PLAYER_SIZE, ArchipeloClient.PLAYER_SIZE));
				for (int direction = 0; direction < Direction.TOTAL; direction++) {
					thrustAnimation[direction][style] = new Animation(0.1f, thrustSheet[direction]);
				}
			}
		} else if (equipType != NO_EQUIP_TYPE) {//Usable item
			usableItemAnimations = new ItemUseAnimation[useableAnimationData.length];
			for (int i = 0; i < usableItemAnimations.length; i++) {
				try {
					usableItemAnimations[i] = new ItemUseAnimation(this, i, useableAnimationData[i]);
				} catch (IllegalItemUseAnimationDataException e) {
					System.out.println(e.getMessage());
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
	public TextureRegion getAnimationFrameForUsable (Direction direction, float statetime, int style, int useStyle) {
		return usableItemAnimations[useStyle % getNumOfUseAnimations()].getFrame(statetime, direction, style);
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
			return getUseFrame(direction, 0, style, totalRuntime);
		else if (animationId.equals("usewalk"))
			return getUseFrame(direction, statetime, style, totalRuntime);
		else if (animationId.equals("thrust"))
			return getThrustFrame(direction, statetime, style, totalRuntime);
		else
			return null;
	}
	
	public TextureRegion getWalkFrame (Direction direction, float statetime, int style, float totalRuntime) {
		Animation animation = walkAnimation[direction.ordinal()][style % numOfStyles];
		animation.setFrameDuration(totalRuntime / animation.getKeyFrames().length);
		return animation.getKeyFrame(statetime, true);
	}
	
	public TextureRegion getSprintFrame (Direction direction, float statetime, int style, float totalRuntime) {
		Animation animation = sprintAnimation[direction.ordinal()][style % numOfStyles];
		animation.setFrameDuration(totalRuntime / animation.getKeyFrames().length);
		return animation.getKeyFrame(statetime, true);
	}
	
	public TextureRegion getRollFrame (Direction direction, float statetime, int style, float totalRuntime) {
		Animation animation = rollAnimation[direction.ordinal()][style % numOfStyles];
		animation.setFrameDuration(totalRuntime / animation.getKeyFrames().length);
		return animation.getKeyFrame(statetime, true);
	}
	
	public TextureRegion getUseFrame (Direction direction, float statetime, int style, float totalRuntime) {
		Animation animation = useAnimation[direction.ordinal()][style % numOfStyles];
		animation.setFrameDuration(totalRuntime / animation.getKeyFrames().length);
		return animation.getKeyFrame(statetime, true);
	}
	
	public TextureRegion getThrustFrame (Direction direction, float statetime, int style, float totalRuntime) {
		Animation animation = thrustAnimation[direction.ordinal()][style % numOfStyles];
		animation.setFrameDuration(totalRuntime / animation.getKeyFrames().length);
		return animation.getKeyFrame(statetime, true);
	}
	
	public TextureRegion getIcon (int style) {
		if (icon == null)
			return invalidIconTexture;
		else
			return icon[style % numOfStyles];
	}
	
	public static TextureRegion getInvalidIcon () {
		return invalidIconTexture;
	}
	
	@Override
	public String toString() {
		return id;
	}
	
	public String getDisplayName () {
		return LM.itemNames(id);
	}
	
	public String getDescription () {
		return LM.itemDescs(id);
	}
	
	public UseType getUseType () {
		return useType;
	}
	
	public int getNumOfUseAnimations() {
		return usableItemAnimations.length;
	}
	
	public float getUseAnimationLength(int useType) {
		return usableItemAnimations[useType % getNumOfUseAnimations()].getTotalRuntime();
	}
	
	public ItemUseAnimation getAnimationFromUseType(int useType) {
		return usableItemAnimations[useType % getNumOfUseAnimations()];
	}
	
	private static HashMap<String, ItemType> itemTypes;
	static {
		itemTypes = new HashMap<String, ItemType>();
		
		for (ItemType type : ItemType.values())
			itemTypes.put(type.id, type);
	}
	
	/**
	 * Loads all icon images and sounds
	 */
	public static void loadAllAssets () {
		for (ItemType type : ItemType.values()) {
			type.loadImages();
			type.loadSounds();
		}
	}
	
	public static ItemType getItemTypeByItem (Item item) {
		return itemTypes.get(item.id);
	}
	
	public static ItemType getItemTypeById (String id) {
		return itemTypes.get(id);
	}
	
}
