package net.hollowbit.archipelo.screen.screens.gamescreen;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.items.Item;

public class InventorySlot extends Widget {
	
	public static float SIZE = 48;
	public static float OFFSET = 2;
	
	private Skin skin;
	private NinePatch patch;
	private Item item;
	
	public InventorySlot (Item item) {
		this.item = item;
		this.skin = ArchipeloClient.getGame().getUiSkin();
		patch = skin.getPatch("textfield");
	}
	
	@Override
	public void draw (Batch batch, float parentAlpha) {
		batch.setColor(1, 1, 1, parentAlpha);
		patch.draw(batch, this.getX(), this.getY(), this.getWidth(), this.getHeight());
		batch.draw(item.getType().getIcon(), this.getX() + OFFSET, this.getY() + OFFSET, this.getWidth() - OFFSET * 2, this.getHeight() - OFFSET * 2);
		GlyphLayout quantityLayout = new GlyphLayout(skin.getFont("chat-font"), "" + item.quantity);
		skin.getFont("chat-font").draw(batch, quantityLayout, this.getX() + this.getWidth() - OFFSET - quantityLayout.width, this.getY() + quantityLayout.height + OFFSET);
		//Add item quantity indicator
		batch.setColor(1, 1, 1, 1);
		
		super.draw(batch, parentAlpha);
	}
	
	public interface InventorySlotActionHandler {
		
		public abstract void slotTouchUp (int slot, int inventoryNum);
		public abstract void slotTouchDown (int slot, int inventoryNum);
		
	}
	
}
