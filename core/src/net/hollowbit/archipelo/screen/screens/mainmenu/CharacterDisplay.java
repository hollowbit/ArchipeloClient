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
	
	private static final float SCALE_FACTOR = 3;
	
	int direction = 0;
	Item[] equippedInventory;
	
	/**
	 * Show the appearance of a character
	 * @param equippedInventory
	 */
	public CharacterDisplay (Item[] equippedInventory, boolean allowDirectionChange) {
		this.equippedInventory = equippedInventory;
		
		if (allowDirectionChange) {
			this.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					//When clicked, change direction
					direction++;
					if (direction >= Direction.TOTAL)
						direction = 0;
					super.clicked(event, x, y);
				}
			});
		}
	}
	
	public void setEquippedInventory (Item[] equippedInventory) {
		this.equippedInventory = equippedInventory;
	}
	
	@Override
	public void draw (Batch batch, float parentAlpha) {
		Player.drawPlayer(batch, Direction.IN_A_ROW[direction], true, false, this.getX() + (this.getWidth() / 2 - this.getDisplayWidth() / 2), this.getY() + (this.getHeight() / 2 - this.getDisplayHeight() / 2), ArchipeloClient.STATE_TIME, 0, equippedInventory, false, this.getDisplayWidth(), this.getDisplayHeight());
		super.draw(batch, parentAlpha);
	}
	
	private float getDisplayWidth () {
		return this.getWidth() * SCALE_FACTOR;
	}
	
	private float getDisplayHeight () {
		return this.getHeight() * SCALE_FACTOR;
	}
	
}
