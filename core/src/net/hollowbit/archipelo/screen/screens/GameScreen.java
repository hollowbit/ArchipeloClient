package net.hollowbit.archipelo.screen.screens;

import java.util.HashMap;

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
import net.hollowbit.archipelo.form.Form;
import net.hollowbit.archipelo.form.FormType;
import net.hollowbit.archipelo.network.Packet;
import net.hollowbit.archipelo.network.PacketHandler;
import net.hollowbit.archipelo.network.PacketType;
import net.hollowbit.archipelo.network.packets.ChatMessagePacket;
import net.hollowbit.archipelo.network.packets.FormDataPacket;
import net.hollowbit.archipelo.network.packets.FormRequestPacket;
import net.hollowbit.archipelo.network.packets.LogoutPacket;
import net.hollowbit.archipelo.network.packets.NpcDialogPacket;
import net.hollowbit.archipelo.network.packets.PopupTextPacket;
import net.hollowbit.archipelo.screen.Screen;
import net.hollowbit.archipelo.screen.ScreenType;
import net.hollowbit.archipelo.screen.screens.gamescreen.ChatManager;
import net.hollowbit.archipelo.screen.screens.gamescreen.HealthBar;
import net.hollowbit.archipelo.screen.screens.gamescreen.popup.PopupTextManager;
import net.hollowbit.archipelo.screen.screens.gamescreen.windows.ChatMessage;
import net.hollowbit.archipelo.screen.screens.gamescreen.windows.ChatWindow;
import net.hollowbit.archipelo.screen.screens.gamescreen.windows.MainMenuWindow;
import net.hollowbit.archipelo.screen.screens.gamescreen.windows.NpcDialogBox;
import net.hollowbit.archipelo.tools.ControlsManager;
import net.hollowbit.archipelo.tools.FontManager.Fonts;
import net.hollowbit.archipelo.tools.FontManager.Sizes;
import net.hollowbit.archipelo.tools.QuickUi;
import net.hollowbit.archipelo.tools.QuickUi.IconType;
import net.hollowbit.archipelo.tools.QuickUi.TextFieldMessageListener;
import net.hollowbit.archipelo.tools.WorldSnapshotManager;
import net.hollowbit.archipelo.world.World;

public class GameScreen extends Screen implements PacketHandler, InputProcessor {
	
	private static final int MENU_BUTTON_SIZE = 70;
	private static final int MENU_BUTTON_PADDING = 2;
	public static final int NPC_DIALOG_BOX_HEIGHT = 20;
	
	Stage stage;
	World world;
	WorldSnapshotManager worldSnapshotManager;
	ControlsManager controlsManager;
	PopupTextManager popupTextManager;
	ChatManager chatManager;
	NpcDialogBox npcDialogBox;
	HealthBar healthBar;
	
	//Ui Elements
	TextField chatTextField = null;
	ImageButton homeButton;
	ImageButton chatButton;
	ImageButton inventoryButton;
	
	MainMenuWindow mainMenuWndw;
	
	HashMap<String, Form> forms;
	
	public GameScreen (String playerName) {
		super(ScreenType.GAME);
		ArchipeloClient.getGame().getPlayerInfoManager().setName(playerName);
	}
	
	@Override
	public void create () {
		ArchipeloClient.getGame().getMusicManager().stop("title-screen");
		
		controlsManager = new ControlsManager(this);
		stage = new Stage(ArchipeloClient.getGame().getCameraUi().getScreenViewport(), ArchipeloClient.getGame().getBatch());
		InputMultiplexer inputMultiplexer = new InputMultiplexer(this, stage);
		Gdx.input.setInputProcessor(inputMultiplexer);
		world = new World(this);
		ArchipeloClient.getGame().setWorld(world);
		worldSnapshotManager = new WorldSnapshotManager(world);
		popupTextManager = new PopupTextManager();
		chatManager = new ChatManager();
		forms = new HashMap<String, Form>();
		ArchipeloClient.getGame().getNetworkManager().addPacketHandler(this);
		ArchipeloClient.getGame().getCamera().zoom(1);
		
		healthBar = new HealthBar(world);
		
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
				openChatWindow();
				super.clicked(event, x, y);
			}
		});
		stage.addActor(chatButton);
		
		inventoryButton = QuickUi.getIconButton(IconType.INVENTORY);
		inventoryButton.setBounds(0, 0, MENU_BUTTON_SIZE, MENU_BUTTON_SIZE);
		inventoryButton.setPosition(MENU_BUTTON_PADDING + (MENU_BUTTON_SIZE + MENU_BUTTON_PADDING) * 2, Gdx.graphics.getHeight() - homeButton.getHeight() - MENU_BUTTON_PADDING);
		inventoryButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				ArchipeloClient.getGame().getNetworkManager().sendPacket(new FormRequestPacket("inventory", new HashMap<String, String>()));
				super.clicked(event, x, y);
			}
		});
		stage.addActor(inventoryButton);
		
		//Chat box
		if (ArchipeloClient.IS_MOBILE) {
			chatTextField = new TextField("", ArchipeloClient.getGame().getUiSkin(), "chat");
			chatTextField.setBounds(0, 0, ChatMessage.WIDTH, 25);
			chatTextField.setPosition(2, 270);
		} else {
			chatTextField = new TextField("", ArchipeloClient.getGame().getUiSkin());
			chatTextField.setBounds(0, 0, ChatMessage.WIDTH, 45);
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
		
		controlsManager.update(isNpcDialogBoxOpen(), deltaTime, canPlayerMove());
		
		worldSnapshotManager.update(deltaTime);
		popupTextManager.update(deltaTime);
		chatManager.update(deltaTime);
		world.update(deltaTime);
		
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
		controlsManager.dispose();
		worldSnapshotManager.dispose();
		world.dispose();
		ArchipeloClient.getGame().getNetworkManager().removePacketHandler(this);
	}
	
	@Override
	public void pause() {
		controlsManager.stopMovement();
		controlsManager.forceUpdate();
		super.pause();
	}
	
	public World getWorld () {
		return world;
	}
	
	@Override
	public void renderUi(SpriteBatch batch, float width, float height) {
		if (ArchipeloClient.DEBUGMODE) {
			//Fps counter
			BitmapFont font = ArchipeloClient.getGame().getFontManager().getFont(Fonts.PIXELATED, Sizes.VERY_SMALL);
			GlyphLayout layoutFPS = new GlyphLayout(font, "FPS: " + Gdx.graphics.getFramesPerSecond());
			font.draw(batch, layoutFPS, width / 2 - layoutFPS.width / 2, height - layoutFPS.height);
			
			GlyphLayout layoutPing = new GlyphLayout(font, "Ping: " + ArchipeloClient.getGame().getNetworkManager().getPing());
			font.draw(batch, layoutPing, width / 2 - layoutPing.width / 2, height - layoutPing.height - layoutFPS.height - 3);
			
			if (world.getPlayer() != null) {
				GlyphLayout layoutCoords = new GlyphLayout(font, "X: " + (int) world.getPlayer().getLocation().getX() + " Y: " + (int) world.getPlayer().getLocation().getY());
				font.draw(batch, layoutCoords, width / 2 - layoutCoords.width / 2, height - layoutFPS.height - 3 - layoutPing.height - 3 - layoutCoords.height);
			}
		}
		
		popupTextManager.render(batch);
		chatManager.render(batch, stage.getKeyboardFocus() == chatTextField);
		healthBar.render(batch);
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
			
			//Don't open blank NPC dialogs. This could occur if a player choice ends the conversation, a blank response could be sent back to close dialog
			if (npcDialogPacket.usesId && (npcDialogPacket.name == null || npcDialogPacket.name.equals("")))
				return true;
			
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
		case PacketType.FORM_DATA:
			FormDataPacket formDataPacket = (FormDataPacket) packet;
			if (forms.containsKey(formDataPacket.data.id))
				forms.get(formDataPacket.data.id).update(formDataPacket.data);
			else {
				Form form = FormType.createFormByFormData(formDataPacket.data, this);
				forms.put(formDataPacket.data.id, form);
				form.setPosition(Gdx.graphics.getWidth() / 2 - form.getWidth() * form.getScaleX() / 2, Gdx.graphics.getHeight() / 2 - form.getHeight() * form.getScaleY() / 2);
				stage.addActor(form);
			}
			return true;
		}
		return false;
	}
	
	public boolean isNpcDialogBoxOpen () {
		return npcDialogBox != null && stage.getActors().contains(npcDialogBox, true);
	}
	
	@Override
	public void resize(int width, int height) {
		if (mainMenuWndw != null)
			mainMenuWndw.centerOnScreen();
		controlsManager.resize(width, height);
		
		//Update positions of menu buttons on resize
		homeButton.setPosition(MENU_BUTTON_PADDING + (MENU_BUTTON_SIZE + MENU_BUTTON_PADDING) * 0, Gdx.graphics.getHeight() - homeButton.getHeight() - MENU_BUTTON_PADDING);
		chatButton.setPosition(MENU_BUTTON_PADDING + (MENU_BUTTON_SIZE + MENU_BUTTON_PADDING) * 1, Gdx.graphics.getHeight() - homeButton.getHeight() - MENU_BUTTON_PADDING);
		inventoryButton.setPosition(MENU_BUTTON_PADDING + (MENU_BUTTON_SIZE + MENU_BUTTON_PADDING) * 2, Gdx.graphics.getHeight() - homeButton.getHeight() - MENU_BUTTON_PADDING);
		
		if (npcDialogBox != null)
			npcDialogBox.setPosition(Gdx.graphics.getWidth() / 2 - npcDialogBox.getWidth() / 2, NPC_DIALOG_BOX_HEIGHT);
	}
	
	public PopupTextManager getPopupTextManager () {
		return popupTextManager;
	}
	
	public ControlsManager getControlsManager() {
		return controlsManager;
	}

	@Override
	public boolean keyDown (int keycode) { 
		if (stage.getKeyboardFocus() == null) {
			if (keycode == Keys.ENTER || keycode == Keys.T) {
				stage.setKeyboardFocus(chatTextField);
				return true;
			} else if (chatTextField.getText().equals("") && (keycode == Keys.SLASH || (keycode == Keys.NUM_1 && (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT))) || keycode == Keys.PERIOD)) {
				stage.setKeyboardFocus(chatTextField);
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
			mainMenuWndw.centerOnScreen();
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
	
	public void removeForm (Form form) {
		this.forms.remove(form.getId());
	}
	
	public void openChatWindow () {
		ChatWindow chatWindow = new ChatWindow(chatManager, stage, controlsManager);
		chatWindow.centerOnScreen();
		stage.addActor(chatWindow);//Open a chat window
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
