package net.hollowbit.archipelo.screen.screens.gamescreen;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.items.Item;

public class InventorySlot extends Widget {
	
	public static float SIZE = 48;
	public static float OFFSET = 2;
	public static float TIME_UNTIL_SHOW_INFO = 0.2f;
	
	private Skin skin;
	private NinePatch patch;
	private Item _item;
	private float timer = -1;
	private InventorySlotActionHandler handler;
	private boolean ignoreClick = false;
	
	public InventorySlot (Item item, final int slotNum, final int inventoryNum, final InventorySlotActionHandler handler) {
		this._item = item;
		this.handler = handler;
		this.skin = ArchipeloClient.getGame().getUiSkin();
		patch = skin.getPatch("textfield");
		
		this.setTouchable(Touchable.enabled);
		this.addListener(new InputListener() {
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				if (ArchipeloClient.IS_MOBILE)
					timer = 0;
				return false;
			}
		});
		this.addListener(new ClickListener() { 
			@Override
			public void clicked(InputEvent event, float x, float y) {
				timer = -1;
				if (ignoreClick)
					ignoreClick = false;
				else {
					Item itemToSend = _item;
					_item = null;
					handler.slotClick(slotNum, inventoryNum, itemToSend, x, y);
				}
				super.clicked(event, x, y);
			}
		});
		this.addListener(new ClickListener(Buttons.RIGHT) {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				timer = -1;
				if (!ArchipeloClient.IS_MOBILE)
					handler.slotRightClick(_item);
				super.clicked(event, x, y);
			}
		});
	}
	
	@Override
	public void act (float delta) {
		if (timer >= 0) {
			timer += delta;
			if (timer >= TIME_UNTIL_SHOW_INFO) {
				timer = -1;
				ignoreClick = true;
				handler.slotRightClick(_item);
			}
		}
		super.act(delta);
	}
	
	@Override
	public void draw (Batch batch, float parentAlpha) {
		batch.setColor(1, 1, 1, parentAlpha);
		patch.draw(batch, this.getX(), this.getY(), this.getWidth(), this.getHeight());
		if (_item != null) {
			Color itemIconDrawColor = new Color(_item.color);
			itemIconDrawColor.a = parentAlpha;
			batch.setColor(itemIconDrawColor);
			batch.draw(_item.getType().getIcon(), this.getX() + OFFSET, this.getY() + OFFSET, this.getWidth() - OFFSET * 2, this.getHeight() - OFFSET * 2);
			batch.setColor(1, 1, 1, parentAlpha);
			if (_item.quantity > 1) {
				GlyphLayout quantityLayout = new GlyphLayout(skin.getFont("chat-font"), "" + _item.quantity);
				skin.getFont("chat-font").draw(batch, quantityLayout, this.getX() + this.getWidth() - OFFSET - quantityLayout.width, this.getY() + quantityLayout.height + OFFSET);
			}
		}
		batch.setColor(1, 1, 1, 1);
		
		super.draw(batch, parentAlpha);
	}
	
	public void setItem (Item item) {
		this._item = item;
	}
	
	public Item getItem () {
		return this._item;
	}
	
	public interface InventorySlotActionHandler {
		
		public abstract void slotClick (int slot, int inventoryNum, Item item, float xOffset, float yOffset);
		public abstract void slotRightClick (Item item);
		
	}
	
}
