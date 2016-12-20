package net.hollowbit.archipelo.screen.screens.gamescreen;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.entity.living.Player;
import net.hollowbit.archipelo.form.Form;
import net.hollowbit.archipelo.items.Item;
import net.hollowbit.archipelo.items.ItemType;
import net.hollowbit.archipelo.network.packets.FormInteractPacket;
import net.hollowbit.archipelo.screen.screens.gamescreen.InventorySlot.InventorySlotActionHandler;
import net.hollowbit.archipelo.screen.screens.mainmenu.CharacterDisplay;
import net.hollowbit.archipelo.tools.LM;
import net.hollowbit.archipelo.tools.QuickUi;
import net.hollowbit.archipelo.tools.StaticTools;
import net.hollowbit.archipeloshared.FormData;

public class InventoryForm extends Form implements InventorySlotActionHandler {

	public static final int INVENTORY_SIZE = 20;
	public static final int EQUIPPED_INVENTORY_SIZE = 6;
	public static final int WEAPON_EQUIPPED_SIZE = 1;
	public static final int CONSUMABLES_EQUIPPED_SIZE = 3;
	public static final int BUFFS_EQUIPPED_SIZE = 3;
	public static final int AMMO_EQUIPPED_SIZE = 2;
	
	private static final int ROW_SIZE = 5;
	
	private static final String KEY_MAIN_INVENTORY = "mainInventory";
	private static final String KEY_EQUIPPED_INVENTORY = "equippedInventory";
	private static final String KEY_COSMETIC_INVENTORY = "cosmeticInventory";
	private static final String KEY_WEAPON_INVENTORY = "weaponInventory";
	private static final String KEY_CONSUMABLES_INVENTORY = "consumablesInventory";
	private static final String KEY_BUFFS_INVENTORY = "buffsInventory";
	private static final String KEY_AMMO_INVENTORY = "ammoInventory";

	private static final String KEY_FROM_SLOT = "fromSlot";
	private static final String KEY_TO_SLOT = "toSlot";
	private static final String KEY_FROM_INVENTORY = "fromInventory";
	private static final String KEY_TO_INVENTORY = "toInventory";
	
	public static final int MAIN_INVENTORY = 0;
	public static final int BANK_INVENTORY = 1;
	public static final int EQUIPPED_INVENTORY = 2;
	public static final int COSMETIC_INVENTORY = 3;
	//public static final int UNEDITABLE_INVENTORY = 4;
	public static final int WEAPON_INVENTORY = 5;
	public static final int CONSUMABLES_INVENTORY = 6;
	public static final int BUFFS_INVENTORY = 7;
	public static final int AMMO_INVENTORY = 8;
	
	private static final int DISPLAY_SIZE = 150;
	
	private Table mainInventoryTable;
	private Table equippedInventoryTable;
	private Table cosmeticInventoryTable;
	private Table weaponInventoryTable;
	private Table consumablesInventoryTable;
	private Table buffsInventoryTable;
	private Table ammoInventoryTable;
	
	private CharacterDisplay characterDisplay;
	
	private int slotDown = -1, inventoryDown = -1;
	private Item itemInHand = null;
	
	private float xOffset = 0, yOffset = 0;
	
	private InventorySlot[] mainInventorySlots;
	private InventorySlot[] equippedInventorySlots;
	private InventorySlot[] cosmeticInventorySlots;
	private InventorySlot[] weaponInventorySlots;
	private InventorySlot[] consumablesInventorySlots;
	private InventorySlot[] buffsInventorySlots;
	private InventorySlot[] ammoInventorySlots;
	
	private HashMap<Integer, InventorySlot[]> inventorySlotCollections;
	
	public InventoryForm () {
		super(LM.ui("inventory"));
		
		this.setResizable(false);
		this.setMovable(true);
		this.setSize(550, 400);
		QuickUi.addCloseButtonToWindow(this);
		
		characterDisplay = new CharacterDisplay(new Item[Player.EQUIP_SIZE], true);
		
		inventorySlotCollections = new HashMap<Integer, InventorySlot[]>();
		
		//Main Inventory//
		mainInventorySlots = new InventorySlot[INVENTORY_SIZE];
		mainInventoryTable = new Table(getSkin());
		
		Label storageLbl = new Label(LM.ui("storage"), getSkin());
		mainInventoryTable.add(storageLbl).colspan(ROW_SIZE);
		mainInventoryTable.row();
		
		int rowLength = 0;
		for (int i = 0; i < mainInventorySlots.length; i++) {
			InventorySlot inventorySlot = new InventorySlot(null, i, MAIN_INVENTORY, this);
			mainInventoryTable.add(inventorySlot).width(InventorySlot.SIZE).height(InventorySlot.SIZE).pad(1);
			mainInventorySlots[i] = inventorySlot;
			
			rowLength++;
			if (rowLength >= ROW_SIZE) {
				mainInventoryTable.row();
				rowLength = 0;
			}
		}
		inventorySlotCollections.put(MAIN_INVENTORY, mainInventorySlots);

		//Equipped Inventory//
		equippedInventorySlots = new InventorySlot[EQUIPPED_INVENTORY_SIZE];
		equippedInventoryTable = new Table(getSkin());
		
		Label equippedLbl = new Label(LM.ui("equippedTag"), getSkin());
		equippedInventoryTable.add(equippedLbl);
		equippedInventoryTable.row();
		
		for (int i = equippedInventorySlots.length - 1; i >= 0; i--) {//Loop through inventory and add slots to window
			InventorySlot inventorySlot = new InventorySlot(null, i, EQUIPPED_INVENTORY, this);
			equippedInventoryTable.add(inventorySlot).width(InventorySlot.SIZE).height(InventorySlot.SIZE).pad(1);
			equippedInventoryTable.row();
			equippedInventorySlots[i] = inventorySlot;
		}
		inventorySlotCollections.put(EQUIPPED_INVENTORY, equippedInventorySlots);

		//Cosmetic Inventory//
		cosmeticInventorySlots = new InventorySlot[EQUIPPED_INVENTORY_SIZE];
		cosmeticInventoryTable = new Table(getSkin());
		
		Label cosmeticLbl = new Label(LM.ui("cosmeticTag"), getSkin());
		cosmeticInventoryTable.add(cosmeticLbl);
		cosmeticInventoryTable.row();
		
		for (int i = cosmeticInventorySlots.length - 1; i >= 0; i--) {//Loop through inventory and add slots to window
			InventorySlot inventorySlot = new InventorySlot(null, i, COSMETIC_INVENTORY, this);
			cosmeticInventoryTable.add(inventorySlot).width(InventorySlot.SIZE).height(InventorySlot.SIZE).pad(1);
			cosmeticInventoryTable.row();
			cosmeticInventorySlots[i] = inventorySlot;
		}
		inventorySlotCollections.put(COSMETIC_INVENTORY, cosmeticInventorySlots);

		//Weapon Inventory//
		weaponInventorySlots = new InventorySlot[WEAPON_EQUIPPED_SIZE];
		weaponInventoryTable = new Table(getSkin());
		
		for (int i = 0; i < weaponInventorySlots.length; i++) {
			InventorySlot inventorySlot = new InventorySlot(null, i, WEAPON_INVENTORY, this);
			weaponInventoryTable.add(inventorySlot).width(InventorySlot.SIZE).height(InventorySlot.SIZE).pad(1);
			weaponInventoryTable.row();
			weaponInventorySlots[i] = inventorySlot;
		}
		inventorySlotCollections.put(WEAPON_INVENTORY, weaponInventorySlots);
		
		//Consumables Inventory//
		consumablesInventorySlots = new InventorySlot[CONSUMABLES_EQUIPPED_SIZE];
		consumablesInventoryTable = new Table(getSkin());
		
		for (int i = 0; i < consumablesInventorySlots.length; i++) {
			InventorySlot inventorySlot = new InventorySlot(null, i, CONSUMABLES_INVENTORY, this);
			consumablesInventoryTable.add(inventorySlot).width(InventorySlot.SIZE).height(InventorySlot.SIZE).pad(1);
			consumablesInventorySlots[i] = inventorySlot;
		}
		inventorySlotCollections.put(CONSUMABLES_INVENTORY, consumablesInventorySlots);
		
		//Buffs Inventory//
		buffsInventorySlots = new InventorySlot[BUFFS_EQUIPPED_SIZE];
		buffsInventoryTable = new Table(getSkin());
		
		for (int i = 0; i < buffsInventorySlots.length; i++) {
			InventorySlot inventorySlot = new InventorySlot(null, i, BUFFS_INVENTORY, this);
			buffsInventoryTable.add(inventorySlot).width(InventorySlot.SIZE).height(InventorySlot.SIZE).pad(1);
			buffsInventorySlots[i] = inventorySlot;
		}
		inventorySlotCollections.put(BUFFS_INVENTORY, buffsInventorySlots);
		
		//Ammo Inventory//
		ammoInventorySlots = new InventorySlot[AMMO_EQUIPPED_SIZE];
		ammoInventoryTable = new Table(getSkin());
		
		for (int i = 0; i < ammoInventorySlots.length; i++) {
			InventorySlot inventorySlot = new InventorySlot(null, i, AMMO_INVENTORY, this);
			ammoInventoryTable.add(inventorySlot).width(InventorySlot.SIZE).height(InventorySlot.SIZE).pad(1);
			ammoInventorySlots[i] = inventorySlot;
		}
		inventorySlotCollections.put(AMMO_INVENTORY, ammoInventorySlots);
		
		//Add widgets to table
		Table sideTable = new Table(getSkin());
		sideTable.add(buffsInventoryTable).colspan(2);
		sideTable.row();
		sideTable.add(characterDisplay).width(DISPLAY_SIZE).height(DISPLAY_SIZE).colspan(2);
		sideTable.row();
		sideTable.add(weaponInventoryTable);
		sideTable.add(ammoInventoryTable);
		sideTable.row();
		sideTable.add(consumablesInventoryTable).colspan(2);
		this.add(sideTable);
		this.add(equippedInventoryTable).padRight(10);
		this.add(cosmeticInventoryTable).padRight(25);
		this.add(mainInventoryTable);
		this.pack();
	}
	
	@Override
	public void act(float delta) {
		if (gameScreen.getWorld() != null && gameScreen.getWorld().getPlayer() != null)
			characterDisplay.setEquippedInventory(gameScreen.getWorld().getPlayer().getDisplayInventory());
		super.act(delta);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		if (itemInHand != null)
			batch.draw(itemInHand.getType().getIcon(), Gdx.input.getX() - xOffset, Gdx.graphics.getHeight() - Gdx.input.getY() - yOffset, InventorySlot.SIZE - InventorySlot.OFFSET * 2, InventorySlot.SIZE - InventorySlot.OFFSET * 2);
	}

	@Override
	public void slotClick (int slot, int inventoryNum, Item item, float xOffset, float yOffset) {
		if (this.itemInHand == null) {
			slotDown = slot;
			inventoryDown = inventoryNum;
			this.itemInHand = item;
			this.xOffset = xOffset;
			this.yOffset = yOffset;
		} else {
			boolean valid = true;
			if (slot != slotDown || inventoryNum != inventoryDown) {
				if (inventoryNum == WEAPON_INVENTORY) {
					if (itemInHand.getType().equipType != ItemType.EQUIP_INDEX_USABLE)
						valid = false;
				} else if (inventoryNum == AMMO_INVENTORY) {
					if (!itemInHand.getType().ammo)
						valid = false;
				} else if (inventoryNum == BUFFS_INVENTORY) {
					if (!itemInHand.getType().buff)
						valid = false;
				} else if (inventoryNum == CONSUMABLES_INVENTORY) {
					if (!itemInHand.getType().consumable)
						valid = false;
				} else if (inventoryNum == EQUIPPED_INVENTORY || inventoryNum == COSMETIC_INVENTORY) {
					if (itemInHand.getType().equipType != slot)
						valid = false;
				}
				
				if (valid) {
					String command = "move";
					HashMap<String, String> data = new HashMap<String, String>();
					data.put(KEY_FROM_SLOT, "" + slotDown);
					data.put(KEY_TO_SLOT, "" + slot);
					data.put(KEY_FROM_INVENTORY, "" + inventoryDown);
					data.put(KEY_TO_INVENTORY, "" + inventoryNum);

					ArchipeloClient.getGame().getNetworkManager().sendPacket(new FormInteractPacket(this.id, command, data));	
				}
			}
			if (valid)
				inventorySlotCollections.get(inventoryNum)[slot].setItem(itemInHand);
			else
				inventorySlotCollections.get(inventoryDown)[slotDown].setItem(itemInHand);
			slotDown = -1;
			inventoryDown = -1;
			itemInHand = null;
		}
	}

	@Override
	public void update (FormData formData) {
		Item[] mainInventory = StaticTools.getJson().fromJson(Item[].class, formData.data.get(KEY_MAIN_INVENTORY));
		for (int i = 0; i < mainInventory.length; i++) {//Loop through inventory and add slots to window
			mainInventorySlots[i].setItem(mainInventory[i]);
		}
		
		Item[] equippedInventory = StaticTools.getJson().fromJson(Item[].class, formData.data.get(KEY_EQUIPPED_INVENTORY));
		for (int i = 0; i < equippedInventory.length; i++) {//Loop through inventory and add slots to window
			equippedInventorySlots[i].setItem(equippedInventory[i]);
		}
		
		Item[] cosmeticInventory = StaticTools.getJson().fromJson(Item[].class, formData.data.get(KEY_COSMETIC_INVENTORY));
		for (int i = 0; i < cosmeticInventory.length; i++) {//Loop through inventory and add slots to window
			cosmeticInventorySlots[i].setItem(cosmeticInventory[i]);
		}
		
		Item[] weaponInventory = StaticTools.getJson().fromJson(Item[].class, formData.data.get(KEY_WEAPON_INVENTORY));
		for (int i = 0; i < weaponInventory.length; i++) {//Loop through inventory and add slots to window
			weaponInventorySlots[i].setItem(weaponInventory[i]);
		}
		
		Item[] consumablesInventory = StaticTools.getJson().fromJson(Item[].class, formData.data.get(KEY_CONSUMABLES_INVENTORY));
		for (int i = 0; i < consumablesInventory.length; i++) {//Loop through inventory and add slots to window
			consumablesInventorySlots[i].setItem(consumablesInventory[i]);
		}
		
		Item[] buffsInventory = StaticTools.getJson().fromJson(Item[].class, formData.data.get(KEY_BUFFS_INVENTORY));
		for (int i = 0; i < buffsInventory.length; i++) {//Loop through inventory and add slots to window
			buffsInventorySlots[i].setItem(buffsInventory[i]);
		}
		
		Item[] ammoInventory = StaticTools.getJson().fromJson(Item[].class, formData.data.get(KEY_AMMO_INVENTORY));
		for (int i = 0; i < ammoInventory.length; i++) {//Loop through inventory and add slots to window
			ammoInventorySlots[i].setItem(ammoInventory[i]);
		}
	}

}
