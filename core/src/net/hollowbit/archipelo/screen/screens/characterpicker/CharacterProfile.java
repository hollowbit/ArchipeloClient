package net.hollowbit.archipelo.screen.screens.characterpicker;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.entity.living.Player;
import net.hollowbit.archipelo.items.Item;
import net.hollowbit.archipelo.network.packets.PlayerPickPacket;
import net.hollowbit.archipeloshared.Direction;

public class CharacterProfile extends Table {
	
	private static final int DIRECTION_CHANGE_BUTTON_SIZE = 200;
	
	private Item[] equippedInventory;
	
	//Ui elements
	Button directionChangeButton;
	Label nameLabel;
	Label islandLabel;
	Label lastPlayedLabel;
	Label creationDateLabel;
	Label levelLabel;
	TextButton pickButton;
	
	int direction = Direction.DOWN.ordinal();
	
	/**
	 * Used in character picker to show different players
	 */
	public CharacterProfile (final String name, Item[] equippedInventory, String island, String lastPlayedDateTime, String creationDateTime, int level) {
		this.equippedInventory = equippedInventory;
		this.setSkin(ArchipeloClient.getGame().getUiSkin());
		
		directionChangeButton = new Button(getSkin());
		directionChangeButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				direction++;
				if (direction >= Direction.TOTAL)
					direction = 0;
				super.clicked(event, x, y);
			}
		});
		add(directionChangeButton).width(DIRECTION_CHANGE_BUTTON_SIZE).height(DIRECTION_CHANGE_BUTTON_SIZE);
		
		row();
		
		nameLabel = new Label(name, getSkin());
		add(nameLabel);
		
		row();
		
		islandLabel = new Label("Location: " + island, getSkin(), "small");
		add(islandLabel).left();
		
		row();
		
		lastPlayedLabel = new Label("Last Played: " + lastPlayedDateTime, getSkin(), "small");
		add(lastPlayedLabel).left();
		
		row();
		
		creationDateLabel = new Label("Created: " + creationDateTime, getSkin(), "small");
		add(creationDateLabel).left();
		
		row();
		
		pickButton = new TextButton("Pick!", getSkin());
		pickButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				//When this button is clicked, send a player pick packet to server
				new PlayerPickPacket(name).send();
				super.clicked(event, x, y);
			}
		});
		add(pickButton);
	}
	
	@Override
	public void draw (Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		
		//Draw player at button location
		Player.drawPlayer(batch, Direction.values()[direction], true, false, directionChangeButton.getX(), directionChangeButton.getY(), ArchipeloClient.STATE_TIME, 0f, equippedInventory, false, DIRECTION_CHANGE_BUTTON_SIZE, DIRECTION_CHANGE_BUTTON_SIZE);
	}
	
}
