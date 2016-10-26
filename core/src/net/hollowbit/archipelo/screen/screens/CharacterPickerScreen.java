package net.hollowbit.archipelo.screen.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.network.Packet;
import net.hollowbit.archipelo.network.PacketHandler;
import net.hollowbit.archipelo.network.PacketType;
import net.hollowbit.archipelo.network.packets.PlayerListPacket;
import net.hollowbit.archipelo.network.packets.PlayerPickPacket;
import net.hollowbit.archipelo.screen.Screen;
import net.hollowbit.archipelo.screen.ScreenType;
import net.hollowbit.archipelo.screen.screens.characterpicker.CharacterPickWindow;
import net.hollowbit.archipelo.screen.screens.mainmenu.ScrollingBackground;
import net.hollowbit.archipelo.tools.QuickUi;
import net.hollowbit.archipelo.tools.QuickUi.IconType;

public class CharacterPickerScreen extends Screen implements PacketHandler {
	
	Stage stage;
	CharacterPickWindow characterPickWindow;
	ScrollingBackground scrollingBackground;
	ImageButton backButton;
	
	public CharacterPickerScreen () {
		super(ScreenType.CHARACTER_PICKER);
		stage = new Stage(ArchipeloClient.getGame().getCameraUi().getScreenViewport(), ArchipeloClient.getGame().getBatch());
		Gdx.input.setInputProcessor(stage);
		ArchipeloClient.getGame().getNetworkManager().addPacketHandler(this);
		scrollingBackground = new ScrollingBackground();
	}

	@Override
	public void create () {
		characterPickWindow = new CharacterPickWindow();
		characterPickWindow.setPosition(Gdx.graphics.getWidth() / 2 - characterPickWindow.getWidth() / 2, Gdx.graphics.getHeight() / 2 - characterPickWindow.getHeight() / 2);
		stage.addActor(characterPickWindow);
		
		backButton = QuickUi.getIconButton(IconType.BACK);
		backButton.addListener(new ClickListener () {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				ArchipeloClient.getGame().getScreenManager().setScreen(new MainMenuScreen());
			}
		});
		backButton.setPosition(5, Gdx.graphics.getHeight() - QuickUi.ICON_SIZE - 5);
		stage.addActor(backButton);
		
		//Send character list packet
		new PlayerListPacket(ArchipeloClient.getGame().getPrefs().getEmail()).send();
	}

	@Override
	public void update (float deltaTime) {
		stage.act();
		scrollingBackground.update(deltaTime);
	}

	@Override
	public void render (SpriteBatch batch, float width, float height) {
		scrollingBackground.render(batch);
	}

	@Override
	public void renderUi (SpriteBatch batch, float width, float height) {
		batch.end();
		stage.draw();
		batch.begin();
	}

	@Override
	public void resize (int width, int height) {
		scrollingBackground.resize();
		backButton.setPosition(5, Gdx.graphics.getHeight() - QuickUi.ICON_SIZE - 5);
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
				QuickUi.showErrorWindow("Invalid Login", "Login was invalid. Only pick characters belonging to your account.", stage);
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
				QuickUi.showErrorWindow("Character Taken", "This character is already logged in.", stage);
				break;
			case PlayerPickPacket.RESULT_NO_PLAYER_WITH_NAME:
				QuickUi.showErrorWindow("Character Not Found", "No character with this name was found.", stage);
				break;
			case PlayerPickPacket.RESULT_PLAYER_BELONGS_TO_ANOTHER_HBU:
				QuickUi.showErrorWindow("Wrong Login", "This character belongs to another user, not you!", stage);
				break;
			case PlayerPickPacket.RESULT_SUCCESSFUL:
				ArchipeloClient.getGame().getScreenManager().setScreen(new GameScreen(playerPickPacket.name));
				break;
			}
			return true;
		}
		return false;
	}

}
