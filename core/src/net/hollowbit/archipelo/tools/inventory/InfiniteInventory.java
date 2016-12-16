package net.hollowbit.archipelo.tools.inventory;

import java.util.ArrayList;
import java.util.LinkedList;

import net.hollowbit.archipelo.items.Item;

/**
 * Inventory with an infinite amount of space. Can never run out.
 * @author Nathanael
 *
 */
public class InfiniteInventory extends Inventory {
	
	private ArrayList<Item> storage;
	
	public InfiniteInventory (ArrayList<Item> startItems) {
		storage = startItems;
	}
	
	@Override
	protected Item add (Item item) {
		for (Item storageItem : storage) {
			if (storageItem.isSameType(item)) {
				int spaceLeftInSlot = storageItem.getType().maxStackSize - storageItem.quantity;
				
				if (item.quantity > spaceLeftInSlot) {
					storageItem.quantity += spaceLeftInSlot;
					item.quantity -= spaceLeftInSlot;
				} else {
					storageItem.quantity += item.quantity;
					item.quantity = 0;
				}
				
				if (item.quantity <= 0) {
					return null;
				}
			}
		}
		
		if (item.quantity > item.getType().maxStackSize) {
			while (item.quantity > item.getType().maxStackSize) {
				Item itemToAdd = new Item(item);
				itemToAdd.quantity = item.getType().maxStackSize;
				storage.add(itemToAdd);
				
				item.quantity -= item.getType().maxStackSize;
			}
			storage.add(item);
			clean();
		} else {
			storage.add(item);
		}
		return null;
	}

	@Override
	protected boolean remove (Item item) {
		return this.remove(item, true);
	}

	@Override
	protected boolean remove (Item item, boolean ignoreStyle) {
		if (!hasItem(item, ignoreStyle))
			return false;
		
		for (Item storageItem : storage) {
			if (storageItem.isSame(item, ignoreStyle)) {
				if (storageItem.quantity < item.quantity) {
					item.quantity -= storageItem.quantity;
					storageItem.quantity = 0;
				} else {
					storageItem.quantity -= item.quantity;
					item.quantity = 0;
				}
				
				if (item.quantity <= 0)
					break;
			}
		}
		
		clean();
		return true;
	}

	@Override
	public boolean move(int fromSlot, int toSlot, boolean ignoreStyle) {
		if (!doesSlotExists(toSlot) || isSlotEmpty(fromSlot))
			return false;
		
		Item fromItem = storage.get(fromSlot);
		Item toItem = storage.get(toSlot);
		
		if (fromItem.isSame(toItem, ignoreStyle)) {
			int spaceLeftInSlot = toItem.getType().maxStackSize - toItem.quantity;
			if (fromItem.quantity > spaceLeftInSlot) {
				toItem.quantity += spaceLeftInSlot;
				
				fromItem = new Item(fromItem);
				storage.get(fromSlot).quantity = 0;
				add(fromItem);
			} else {
				toItem.quantity += fromItem.quantity;
				fromItem.quantity = 0;
			}
		} else {
			storage.set(toSlot, fromItem);
			storage.set(fromSlot, toItem);
		}
		
		return true;
	}

	@Override
	public boolean hasItem(Item item, boolean ignoreStyle) {
		int quantityFound = 0;
		for (Item storageItem : storage) {
			if (storageItem.isSame(item, ignoreStyle)) {
				quantityFound += storageItem.quantity;
				
				if (quantityFound >= item.quantity)
					return true;
			}
		}
		
		return quantityFound >= item.quantity;
	}

	@Override
	public boolean isInventoryFull() {
		return false;//Infinite space means never full
	}

	@Override
	protected boolean isSlotEmpty(int slot) {
		if (doesSlotExists(slot))
			return storage.get(slot) == null;
		else
			return true;
	}

	@Override
	protected void clean() {
		LinkedList<Item> itemsToRemove = new LinkedList<Item>();
		for (Item item : storage) {
			if (item.quantity <= 0)
				itemsToRemove.add(item);
		}
		
		storage.removeAll(itemsToRemove);
	}

	@Override
	protected boolean doesSlotExists (int slot) {
		return slot < storage.size() && slot >= 0;
	}
	
	@Override
	public void fromJson (String jsonStorage) {
		Item[] storageArray = json.fromJson(Item[].class, jsonStorage);
		storage.clear();
		for (Item item : storageArray) {
			storage.add(item);
		}
	}

	@Override
	protected Item setSlot (int slot, Item item) {
		if (!doesSlotExists(slot))
			return null;
		
		Item replacedItem = removeFromSlot(slot);
		storage.set(slot, item);
		return replacedItem;
	}

	@Override
	public Item removeFromSlot (int slot) {
		if (!doesSlotExists(slot))
			return null;
		
		Item item = storage.get(slot);
		storage.set(slot, null);
		return item;
	}
}
