package net.hollowbit.archipelo.tools.inventory;

import com.badlogic.gdx.utils.Json;

import net.hollowbit.archipelo.items.Item;


public abstract class Inventory {
	
	public static final int ROW_LENGTH = 9;
	protected static Json json = new Json();
	
	/**
	 * Add an item to this inventory.
	 * @param item
	 * @return Returns the left overs that could not be added.
	 */
	protected abstract Item add(Item item);
	
	/**
	 * Set the item at the specific slot. Returns the item that was in the
	 * slot before if there was one.
	 * @param slot
	 * @return
	 */
	protected abstract Item setSlot(int slot, Item item);
	
	/**
	 * Remove an item from this inventory ignoring style.
	 * @param item
	 * @return False if item not found and not removed.
	 */
	protected boolean remove (Item item) {
		return this.remove(item, true);
	}
	
	/**
	 * Remove an item from this inventory.
	 * @param item
	 * @param ignoreStyle
	 * @return False if item not found and not removed.
	 */
	protected abstract boolean remove(Item item, boolean ignoreStyle);
	
	/**
	 * Remove an item from a specific slot
	 * @param slot
	 * @return
	 */
	public abstract Item removeFromSlot (int slot);
	
	/**
	 * Move an item from one slot to another and handles overflow. Ignores item style.
	 * @param fromSlot
	 * @param toSlot
	 * @param ignoreStyle
	 * @return False if the fromSlot is empty or the toSlot doesn't exist
	 */
	public boolean move (int fromSlot, int toSlot) {
		return this.move(fromSlot, toSlot, true);
	}
	
	/**
	 * Move an item from one slot to another and handles overflow.
	 * @param fromSlot
	 * @param toSlot
	 * @param ignoreStyle
	 * @return False if the fromSlot is empty or the toSlot doesn't exist
	 */
	public abstract boolean move(int fromSlot, int toSlot, boolean ignoreStyle);
	
	/**
	 * Checks if the inventory has a particular item in it, ready to be removed. Ignores style.
	 * @param item
	 * @return
	 */
	public boolean hasItem(Item item) {
		return this.hasItem(item, true);
	}
	
	/**
	 * Checks if the inventory has a particular item in it, ready to be removed.
	 * @param item
	 * @param ignoreStyle
	 * @return
	 */
	public abstract boolean hasItem(Item item, boolean ignoreStyle);
	
	/**
	 * Checks if the inventory is full.
	 * @return
	 */
	public abstract boolean isInventoryFull();
	
	/**
	 * Checks if a particular slot is empty
	 * @param slot
	 * @return
	 */
	protected abstract boolean isSlotEmpty(int slot);
	
	/**
	 * Returns if a slot exists within inventory.
	 * @param slot
	 * @return
	 */
	protected abstract boolean doesSlotExists (int slot);
	
	/**
	 * Removes all items with a quantity of 0.
	 */
	protected abstract void clean();

	/**
	 * Convert json to storage and save it
	 * @return
	 */
	public abstract void fromJson (String jsonStorage);
	
}
