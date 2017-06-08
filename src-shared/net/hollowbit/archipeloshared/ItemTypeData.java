package net.hollowbit.archipeloshared;

public class ItemTypeData {
	
	public int iconSize = 16;
	public int maxStackSize = 1;
	public int durability = 1;
	public int equipType = -1;
	public boolean buff = false;
	public boolean ammo = false;
	public boolean consumable = false;
	public boolean material = false;
	public int numOfStyles = 1;
	public String[][] sounds = new String[numOfStyles][0];
	public ItemUseAnimationData[] useAnimData = new ItemUseAnimationData[0];
	
	//Combat stats
	public int knockback = DEFAULT_KNOCKBACK;
	public int minDamage = DEFAULT_MIN_DAMAGE;
	public int maxDamage = DEFAULT_MAX_DAMAGE;
	public int defense = DEFAULT_DEFENSE;
	public float damageMultiplier = DEFAULT_DAMAGE_MULTIPLIER;
	public float defenseMultiplier = DEFAULT_DEFENSE_MULTIPLIER;
	public float speedMultiplier = DEFAULT_SPEED_MULTIPLIER;
	public float critMultiplier = DEFAULT_CRIT_MULTIPLIER;
	public int critChance = DEFAULT_CRIT_CHANCE;
	public int hitRange = 8;

	public static final int DEFAULT_KNOCKBACK = 0;
	public static final int DEFAULT_MIN_DAMAGE = 0;
	public static final int DEFAULT_MAX_DAMAGE = 0;
	public static final int DEFAULT_DEFENSE = 0;
	public static final float DEFAULT_DAMAGE_MULTIPLIER = 1;
	public static final float DEFAULT_DEFENSE_MULTIPLIER = 1;
	public static final float DEFAULT_SPEED_MULTIPLIER = 1;
	public static final float DEFAULT_CRIT_MULTIPLIER = 1f;
	public static final int DEFAULT_CRIT_CHANCE = 0;
	
}
