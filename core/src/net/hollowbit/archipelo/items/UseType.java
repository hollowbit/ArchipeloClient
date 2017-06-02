package net.hollowbit.archipelo.items;

import net.hollowbit.archipelo.entity.living.CurrentPlayer;
import net.hollowbit.archipeloshared.UseTypeSettings;

public abstract class UseType {
	
	protected static final float STANDARD_DOUBLE_TAP_DELTA = 0.21f;
	
	/**
	 * Uses an item on tapping action button. Returns a UseTypeSettings object with information about animations and sounds.
	 * Could return null if unsuccessful.
	 * @param item
	 * @return
	 */
	public abstract UseTypeSettings useItemTap (CurrentPlayer user, Item item);
	
	/**
	 * Use item after action button held. Returns a UseTypeSettings object with information about animations and sounds.
	 * Could return null if unsuccessful, or not held down long enough.
	 * @param item
	 * @param duration
	 * @return
	 */
	public abstract UseTypeSettings useItemHold (CurrentPlayer user, Item item, float duration);
	
	/**
	 * Use an item on double tapping action button. Returns a UseTypeSettings object with information about animations and sounds.
	 * Could return null if unsuccessful.
	 * @param item
	 * @param delta Time between both presses.
	 * @return
	 */
	public abstract UseTypeSettings useItemDoubleTap (CurrentPlayer user, Item item, float delta);
	
}
