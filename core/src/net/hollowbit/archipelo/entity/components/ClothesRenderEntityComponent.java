package net.hollowbit.archipelo.entity.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.hollowbit.archipelo.entity.Entity;
import net.hollowbit.archipelo.entity.EntityComponent;
import net.hollowbit.archipelo.entity.EntitySnapshot;
import net.hollowbit.archipelo.entity.living.CurrentPlayer;
import net.hollowbit.archipelo.entity.living.Player;
import net.hollowbit.archipelo.items.Item;
import net.hollowbit.archipelo.items.ItemType;
import net.hollowbit.archipelo.tools.StaticTools;
import net.hollowbit.archipeloshared.Direction;

public class ClothesRenderEntityComponent extends EntityComponent {
	
	private Item[] displayInventory;
	private UseAnimationMeta useAnimationMeta;
	
	public ClothesRenderEntityComponent (Entity entity, Item[] currentDisplayInventory) {
		super(entity);
		this.displayInventory = currentDisplayInventory;
	}
	
	@Override
	public boolean render(SpriteBatch batch, boolean previouslyCancelled) {
		return true;
	}
	
	@Override
	public boolean renderAfter(SpriteBatch batch, boolean previouslyCancelled) {
		if (previouslyCancelled)
			return false;
		
		float x = entity.getLocation().getX();
		float y = entity.getLocation().getY();
		
		Direction direction = entity.getDirection();
		String animationId = entity.getAnimationManager().getAnimationId();
		float stateTime = entity.getAnimationManager().getStateTime();
		float animationLength = entity.getEntityType().getEntityAnimation(animationId).getTotalRuntime();
		
		if (animationId.equals("roll"))//Rolling animation use meta data for rolling direction
			direction = Direction.values()[Integer.parseInt(entity.getAnimationManager().getAnimationMeta())];
		
		batch.draw(entity.getEntityType().getAnimationFrame(animationId, direction, stateTime, entity.getStyle()), x, y);
		
		boolean isUseAnimation = entity.getAnimationManager().isUseAnimation();
		if (isUseAnimation) {
			if (entity.getAnimationManager().getAnimationMeta().equals("")) {//If there is no meta data
				animationLength = CurrentPlayer.EMPTY_HAND_USE_ANIMATION_LENTH;
			} else {//If there is, load it if it's not already loaded
				if (useAnimationMeta == null) {
					useAnimationMeta = new UseAnimationMeta(entity.getAnimationManager().getAnimationMeta());
					if (useAnimationMeta.useItem.equipType == ItemType.EQUIP_INDEX_USABLE)
						animationLength = useAnimationMeta.useItem.useAnimationLength;
				} else {
					if (useAnimationMeta.useItem.equipType == ItemType.EQUIP_INDEX_USABLE)
						animationLength = useAnimationMeta.useItem.useAnimationLength;
				}
			}
		} else {
			useAnimationMeta = null;
		}
		
		boolean renderUsableFirst = false;
		if (entity.getDirection() == Direction.UP || entity.getDirection() == Direction.UP_LEFT || entity.getDirection() == Direction.DOWN_RIGHT)
			renderUsableFirst = true;
		
		//If facing up, render the usable item first
		if (renderUsableFirst) {
			if (isUseAnimation && useAnimationMeta != null) {
				if (useAnimationMeta.useItem.equipType == ItemType.EQUIP_INDEX_USABLE) {//Only render use animation if item is usable
					batch.setColor(new Color(useAnimationMeta.useColorR, useAnimationMeta.useColorG, useAnimationMeta.useColorB, useAnimationMeta.useColorA));
					batch.draw(useAnimationMeta.useItem.getAnimationFrameForUsable(animationId, direction, stateTime, useAnimationMeta.useStyle, useAnimationMeta.useType, useAnimationMeta.useItem.useAnimationLength), x, y);
					batch.setColor(1, 1, 1, 1);
				}
			}
		}
		
		//Render display items
		for (int i = 0; i < displayInventory.length - 1; i++) {//Loop through each part of equipable
			Item displayItem = displayInventory[i];
			
			//Render boots over pants
			if (i == Player.EQUIP_INDEX_BOOTS) 
				displayItem = displayInventory[Player.EQUIP_INDEX_PANTS];
			else if (i == Player.EQUIP_INDEX_PANTS) 
				displayItem = displayInventory[Player.EQUIP_INDEX_BOOTS];
			
			if (displayItem == null)//Don't draw null clothes
				continue;
			
			if (i == Player.EQUIP_INDEX_FACE) {//Change color of different face elements such as for hair, iris, etc.
				batch.setColor(1, 1, 1, 1);
				batch.draw(ItemType.getItemTypeByItem(displayItem).getAnimationFrame(animationId, direction, stateTime, 0, animationLength), x, y);//Draw mouth
				
				batch.setColor(new Color(displayItem.color));
				batch.draw(ItemType.getItemTypeByItem(displayItem).getAnimationFrame(animationId, direction, stateTime, 1, animationLength), x, y);//Draw eye's iris with color
				
				batch.setColor(new Color(displayInventory[Player.EQUIP_INDEX_HAIR].color));
				batch.draw(ItemType.getItemTypeByItem(displayItem).getAnimationFrame(animationId, direction, stateTime, 2, animationLength), x, y);//Draw eyebrows with hair color
			} else {
				batch.setColor(new Color(displayItem.color));
				batch.draw(ItemType.getItemTypeByItem(displayItem).getAnimationFrame(animationId, direction, stateTime, displayItem.style, animationLength), x, y);
			}
			
			batch.setColor(1, 1, 1, 1);
		}
		
		//If not facing up, render the usable item last
		if (!renderUsableFirst) {
			if (isUseAnimation && useAnimationMeta != null) {
				if (useAnimationMeta.useItem.equipType == ItemType.EQUIP_INDEX_USABLE) {//Only render use animation if item is usable
					batch.setColor(new Color(useAnimationMeta.useColorR, useAnimationMeta.useColorG, useAnimationMeta.useColorB, useAnimationMeta.useColorA));
					batch.draw(useAnimationMeta.useItem.getAnimationFrameForUsable(animationId, direction, stateTime, useAnimationMeta.useStyle, useAnimationMeta.useType, useAnimationMeta.useItem.useAnimationLength), x, y);
					batch.setColor(1, 1, 1, 1);
				}
			}
		}
		
		return false;
	}
	
	@Override
	public void applyChangesSnapshot(EntitySnapshot snapshot) {
		super.applyChangesSnapshot(snapshot);
		
		if (snapshot.doesPropertyExist("displayInventory")) {
			this.displayInventory = StaticTools.getJson().fromJson(Item[].class, snapshot.getString("displayInventory", ""));
		}
	}

	public Item[] getDisplayInventory() {
		return displayInventory;
	}
	
	private class UseAnimationMeta {

		public ItemType useItem;
		public int useType;
		public int useStyle;
		public float useColorR;
		public float useColorG;
		public float useColorB;
		public float useColorA;
		
		public UseAnimationMeta (String meta) {
			try {
				String[] split = entity.getAnimationManager().getAnimationMeta().split(";");
				useItem = ItemType.getItemTypeById(split[0]);
				useType = Integer.parseInt(split[1]);
				useStyle = Integer.parseInt(split[2]);
				useColorR = Float.parseFloat(split[3]);
				useColorG = Float.parseFloat(split[4]);
				useColorB = Float.parseFloat(split[5]);
				useColorA = Float.parseFloat(split[6]);
			} catch (Exception e) {
				throw new IllegalArgumentException("Invalid aniamtion meta.");
			}
		}
	}
	
}
