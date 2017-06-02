package net.hollowbit.archipelo.entity.living;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;

import net.hollowbit.archipelo.entity.EntityType;
import net.hollowbit.archipelo.entity.LivingEntity;
import net.hollowbit.archipelo.entity.components.ClothesRenderEntityComponent;
import net.hollowbit.archipelo.items.Item;
import net.hollowbit.archipelo.items.ItemType;
import net.hollowbit.archipelo.tools.StaticTools;
import net.hollowbit.archipelo.world.Map;
import net.hollowbit.archipeloshared.Direction;
import net.hollowbit.archipeloshared.EntitySnapshot;

public class Player extends LivingEntity {
	
	public static final float ROLLING_SPEED_SCALE = 3.0f;
	public static final float SPRINTING_SPEED_SCALE = 1.4f;
	public static final float ROLL_DOUBLE_CLICK_DURATION = 0.21f;
	
	//Equipped Inventory Index
	public static final int EQUIP_SIZE = 10;
	public static final int EQUIP_INDEX_BODY = 0;
	public static final int EQUIP_INDEX_BOOTS = 1;
	public static final int EQUIP_INDEX_PANTS = 2;
	public static final int EQUIP_INDEX_SHIRT = 3;
	public static final int EQUIP_INDEX_GLOVES = 4;
	public static final int EQUIP_INDEX_SHOULDERPADS = 5;
	public static final int EQUIP_INDEX_FACE = 6;
	public static final int EQUIP_INDEX_HAIR = 7;
	public static final int EQUIP_INDEX_HAT = 8;
	public static final int EQUIP_INDEX_USABLE = 9;
	
	ClothesRenderEntityComponent clothesRenderer;
	
	@Override
	public void create (EntitySnapshot fullSnapshot, Map map, EntityType entityType) {
		super.create(fullSnapshot, map, entityType);
		this.clothesRenderer = new ClothesRenderEntityComponent(this, StaticTools.getJson().fromJson(Item[].class, fullSnapshot.getString("displayInventory", "")));
		components.add(clothesRenderer);
	}
	
	@Override
	public boolean isPlayer () {
		return true;
	}
	
	public Item[] getDisplayInventory () {
		return clothesRenderer.getDisplayInventory();
	}
	
	/**
	 * Static method to draw player from any screen, not just gamescreen
	 * @param batch
	 * @param direction
	 * @param isMoving
	 * @param isRolling
	 * @param x
	 * @param y
	 * @param movingStateTime
	 * @param rollingStateTime
	 * @param equippedInventory
	 * @param isSprinting
	 * @param width
	 * @param height
	 */
	public static void drawPlayer (Batch batch, Direction direction, boolean isMoving, boolean isRolling, float xFloat, float yFloat, float movingStateTime, float rollingStateTime, Item[] equippedInventory, boolean isSprinting, float width, float height) {
		int x = (int) xFloat;
		int y = (int) yFloat;
		
		if (isMoving) {
			if (isRolling) {
				batch.draw(EntityType.PLAYER.getAnimationFrame("roll", direction, rollingStateTime, 0), x, y, width, height);
			} else {
				if (isSprinting) {
					batch.draw(EntityType.PLAYER.getAnimationFrame("sprint", direction, movingStateTime, 0), x, y, width, height);
				} else {
					batch.draw(EntityType.PLAYER.getAnimationFrame("walk", direction, movingStateTime, 0), x, y, width, height);
				}
			}
		} else {
			batch.draw(EntityType.PLAYER.getAnimationFrame("default", direction, 0, 0), x, y);
		}
		
		if (isMoving) {
			if (isRolling) {
				float animationRuntime = EntityType.PLAYER.getEntityAnimation("roll").getTotalRuntime();
				for (int i = 0; i < EQUIP_SIZE - 1; i++) {//Loop through each part of equipable
					if (equippedInventory[i] == null)
						continue;
					batch.setColor(new Color(equippedInventory[i].color));
					if (i == EQUIP_INDEX_HAIR) {
						//If a hat is equipped, use the hair texture for when wearing hats
						if (equippedInventory[EQUIP_INDEX_HAT] == null)
							batch.draw(ItemType.getItemTypeByItem(equippedInventory[i]).getRollFrame(direction, rollingStateTime, 0, animationRuntime), x, y, width, height);
					} else if (i == EQUIP_INDEX_FACE) {
						batch.setColor(1, 1, 1, 1);
						batch.draw(ItemType.getItemTypeByItem(equippedInventory[i]).getRollFrame(direction, rollingStateTime, 0, animationRuntime), x, y, width, height);//Draw mouth
						batch.setColor(new Color(equippedInventory[i].color));
						batch.draw(ItemType.getItemTypeByItem(equippedInventory[i]).getRollFrame(direction, rollingStateTime, 1, animationRuntime), x, y, width, height);//Draw eye's iris with color
						batch.setColor(new Color(equippedInventory[EQUIP_INDEX_HAIR].color));
						batch.draw(ItemType.getItemTypeByItem(equippedInventory[i]).getRollFrame(direction, rollingStateTime, 2, animationRuntime), x, y, width, height);//Draw eyebrows with hair color
						batch.setColor(1, 1, 1, 1);
					} else
						batch.draw(ItemType.getItemTypeByItem(equippedInventory[i]).getRollFrame(direction, rollingStateTime, equippedInventory[i].style, animationRuntime), x, y, width, height);
					batch.setColor(1, 1, 1, 1);
				}
			} else {
				if (isSprinting) {
					float animationRuntime = EntityType.PLAYER.getEntityAnimation("sprint").getTotalRuntime();
					for (int i = 0; i < EQUIP_SIZE - 1; i++) {//Loop through each part of equipable
						if (equippedInventory[i] == null)
							continue;
						batch.setColor(new Color(equippedInventory[i].color));
						if (i == EQUIP_INDEX_HAIR) {
							//If a hat is equipped, use the hair texture for when wearing hats
							if (equippedInventory[EQUIP_INDEX_HAT] == null)
								batch.draw(ItemType.getItemTypeByItem(equippedInventory[i]).getSprintFrame(direction, movingStateTime, 0, animationRuntime), x, y, width, height);
						} else if (i == EQUIP_INDEX_FACE) {
							batch.setColor(1, 1, 1, 1);
							batch.draw(ItemType.getItemTypeByItem(equippedInventory[i]).getSprintFrame(direction, movingStateTime, 0, animationRuntime), x, y, width, height);//Draw mouth
							batch.setColor(new Color(equippedInventory[i].color));
							batch.draw(ItemType.getItemTypeByItem(equippedInventory[i]).getSprintFrame(direction, movingStateTime, 1, animationRuntime), x, y, width, height);//Draw eye's iris with color
							batch.setColor(new Color(equippedInventory[EQUIP_INDEX_HAIR].color));
							batch.draw(ItemType.getItemTypeByItem(equippedInventory[i]).getSprintFrame(direction, movingStateTime, 2, animationRuntime), x, y, width, height);//Draw eyebrows with hair color
							batch.setColor(1, 1, 1, 1);
						} else
							batch.draw(ItemType.getItemTypeByItem(equippedInventory[i]).getSprintFrame(direction, movingStateTime, equippedInventory[i].style, animationRuntime), x, y, width, height);
						batch.setColor(1, 1, 1, 1);
					}
				 } else {
					float animationRuntime = EntityType.PLAYER.getEntityAnimation("walk").getTotalRuntime();
					for (int i = 0; i < EQUIP_SIZE - 1; i++) {//Loop through each part of equipable
						if (equippedInventory[i] == null)
							continue;
						batch.setColor(new Color(equippedInventory[i].color));
						if (i == EQUIP_INDEX_HAIR) {
							//Only draw hair if no hat/helmet is equipped
							if (equippedInventory[EQUIP_INDEX_HAT] == null)
								batch.draw(ItemType.getItemTypeByItem(equippedInventory[i]).getWalkFrame(direction, movingStateTime, 0, animationRuntime), x, y, width, height);
						} else if (i == EQUIP_INDEX_FACE) {
							batch.setColor(1, 1, 1, 1);
							batch.draw(ItemType.getItemTypeByItem(equippedInventory[i]).getWalkFrame(direction, movingStateTime, 0, animationRuntime), x, y, width, height);//Draw mouth
							batch.setColor(new Color(equippedInventory[i].color));
							batch.draw(ItemType.getItemTypeByItem(equippedInventory[i]).getWalkFrame(direction, movingStateTime, 1, animationRuntime), x, y, width, height);//Draw eye's iris with color
							batch.setColor(new Color(equippedInventory[EQUIP_INDEX_HAIR].color));
							batch.draw(ItemType.getItemTypeByItem(equippedInventory[i]).getWalkFrame(direction, movingStateTime, 2, animationRuntime), x, y, width, height);//Draw eyebrows with hair color
							batch.setColor(1, 1, 1, 1);
						} else
							batch.draw(ItemType.getItemTypeByItem(equippedInventory[i]).getWalkFrame(direction, movingStateTime, equippedInventory[i].style, animationRuntime), x, y, width, height);
						batch.setColor(1, 1, 1, 1);
					}
				 }
			}
		} else {
			float animationRuntime = EntityType.PLAYER.getEntityAnimation("default").getTotalRuntime();
			for (int i = 0; i < EQUIP_SIZE - 1; i++) {//Loop through each part of equipable
				if (equippedInventory[i] == null)
					continue;
				batch.setColor(new Color(equippedInventory[i].color));
				if (i == EQUIP_INDEX_HAIR) {
					//If a hat is equipped, use the hair texture for when wearing hats
					if (equippedInventory[EQUIP_INDEX_HAT] == null)
						batch.draw(ItemType.getItemTypeByItem(equippedInventory[i]).getWalkFrame(direction, 0, 0, animationRuntime), x, y, width, height);
				} else if (i == EQUIP_INDEX_FACE) {
					batch.setColor(1, 1, 1, 1);
					batch.draw(ItemType.getItemTypeByItem(equippedInventory[i]).getWalkFrame(direction, 0, 0, animationRuntime), x, y, width, height);//Draw mouth
					batch.setColor(new Color(equippedInventory[i].color));
					batch.draw(ItemType.getItemTypeByItem(equippedInventory[i]).getWalkFrame(direction, 0, 1, animationRuntime), x, y, width, height);//Draw eye's iris with color
					batch.setColor(new Color(equippedInventory[EQUIP_INDEX_HAIR].color));
					batch.draw(ItemType.getItemTypeByItem(equippedInventory[i]).getWalkFrame(direction, 0, 2, animationRuntime), x, y, width, height);//Draw eyebrows with hair color
					batch.setColor(1, 1, 1, 1);
				} else
					batch.draw(ItemType.getItemTypeByItem(equippedInventory[i]).getWalkFrame(direction, 0, equippedInventory[i].style, animationRuntime), x, y, width, height);
				batch.setColor(1, 1, 1, 1);
			}
		}
		batch.setColor(1, 1, 1, 1);
	}
	
	/**
	 * Creates equipment inventory with given items
	 * @return
	 */
	public static Item[] createEquipInventory (Item body, Item boots, Item pants, Item shirt, Item gloves, Item shoulderpads, Item face, Item hair, Item hat, Item usable) {
		Item[] equipInventory = new Item[EQUIP_SIZE];
		equipInventory[EQUIP_INDEX_BODY] = body;
		equipInventory[EQUIP_INDEX_BOOTS] = boots;
		equipInventory[EQUIP_INDEX_PANTS] = pants;
		equipInventory[EQUIP_INDEX_SHIRT] = shirt;
		equipInventory[EQUIP_INDEX_GLOVES] = gloves;
		equipInventory[EQUIP_INDEX_SHOULDERPADS] = shoulderpads;
		equipInventory[EQUIP_INDEX_FACE] = face;
		equipInventory[EQUIP_INDEX_HAIR] = hair;
		equipInventory[EQUIP_INDEX_HAT] = hat;
		equipInventory[EQUIP_INDEX_USABLE] = usable;
		
		return equipInventory;
	}

}
