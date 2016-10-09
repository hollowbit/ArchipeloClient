package net.hollowbit.archipelo.screen.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.network.Packet;
import net.hollowbit.archipelo.network.PacketHandler;
import net.hollowbit.archipelo.network.PacketType;
import net.hollowbit.archipelo.network.packets.PlayerListPacket;
import net.hollowbit.archipelo.network.packets.PlayerPickPacket;
import net.hollowbit.archipelo.screen.Screen;
import net.hollowbit.archipelo.screen.ScreenType;
import net.hollowbit.archipelo.screen.screens.characterpicker.CharacterPickWindow;

public class CharacterPickerScreen extends Screen implements PacketHandler {
	
	Stage stage;
	CharacterPickWindow characterPickWindow;
	
	public CharacterPickerScreen () {
		super(ScreenType.CHARACTER_PICKER);
		stage = new Stage(ArchipeloClient.getGame().getCameraUi().getScreenViewport(), ArchipeloClient.getGame().getBatch());
		Gdx.input.setInputProcessor(stage);
		ArchipeloClient.getGame().getNetworkManager().addPacketHandler(this);
	}

	@Override
	public void create () {
		characterPickWindow = new CharacterPickWindow();
		characterPickWindow.setPosition(Gdx.graphics.getWidth() / 2 - characterPickWindow.getWidth() / 2, Gdx.graphics.getHeight() / 2 - characterPickWindow.getHeight() / 2);
		stage.addActor(characterPickWindow);
		
		//Send character list packet
		new PlayerListPacket(ArchipeloClient.getGame().getPrefs().getUsername()).send();
	}

	@Override
	public void update (float deltaTime) {
		stage.act();
	}

	@Override
	public void render (SpriteBatch batch, float width, float height) {
		
	}

	@Override
	public void renderUi (SpriteBatch batch, float width, float height) {
		batch.end();
		stage.draw();
		batch.begin();
	}

	@Override
	public void resize (int width, int height) {
		characterPickWindow.setPosition(Gdx.graphics.getWidth() / 2 - characterPickWindow.getWidth() / 2, Gdx.graphics.getHeight() / 2 - characterPickWindow.getHeight() / 2);
	}

	@Override
	public void dispose () {
		ArchipeloClient.getGame().getNetworkManager().removePacketHandler(this);
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
				characterPickWindow.reloadList(playerListPacket);
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
