package net.hollowbit.archipelo.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.hollowbit.archipelo.entity.living.CurrentPlayer;
import net.hollowbit.archipeloshared.UseTypeSettings;

public class Item {

	public static final int DEFAULT_COLOR = Color.rgba8888(new Color(1, 1, 1, 1));
	
	public String id;
	public int color = DEFAULT_COLOR;
	public int durability = 1;
	public int style = 0;
	public int quantity = 1;
	
	public Item () {}
	
	/**
	 * Duplicates an item
	 * @param item
	 */
	public Item (Item item) {
		this.id = item.id;
		this.color = item.color;
		this.durability = item.durability;
		this.style = item.style;
		this.quantity = item.quantity;
	}
	
	public Item (ItemType type) {
		this.id = type.id;
	}
	
	public Item (ItemType type, int style) {
		this.id = type.id;
		this.style = style;
	}
	
	public Item (ItemType type, int style, int quantity) {
		this.id = type.id;
		this.style = style;
		this.quantity = quantity;
	}
	
	public UseTypeSettings useTap(CurrentPlayer user) {
		if (this.getType().getUseType() != null)
			return this.getType().getUseType().useItemTap(user, this);
		else
			return null;
	}
	
	public UseTypeSettings useDoubleTap(CurrentPlayer user, float delta) {
		if (this.getType().getUseType() != null)
			return this.getType().getUseType().useItemDoubleTap(user, this, delta);
		else
			return null;
	}
	
	public UseTypeSettings useHold(CurrentPlayer user, float duration) {
		if (this.getType().getUseType() != null)
			return this.getType().getUseType().useItemHold(user, this, duration);
		else
			return null;
	}
	
	public ItemType getType () {
		return ItemType.getItemTypeByItem(this);
	}
	
	public TextureRegion getIcon() {
		if (this.getType() != null)
			return this.getType().getIcon(style);
		else
			return ItemType.getInvalidIcon();
	}
	
	/**
	 * Returns if item is same type
	 * @param item
	 * @return
	 */
	public boolean isSameType (Item item) {
		return this.id.equals(item.id);
	}
	
	/**
	 * Returns if item is same type and style
	 * @param item
	 * @return
	 */
	public boolean isSameTypeAndStyle (Item item) {
		return isSameType(item) && this.style == item.style;
	}
	
	/**
	 * Returns if item is same type
	 * @param item
	 * @param ignoreStyle
	 * @return
	 */
	public boolean isSame (Item item, boolean ignoreStyle) {
		if (ignoreStyle)
			return isSameType(item);
		else
			return isSameTypeAndStyle(item);
	}
	
	/**
	 * Creates a new color object of this items color.
	 * @return
	 */
	public Color getColor () {
		return new Color(color);
	}
}
