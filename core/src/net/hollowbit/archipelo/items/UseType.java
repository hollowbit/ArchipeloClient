package net.hollowbit.archipelo.items;

import net.hollowbit.archipeloshared.UseTypeSettings;

public interface UseType {
	
	/**
	 * Uses an item on tapping action button. Returns a UseTypeSettings object with information about animations and sounds.
	 * Could return null if unsuccessful.
	 * @param item
	 * @return
	 */
	public abstract UseTypeSettings useItemTap (Item item);
	
	/**
	 * Use item after action button held. Returns a UseTypeSettings object with information about animations and sounds.
	 * Could return null if unsuccessful, or not held down long enough.
	 * @param item
	 * @param duration
	 * @return
	 */
	public abstract UseTypeSettings useItemHold (Item item, float duration);
	
	/**
	 * Use an item on double tapping action button. Returns a UseTypeSettings object with information about animations and sounds.
	 * Could return null if unsuccessful.
	 * @param item
	 * @param delta Time between both presses.
	 * @return
	 */
	public abstract UseTypeSettings useItemDoubleTap (Item item, float delta);
	
}
