package net.hollowbit.archipelo.screen.screens.mainmenu;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.entity.living.Player;
import net.hollowbit.archipelo.items.Item;
import net.hollowbit.archipeloshared.Direction;

public class CharacterDisplay extends Actor {
	
	int direction = 0;
	Item[] equippedInventory;
	
	public CharacterDisplay (Item[] equippedInventory) {
		this.equippedInventory = equippedInventory;
		this.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				direction++;
				if (direction >= Direction.TOTAL)
					direction = 0;
				super.clicked(event, x, y);
			}
		});
	}
	
	public void setEquippedInventory (Item[] equippedInventory) {
		this.equippedInventory = equippedInventory;
	}
	
	@Override
	public void draw (Batch batch, float parentAlpha) {
		Player.drawPlayer(batch, Direction.IN_A_ROW[direction], true, false, this.getX(), this.getY(), ArchipeloClient.STATE_TIME, 0, equippedInventory, false, this.getWidth(), this.getHeight());
		super.draw(batch, parentAlpha);
	}
	
}
