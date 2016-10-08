package net.hollowbit.archipelo.screen.screens;

import java.util.Date;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.network.Packet;
import net.hollowbit.archipelo.network.PacketHandler;
import net.hollowbit.archipelo.network.PacketType;
import net.hollowbit.archipelo.network.packets.PlayerListPacket;
import net.hollowbit.archipelo.network.packets.PlayerPickPacket;
import net.hollowbit.archipelo.screen.Screen;
import net.hollowbit.archipelo.screen.ScreenType;
import net.hollowbit.archipelo.screen.screens.characterpicker.CharacterProfile;

public class CharacterPickerScreen extends Screen implements PacketHandler {
	
	Stage stage;
	Table characterTable;
	ScrollPane characterScrollPane;
	
	public CharacterPickerScreen () {
		super(ScreenType.CHARACTER_PICKER);
		stage = new Stage(ArchipeloClient.getGame().getCameraUi().getScreenViewport(), ArchipeloClient.getGame().getBatch());
		ArchipeloClient.getGame().getNetworkManager().addPacketHandler(this);
	}

	@Override
	public void create () {
		characterTable = new Table();
		characterScrollPane = new ScrollPane(characterTable);
		characterScrollPane.setBounds(0, 0, 500, 300);
		characterScrollPane.setPosition(Gdx.graphics.getWidth() / 2 - characterScrollPane.getWidth() / 2, Gdx.graphics.getHeight() / 2 - characterScrollPane.getHeight() / 2);
		stage.addActor(characterScrollPane);
	}

	@Override
	public void update (float deltaTime) {
		stage.act();
	}

	@Override
	public void render (SpriteBatch batch, float width, float height) {
		stage.draw();
	}

	@Override
	public void renderUi (SpriteBatch batch, float width, float height) {
		
	}

	@Override
	public void resize (int width, int height) {
		if (characterScrollPane != null)
			characterScrollPane.setPosition(Gdx.graphics.getWidth() / 2 - characterScrollPane.getWidth() / 2, Gdx.graphics.getHeight() / 2 - characterScrollPane.getHeight() / 2);
	}

	@Override
	public void dispose () {
		ArchipeloClient.getGame().getNetworkManager().removePacketHandler(this);
	}
	
	/**
	 * Reloads character list.
	 * @param playerListPacket
	 */
	@SuppressWarnings("deprecation")//Allow for deprecated methods because GWT only supports those ones.
	private void reloadList (PlayerListPacket playerListPacket) {
		characterTable.clear();
		
		//Add character profiles to table
		for (int i = 0; i < playerListPacket.names.length; i++) {
			Date lastPlayed = new Date((long) playerListPacket.lastPlayedDateTimes[i]);
			Date creation = new Date((long) playerListPacket.creationDateTimes[i]);
			characterTable.add(new CharacterProfile(playerListPacket.names[i], playerListPacket.playerEquippedInventories[i], playerListPacket.islands[i], lastPlayed.toLocaleString(), creation.toLocaleString(), playerListPacket.levels[i]));
		}
		
		//If user has another character slot available, add button to create a new one
		if (playerListPacket.names.length < ArchipeloClient.MAX_CHARACTERS_PER_PLAYER) {
			TextButton createNewButton = new TextButton("Create new...", ArchipeloClient.getGame().getUiSkin());
			createNewButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					//If button is clicked, open the player creator screen
					ArchipeloClient.getGame().getScreenManager().setScreen(new CharacterCreatorScreen());
					super.clicked(event, x, y);
				}
			});
			characterTable.add(createNewButton);
		}
		
		//Make scrollpane height match he height of the profiles inside
		characterScrollPane.setHeight(characterTable.getHeight());
	}
	
	@Override
	public boolean handlePacket (Packet packet) {
		switch (packet.packetType) {
		case PacketType.PLAYER_LIST:
			//Handle player list packets
			PlayerListPacket playerListPacket = (PlayerListPacket) packet;
			switch (playerListPacket.result) {
			case PlayerListPacket.RESULT_INVALID_LOGIN://If request was invalid, show error
				showErrorWindow("Login was invalid. Only pick characters belonging to your account.");
				break;
			case PlayerListPacket.RESULT_SUCCESSFUL:
				reloadList(playerListPacket);
				break;
			}
			return true;
		case PacketType.PLAYER_PICK:
			PlayerPickPacket playerPickPacket = (PlayerPickPacket) packet;
			switch (playerPickPacket.result) {
			case PlayerPickPacket.RESULT_ALREADY_LOGGED_IN:
				showErrorWindow("Character already logged in.");
				break;
			case PlayerPickPacket.RESULT_NO_PLAYER_WITH_NAME:
				showErrorWindow("No character with this name was found.");
				break;
			case PlayerPickPacket.RESULT_PLAYER_BELONGS_TO_ANOTHER_HBU:
				showErrorWindow("This character belongs to another user, not you!");
				break;
			case PlayerPickPacket.RESULT_SUCCESSFUL:
				ArchipeloClient.getGame().getScreenManager().setScreen(new GameScreen(playerPickPacket.name));
				break;
			}
			return true;
		}
		return false;
	}
	
	private void showErrorWindow (String error) {
		Dialog dialog = new Dialog("Pick Error", ArchipeloClient.getGame().getUiSkin(), "dialog") {
		    public void result(Object obj) {
		        remove();
		    }
		};
		dialog.text(error);
		dialog.button("Close", true);
		dialog.key(Keys.ENTER, true);
		dialog.key(Keys.ESCAPE, true);
		dialog.show(stage);
	}

}
