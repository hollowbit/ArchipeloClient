package net.hollowbit.archipelo.screen.screens.gamescreen;

import java.util.HashMap;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.entity.living.Player;
import net.hollowbit.archipelo.form.Form;
import net.hollowbit.archipelo.items.Item;
import net.hollowbit.archipelo.network.packets.FormInteractPacket;
import net.hollowbit.archipelo.screen.screens.gamescreen.InventorySlot.InventorySlotActionHandler;
import net.hollowbit.archipelo.screen.screens.mainmenu.CharacterDisplay;
import net.hollowbit.archipelo.tools.LM;
import net.hollowbit.archipelo.tools.QuickUi;
import net.hollowbit.archipelo.tools.StaticTools;
import net.hollowbit.archipeloshared.FormData;

public class InventoryForm extends Form implements InventorySlotActionHandler {
	
	private static final int ROW_SIZE = 5;
	
	private static final String KEY_MAIN_INVENTORY = "mainInventory";
	private static final String KEY_EQUIPPED_INVENTORY = "equippedInventory";
	private static final String KEY_COSMETIC_INVENTORY = "cosmeticInventory";

	private static final String KEY_FROM_SLOT = "fromSlot";
	private static final String KEY_TO_SLOT = "toSlot";
	private static final String KEY_FROM_INVENTORY = "fromInventory";
	private static final String KEY_TO_INVENTORY = "toInventory";
	
	public static final int MAIN_INVENTORY = 0;
	public static final int BANK_INVENTORY = 1;
	public static final int EQUIPPED_INVENTORY = 2;
	public static final int COSMETIC_INVENTORY = 3;
	
	private static final int DISPLAY_SIZE = 150;
	
	private Table mainInventoryTable;
	private Table equippedInventoryTable;
	private Table cosmeticInventoryTable;
	private CharacterDisplay characterDisplay;
	
	private int slotDown = -1, inventoryDown = -1;
	
	public InventoryForm () {
		super(LM.ui("inventory"));
		
		this.setResizable(false);
		this.setMovable(true);
		this.setSize(550, 400);
		QuickUi.addCloseButtonToWindow(this);

		mainInventoryTable = new Table(getSkin());
		equippedInventoryTable = new Table(getSkin());
		cosmeticInventoryTable = new Table(getSkin());
		
		characterDisplay = new CharacterDisplay(new Item[Player.EQUIP_SIZE], true);
		this.add(characterDisplay).width(DISPLAY_SIZE).height(DISPLAY_SIZE);
	}
	
	@Override
	public void act(float delta) {
		if (gameScreen.getWorld() != null && gameScreen.getWorld().getPlayer() != null)
			characterDisplay.setEquippedInventory(gameScreen.getWorld().getPlayer().getDisplayInventory());
		super.act(delta);
	}

	@Override
	public void slotTouchDown (int slot, int inventoryNum) {
		System.out.println("Inventory slot touched down! " + slot + "   " + inventoryNum);
		slotDown = slot;
		inventoryDown = inventoryNum;
	}

	@Override
	public void slotTouchUp (int slot, int inventoryNum) {
		System.out.println("Inventory slot touched up! " + slot + "   " + inventoryNum);
		if (slot != slotDown || inventoryNum != inventoryDown) {
			String command = "move";
			HashMap<String, String> data = new HashMap<String, String>();
			data.put(KEY_FROM_SLOT, "" + slotDown);
			data.put(KEY_TO_SLOT, "" + slot);
			data.put(KEY_FROM_INVENTORY, "" + inventoryDown);
			data.put(KEY_TO_INVENTORY, "" + inventoryNum);
			
			ArchipeloClient.getGame().getNetworkManager().sendPacket(new FormInteractPacket(this.id, command, data));
		}
	}

	@Override
	public void update (FormData formData) {
		mainInventoryTable.remove();
		equippedInventoryTable.remove();
		cosmeticInventoryTable.remove();
		
		mainInventoryTable = new Table(getSkin());
		Item[] mainInventory = StaticTools.getJson().fromJson(Item[].class, formData.data.get(KEY_MAIN_INVENTORY));
		int rowLength = 0;
		for (int i = 0; i < mainInventory.length; i++) {//Loop through inventory and add slots to window
			InventorySlot inventorySlot = new InventorySlot(mainInventory[i], i, MAIN_INVENTORY, this);
			mainInventoryTable.add(inventorySlot).width(InventorySlot.SIZE).height(InventorySlot.SIZE).pad(1);
			
			rowLength++;
			if (rowLength >= ROW_SIZE) {
				mainInventoryTable.row();
				rowLength = 0;
			}
		}
		
		equippedInventoryTable = new Table(getSkin());
		Item[] equippedInventory = StaticTools.getJson().fromJson(Item[].class, formData.data.get(KEY_EQUIPPED_INVENTORY));
		for (int i = 0; i < equippedInventory.length; i++) {//Loop through inventory and add slots to window
			InventorySlot inventorySlot = new InventorySlot(equippedInventory[i], i, EQUIPPED_INVENTORY, this);
			equippedInventoryTable.add(inventorySlot).width(InventorySlot.SIZE).height(InventorySlot.SIZE).pad(1);
			equippedInventoryTable.row();
		}
		
		cosmeticInventoryTable = new Table(getSkin());
		Item[] cosmeticInventory = StaticTools.getJson().fromJson(Item[].class, formData.data.get(KEY_COSMETIC_INVENTORY));
		for (int i = 0; i < cosmeticInventory.length; i++) {//Loop through inventory and add slots to window
			InventorySlot inventorySlot = new InventorySlot(cosmeticInventory[i], i, COSMETIC_INVENTORY, this);
			cosmeticInventoryTable.add(inventorySlot).width(InventorySlot.SIZE).height(InventorySlot.SIZE).pad(1);
			cosmeticInventoryTable.row();
		}
		
		this.add(equippedInventoryTable).padRight(10);
		this.add(cosmeticInventoryTable).padRight(25);
		this.add(mainInventoryTable);
	}

}
