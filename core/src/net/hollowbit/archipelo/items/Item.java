package net.hollowbit.archipelo.items;

import com.badlogic.gdx.graphics.Color;

public class Item {

	public static final int DEFAULT_COLOR = Color.rgba8888(new Color(1, 1, 1, 1));
	
	public String id;
	public int color = DEFAULT_COLOR;
	public int style = 0;
	
	public Item () {}
	
	public Item (ItemType type) {
		this.id = type.id;
	}
	
	public ItemType getType () {
		return ItemType.getItemTypeByItem(this);
	}
	
}
