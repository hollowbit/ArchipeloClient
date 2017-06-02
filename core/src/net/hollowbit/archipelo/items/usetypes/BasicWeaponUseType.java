package net.hollowbit.archipelo.items.usetypes;

import net.hollowbit.archipelo.entity.living.CurrentPlayer;
import net.hollowbit.archipelo.items.Item;
import net.hollowbit.archipelo.items.UseType;
import net.hollowbit.archipeloshared.UseTypeSettings;

public class BasicWeaponUseType extends UseType {

	@Override
	public UseTypeSettings useItemTap(CurrentPlayer user, Item item) {
		//int useAnimation = user.getRandom().nextInt(item.getType().numOfUseAnimations);
		return new UseTypeSettings(0, 0);
	}

	@Override
	public UseTypeSettings useItemHold(CurrentPlayer user, Item item, float duration) {
		return null;
	}

	@Override
	public UseTypeSettings useItemDoubleTap(CurrentPlayer user, Item item, float delta) {
		return null;
	}

}
