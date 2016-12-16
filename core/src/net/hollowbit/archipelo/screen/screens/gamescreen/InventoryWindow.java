package net.hollowbit.archipelo.screen.screens.gamescreen;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.items.Item;
import net.hollowbit.archipelo.items.ItemType;
import net.hollowbit.archipelo.screen.screens.gamescreen.InventorySlot.InventorySlotActionHandler;
import net.hollowbit.archipelo.tools.LM;
import net.hollowbit.archipelo.tools.QuickUi;

public class InventoryWindow extends Window implements InventorySlotActionHandler {

	public InventoryWindow () {
		super(LM.ui("inventory"), ArchipeloClient.getGame().getUiSkin());
		
		this.setResizable(false);
		this.setMovable(true);
		this.setSize(400, 300);
		
		QuickUi.addCloseButtonToWindow(this);
		
		final int slot = 0;
		final int inventoryNum = 0;
		final InventorySlotActionHandler handler = this;
		InventorySlot inventorySlot = new InventorySlot(new Item(ItemType.GLOVES_BASIC, 0, 64));
		inventorySlot.setTouchable(Touchable.enabled);
		
		inventorySlot.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				handler.slotTouchUp(slot, inventoryNum);
				super.clicked(event, x, y);
			}
		});
		
		inventorySlot.addListener(new InputListener() {
			
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				handler.slotTouchDown(slot, inventoryNum);
				return super.touchDown(event, x, y, pointer, button);
			}
		});
		this.add(inventorySlot).width(InventorySlot.SIZE).height(InventorySlot.SIZE);
	}

	@Override
	public void slotTouchUp (int slot, int inventoryNum) {
		System.out.println("Inventory slot touched up! " + slot + "   " + inventoryNum);
	}

	@Override
	public void slotTouchDown (int slot, int inventoryNum) {
		System.out.println("Inventory slot touched down! " + slot + "   " + inventoryNum);
	}

}
