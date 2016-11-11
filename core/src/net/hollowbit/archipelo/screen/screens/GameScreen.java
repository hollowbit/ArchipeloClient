package net.hollowbit.archipelo.screen.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.network.Packet;
import net.hollowbit.archipelo.network.PacketHandler;
import net.hollowbit.archipelo.network.PacketType;
import net.hollowbit.archipelo.network.packets.ChatMessagePacket;
import net.hollowbit.archipelo.network.packets.ControlsPacket;
import net.hollowbit.archipelo.network.packets.LogoutPacket;
import net.hollowbit.archipelo.network.packets.NpcDialogPacket;
import net.hollowbit.archipelo.network.packets.PopupTextPacket;
import net.hollowbit.archipelo.screen.Screen;
import net.hollowbit.archipelo.screen.ScreenType;
import net.hollowbit.archipelo.screen.screens.gamescreen.ChatManager;
import net.hollowbit.archipelo.screen.screens.gamescreen.ChatMessage;
import net.hollowbit.archipelo.screen.screens.gamescreen.ChatWindow;
import net.hollowbit.archipelo.screen.screens.gamescreen.MainMenuWindow;
import net.hollowbit.archipelo.screen.screens.gamescreen.NpcDialogBox;
import net.hollowbit.archipelo.screen.screens.gamescreen.PopupTextManager;
import net.hollowbit.archipelo.tools.ControlsManager;
import net.hollowbit.archipelo.tools.FontManager.Fonts;
import net.hollowbit.archipelo.tools.FontManager.Sizes;
import net.hollowbit.archipelo.tools.QuickUi;
import net.hollowbit.archipelo.tools.QuickUi.IconType;
import net.hollowbit.archipelo.tools.QuickUi.TextFieldMessageListener;
import net.hollowbit.archipelo.tools.WorldSnapshotManager;
import net.hollowbit.archipelo.world.World;
import net.hollowbit.archipeloshared.Controls;

public class GameScreen extends Screen implements PacketHandler, InputProcessor {
	
	private static final int MENU_BUTTON_SIZE = 70;
	private static final int MENU_BUTTON_PADDING = 2;
	private static final int NPC_DIALOG_BOX_HEIGHT = 20;
	
	Stage stage;
	World world;
	WorldSnapshotManager worldSnapshotManager;
	ControlsManager controlsManager;
	PopupTextManager popupTextManager;
	ChatManager chatManager;
	NpcDialogBox npcDialogBox;
	
	boolean[] controls;
	
	//Ui Elements
	TextField chatTextField = null;
	ImageButton homeButton;
	ImageButton chatButton;
	
	MainMenuWindow mainMenuWndw;
	
	public GameScreen (String playerName) {
		super(ScreenType.GAME);
		ArchipeloClient.getGame().setPlayerName(playerName);
	}
	
	@Override
	public void create () {
		controlsManager = new ControlsManager(this);
		controls = new boolean[Controls.TOTAL];
		stage = new Stage(ArchipeloClient.getGame().getCameraUi().getScreenViewport(), ArchipeloClient.getGame().getBatch());
		InputMultiplexer inputMultiplexer = new InputMultiplexer(this, stage);
		Gdx.input.setInputProcessor(inputMultiplexer);
		world = new World(this);
		ArchipeloClient.getGame().setWorld(world);
		worldSnapshotManager = new WorldSnapshotManager(world);
		popupTextManager = new PopupTextManager();
		chatManager = new ChatManager();
		ArchipeloClient.getGame().getNetworkManager().addPacketHandler(this);
		ArchipeloClient.getGame().getCamera().zoom(1);
		
		//Add stage elements
		homeButton = QuickUi.getIconButton(IconType.HOME);
		homeButton.setBounds(0, 0, MENU_BUTTON_SIZE, MENU_BUTTON_SIZE);
		homeButton.setPosition(MENU_BUTTON_PADDING + (MENU_BUTTON_SIZE + MENU_BUTTON_PADDING) * 0, Gdx.graphics.getHeight() - homeButton.getHeight() - MENU_BUTTON_PADDING);
		homeButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				openMenu();
				super.clicked(event, x, y);
			}
		});
		stage.addActor(homeButton);
		
		chatButton = QuickUi.getIconButton(IconType.CHAT);
		chatButton.setBounds(0, 0, MENU_BUTTON_SIZE, MENU_BUTTON_SIZE);
		chatButton.setPosition(MENU_BUTTON_PADDING + (MENU_BUTTON_SIZE + MENU_BUTTON_PADDING) * 1, Gdx.graphics.getHeight() - homeButton.getHeight() - MENU_BUTTON_PADDING);
		chatButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				ChatWindow chatWindow = new ChatWindow(chatManager, stage, controlsManager);
				chatWindow.setPosition(Gdx.graphics.getWidth() / 2 - chatWindow.getWidth() / 2, Gdx.graphics.getHeight() / 2 - chatWindow.getHeight() / 2);
				stage.addActor(chatWindow);//Open a chat window
				
				super.clicked(event, x, y);
			}
		});
		stage.addActor(chatButton);
		
		//Chat box
		if (ArchipeloClient.IS_MOBILE) {
			chatTextField = new TextField("", ArchipeloClient.getGame().getUiSkin(), "chat");
			chatTextField.setBounds(0, 0, ChatMessage.WIDTH, 25);
			chatTextField.setPosition(2, 270);
		} else {
			chatTextField = new TextField("", ArchipeloClient.getGame().getUiSkin());
			chatTextField.setBounds(0, 0, ChatMessage.WIDTH, 40);
			chatTextField.setPosition(2, 2);
		}
		QuickUi.makeTextFieldMobileCompatible("Chat", chatTextField, stage, new TextFieldMessageListener() {
			
			@Override
			public void messageReceived (String message, boolean isEmpty) {
				if (!isEmpty) {
					chatManager.sendMessage(message);
					chatTextField.setText("");
				}
			}
		});
		chatTextField.setName("chatTextField");
		chatTextField.setMaxLength(140);//Like twitter!
		stage.addActor(chatTextField);
	}

	@Override
	public void update (float deltaTime) {
		if (!(stage.getKeyboardFocus() instanceof TextField)) {
			controlsManager.setFocused(true);
		} else {
			controlsManager.setFocused(false);
		}
		
		for (int i = 0; i < controls.length; i++) {
			controls[i] = controlsManager.getControls()[i];
		}
		
		if (controlsManager.areControlsUpdated()) {//If controls have changed, send changes to server.
			ArchipeloClient.getGame().getNetworkManager().sendPacket(new ControlsPacket(controlsManager.getControls()));
			controlsManager.resetControls();
		}
		
		if (canPlayerMove())
			controlsManager.update();
		worldSnapshotManager.update();
		popupTextManager.update(deltaTime);
		chatManager.update(deltaTime);
		world.update(deltaTime, controls);
		
		//Check if user is done using keyboard
		if (stage.getKeyboardFocus() == chatTextField) {
			if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
				stage.setKeyboardFocus(null);
			} else if (Gdx.input.isKeyJustPressed(Keys.ENTER)) {
				if (!chatTextField.getText().equals("") && !chatTextField.getText().equals(".") && !chatTextField.getText().equals("/") && !chatTextField.getText().equals("!")) {
					chatManager.sendMessage(chatTextField.getText());
					chatTextField.setText("");
					stage.setKeyboardFocus(null);
				}
			}
		}
		stage.act();
	}
	
	@Override
	public void render (SpriteBatch batch, float width, float height) {
		world.render(batch);
	}

	@Override
	public void dispose () {
		world.dispose();
		ArchipeloClient.getGame().getNetworkManager().removePacketHandler(this);
	}
	
	public World getWorld () {
		return world;
	}
	
	@Override
	public void renderUi(SpriteBatch batch, float width, float height) {
		if (ArchipeloClient.DEBUGMODE) {
			//Fps counter
			BitmapFont font = ArchipeloClient.getGame().getFontManager().getFont(Fonts.PIXELATED, Sizes.SMALL);
			GlyphLayout layoutFPS = new GlyphLayout(font, "FPS: " + Gdx.graphics.getFramesPerSecond());
			font.draw(batch, layoutFPS, width / 2 - layoutFPS.width / 2, height - layoutFPS.height);
			
			if (world.getPlayer() != null) {
				GlyphLayout layoutCoords = new GlyphLayout(font, "X: " + (int) world.getPlayer().getLocation().getX() + " Y: " + (int) world.getPlayer().getLocation().getY());
				font.draw(batch, layoutCoords, width / 2 - layoutCoords.width / 2, height - layoutFPS.height - 3 - layoutCoords.height);
			}
		}
		
		popupTextManager.render(batch);
		chatManager.render(batch, stage.getKeyboardFocus() == chatTextField);
		batch.end();
		stage.draw();
		batch.begin();
		world.renderUi(batch);
		controlsManager.render(batch);
	}

	@Override
	public boolean handlePacket (Packet packet) {
		switch (packet.packetType) {
		case PacketType.POPUP_TEXT:
			popupTextManager.addPopupTextWithPacket((PopupTextPacket) packet);
			return true;
		case PacketType.CHAT_MESSAGE:
			chatManager.addChatMessage((ChatMessagePacket) packet);
			return true;
		case PacketType.LOGOUT:
			LogoutPacket logoutPacket = (LogoutPacket) packet;
			if (logoutPacket.reason == LogoutPacket.REASON_KICK)
				ArchipeloClient.getGame().getScreenManager().setScreen(new MainMenuScreen("You were kicked from the server!\n" + logoutPacket.alt));
			else
				ArchipeloClient.getGame().getScreenManager().setScreen(new MainMenuScreen());
			return true;
		case PacketType.NPC_DIALOG:
			NpcDialogPacket npcDialogPacket = (NpcDialogPacket) packet;
			
			if (npcDialogBox != null)//If there is a box open, close it
				npcDialogBox.remove();
			
			String name = "";
			if (npcDialogPacket.usesId)
				name = ArchipeloClient.getGame().getLanguageSpecificMessageManager().getNpcDialogById(npcDialogPacket.prefix, npcDialogPacket.name).name;
			else
				name = npcDialogPacket.name;
			
			stopPlayerMovement();
			npcDialogBox = new NpcDialogBox(name, npcDialogPacket);
			npcDialogBox.setPosition(Gdx.graphics.getWidth() / 2 - npcDialogBox.getWidth() / 2, NPC_DIALOG_BOX_HEIGHT);
			stage.addActor(npcDialogBox);
			return true;
		}
		return false;
	}
	
	@Override
	public void resize(int width, int height) {
		if (mainMenuWndw != null)
			mainMenuWndw.setPosition(width / 2 - mainMenuWndw.getWidth() / 2, height / 2 - mainMenuWndw.getHeight() / 2);
		controlsManager.resize(width, height);
		
		//Update positions of menu buttons on resize
		homeButton.setPosition(MENU_BUTTON_PADDING + (MENU_BUTTON_SIZE + MENU_BUTTON_PADDING) * 0, Gdx.graphics.getHeight() - homeButton.getHeight() - MENU_BUTTON_PADDING);
		chatButton.setPosition(MENU_BUTTON_PADDING + (MENU_BUTTON_SIZE + MENU_BUTTON_PADDING) * 1, Gdx.graphics.getHeight() - homeButton.getHeight() - MENU_BUTTON_PADDING);
		
		if (npcDialogBox != null)
			npcDialogBox.setPosition(Gdx.graphics.getWidth() / 2 - npcDialogBox.getWidth() / 2, NPC_DIALOG_BOX_HEIGHT);
	}
	
	public PopupTextManager getPopupTextManager () {
		return popupTextManager;
	}

	@Override
	public boolean keyDown (int keycode) { 
		if (stage.getKeyboardFocus() == null) {
			if (keycode == Keys.ENTER || keycode == Keys.T) {
				stage.setKeyboardFocus(chatTextField);
				return true;
			} else if (keycode == Keys.SLASH) {
				stage.setKeyboardFocus(chatTextField);
				chatTextField.setText("/");
				chatTextField.setCursorPosition(1);
				return true;
			} else if (keycode == Keys.NUM_1 && (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT))) {
				stage.setKeyboardFocus(chatTextField);
				chatTextField.setText("!");
				chatTextField.setCursorPosition(1);
				return true;
			} else if (keycode == Keys.PERIOD) {
				stage.setKeyboardFocus(chatTextField);
				chatTextField.setText(".");
				chatTextField.setCursorPosition(1);
				return true;
			}
		}
		
		if (keycode == Keys.ESCAPE) {
			if (openMenu())
				return true;
		}
			
		return false;
	}
	
	//Returns true if successful
	private boolean openMenu () {
		if (mainMenuWndw == null || mainMenuWndw.getStage() == null) {
			mainMenuWndw = new MainMenuWindow(this, stage);
			mainMenuWndw.setPosition(stage.getWidth() / 2 - mainMenuWndw.getWidth() / 2, stage.getHeight() / 2 - mainMenuWndw.getHeight() / 2);
			stage.addActor(mainMenuWndw);
			return true;
		}
		return false;
	}
	
	private void stopPlayerMovement () {
		ArchipeloClient.getGame().getWorld().getPlayer().stopMovement();
		controlsManager.stopMovement();
	}
	
	public boolean canPlayerMove () {
		if (npcDialogBox == null || !stage.getActors().contains(npcDialogBox, true))
			return true;
		else
			return npcDialogBox.isInterruptable();
	}
	
	public void playerMoved () {
		if (npcDialogBox != null)
			npcDialogBox.remove();
	}
	
	@Override
	public boolean keyUp(int keycode) {return false;}

	@Override
	public boolean keyTyped(char character) { return false;}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) { 
		stage.setKeyboardFocus(null);
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false;}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) { return false;}

	@Override
	public boolean mouseMoved(int screenX, int screenY) { return false;}

	@Override
	public boolean scrolled(int amount) { return false;}

}
