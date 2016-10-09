package net.hollowbit.archipelo.screen.screens.characterpicker;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.items.Item;
import net.hollowbit.archipelo.network.packets.PlayerPickPacket;
import net.hollowbit.archipelo.screen.screens.mainmenu.CharacterDisplay;

public class CharacterProfile extends Table {
	
	private static final int DISPLAY_SIZE = 200;
	
	//Ui elements
	Label nameLabel;
	Label islandLabel;
	Label lastPlayedLabel;
	Label creationDateLabel;
	Label levelLabel;
	TextButton pickButton;
	CharacterDisplay characterDisplay;
	
	/**
	 * Used in character picker to show different players
	 */
	public CharacterProfile (final String name, Item[] equippedInventory, String island, String lastPlayedDateTime, String creationDateTime, int level) {
		this.setSkin(ArchipeloClient.getGame().getUiSkin());
		
		characterDisplay = new CharacterDisplay(equippedInventory, true);
		add(characterDisplay).width(DISPLAY_SIZE).height(DISPLAY_SIZE);
		
		row();
		
		nameLabel = new Label(name, getSkin());
		add(nameLabel).padTop(10);
		
		row();
		
		islandLabel = new Label("Location: " + island, getSkin(), "small");
		add(islandLabel).left().padTop(10);
		
		row();
		
		lastPlayedLabel = new Label("Last Played:\n" + lastPlayedDateTime, getSkin(), "small");
		add(lastPlayedLabel).left().padTop(10);
		
		row();
		
		creationDateLabel = new Label("Created:\n" + creationDateTime, getSkin(), "small");
		add(creationDateLabel).left().padTop(10);
		
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
		add(pickButton).padTop(10);
	}
	
}
