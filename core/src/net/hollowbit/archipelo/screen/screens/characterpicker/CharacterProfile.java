package net.hollowbit.archipelo.screen.screens.characterpicker;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.items.Item;
import net.hollowbit.archipelo.network.packets.PlayerDeletePacket;
import net.hollowbit.archipelo.network.packets.PlayerPickPacket;
import net.hollowbit.archipelo.screen.screens.mainmenu.CharacterDisplay;
import net.hollowbit.archipelo.tools.LM;
import net.hollowbit.archipelo.tools.QuickUi;

public class CharacterProfile extends Table {
	
	private static final int DISPLAY_SIZE = 256;
	
	//Ui elements
	String name;
	Label nameLabel;
	Label islandLabel;
	Label lastPlayedLabel;
	Label creationDateLabel;
	Label levelLabel;
	TextButton pickButton;
	TextButton deleteButton;
	CharacterDisplay characterDisplay;
	CharacterPickWindow window;
	
	/**
	 * Used in character picker to show different players
	 */
	public CharacterProfile (Stage stage, CharacterPickWindow window, final String name, Item[] equippedInventory, String island, String lastPlayedDateTime, String creationDateTime, int level) {
		this.setStage(stage);
		this.window = window;
		this.setSkin(ArchipeloClient.getGame().getUiSkin());
		this.name = name;
		
		characterDisplay = new CharacterDisplay(equippedInventory, true);
		add(characterDisplay).width(DISPLAY_SIZE).height(DISPLAY_SIZE);
		
		row();
		
		nameLabel = new Label(name, getSkin());
		add(nameLabel).padTop(10);
		
		row();
		
		islandLabel = new Label(LM.ui("location") + ": " + island, getSkin(), "small");
		add(islandLabel).left().padTop(10);
		
		row();
		
		lastPlayedLabel = new Label(LM.ui("lastPlayed") + ":\n" + lastPlayedDateTime, getSkin(), "small");
		add(lastPlayedLabel).left().padTop(10);
		
		row();
		
		creationDateLabel = new Label(LM.ui("created") + ":\n" + creationDateTime, getSkin(), "small");
		add(creationDateLabel).left().padTop(10);
		
		row();
		
		pickButton = new TextButton(LM.ui("pick"), getSkin());
		pickButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				new PlayerPickPacket(name).send();//When this button is clicked, send a player pick packet to server
				super.clicked(event, x, y);
			}
		});
		add(pickButton).padTop(10);
		
		row();
		
		pack();//Pack before adding delete button so it is hidden
		
		deleteButton = new TextButton(LM.ui("delete"), getSkin());
		deleteButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				showConfirmWindow();
				super.clicked(event, x, y);
			}
		});
		
		add(deleteButton).padTop(30);
	}
	
	public void showConfirmWindow () {
		final CharacterProfile charProfile = this;
		Dialog dialog = new Dialog(LM.ui("deletePlayerTitle"), ArchipeloClient.getGame().getUiSkin(), "dialog") {
		    public void result(Object obj) {
		    	boolean delete = (Boolean) obj;
		    	
		    	if (delete) {//If confirmed, delete player
		    		new PlayerDeletePacket(name).send();//Delete player
		    		charProfile.remove();
		    		window.updateAfterCharacterRemoved();
		    	}
		    	
		        remove();
		    }
		};
		
		Label label = new Label(LM.ui("deletePlayerWarning"), ArchipeloClient.getGame().getUiSkin());
		label.setWrap(true);
		label.setAlignment(Align.center);
		dialog.getContentTable().add(label).width(QuickUi.ERROR_DIALOG_WRAP_WIDTH);
		
		QuickUi.addCloseButtonToWindow(dialog);
		
		dialog.button(LM.ui("yes"), true);
		dialog.button(LM.ui("no"), false);
		dialog.key(Keys.ENTER, false);
		dialog.key(Keys.ESCAPE, false);
		dialog.show(getStage());
	}
	
}
