package net.hollowbit.archipelo.screen.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.hollowbitserver.HollowBitServerConnectivity;
import net.hollowbit.archipelo.hollowbitserver.HollowBitServerQueryResponseHandler;
import net.hollowbit.archipelo.network.Packet;
import net.hollowbit.archipelo.network.PacketHandler;
import net.hollowbit.archipelo.network.PacketType;
import net.hollowbit.archipelo.network.packets.LoginPacket;
import net.hollowbit.archipelo.network.packets.LogoutPacket;
import net.hollowbit.archipelo.screen.Screen;
import net.hollowbit.archipelo.screen.ScreenType;
import net.hollowbit.archipelo.screen.screens.mainmenu.LoginRegisterWindow;
import net.hollowbit.archipelo.screen.screens.mainmenu.LoginWindow;
import net.hollowbit.archipelo.screen.screens.mainmenu.RegisterWindow;
import net.hollowbit.archipelo.screen.screens.mainmenu.ScrollingBackground;
import net.hollowbit.archipelo.screen.screens.mainmenu.ServerPickerWindow;
import net.hollowbit.archipelo.tools.FontManager.Fonts;
import net.hollowbit.archipelo.tools.FontManager.Sizes;
import net.hollowbit.archipelo.tools.LanguageSpecificMessageManager.Cat;
import net.hollowbit.archipelo.tools.LM;
import net.hollowbit.archipelo.tools.Prefs;
import net.hollowbit.archipelo.tools.QuickUi;

public class MainMenuScreen extends Screen {
	
	private static final float LOGO_SPEED = 450;
	private int LOGO_PROGRESSION_ZERO_Y = 150;
	private int LOGO_PROGRESSION_ONE_Y = 15;
	
	private static final float FLASH_TIME = 0.8f;
	
	private int progression;//0 = press start. 1 = start menu. 2 = menu started
	
	//Ui
	Stage stage;
	TextButton playBtn;
	TextButton changeServerBtn;
	TextButton logoutBtn;
	TextButton exitBtn;
	
	LoginWindow loginWndw;
	RegisterWindow registerWndw;
	LoginRegisterWindow loginRegisterWndw;
	ServerPickerWindow serverPickerWndw;
	
	Texture logo;
	
	float logoY;
	
	float flashTimer;
	boolean flashOn;
	
	GlyphLayout pressAnyGlyphLayout;
	
	Prefs prefs;
	
	ScrollingBackground scrollingBackground;
	
	String error;
	
	public MainMenuScreen () {
		super(ScreenType.MAIN_MENU);
	}
	
	public MainMenuScreen (String error) {
		this();
		this.error = error;
	}

	@Override
	public void create () {
		progression = 0;
		stage = new Stage(ArchipeloClient.getGame().getCameraUi().getScreenViewport(), ArchipeloClient.getGame().getBatch());
		logo = ArchipeloClient.getGame().getAssetManager().getTexture("logo");
		scrollingBackground  = new ScrollingBackground();
		
		pressAnyGlyphLayout = new GlyphLayout(ArchipeloClient.getGame().getFontManager().getFont(Fonts.PIXELATED, Sizes.MEDIUM), ArchipeloClient.IS_MOBILE ? LM.getMsg(Cat.UI, "tapStart") : LM.getMsg(Cat.UI, "pressAnyStart"));
		flashOn = true;
		InputMultiplexer inputMultiplexer = new InputMultiplexer(stage, new InputAdapter() {
			
			@Override
			public boolean keyDown(int keycode) {
				if (progression == 0)
					progression = 1;
				return false;
			}
			
			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {
				if (progression == 0)
					progression = 1;
				return false;
			}
			
		});
		Gdx.input.setInputProcessor(inputMultiplexer);
		LOGO_PROGRESSION_ONE_Y += logo.getHeight();
		LOGO_PROGRESSION_ZERO_Y += logo.getHeight();
		logoY = 0;
		
		prefs = ArchipeloClient.getGame().getPrefs();
		
		//Show disclaimer message on first time opening app
		if (!ArchipeloClient.DEBUGMODE && !prefs.hasShownDisclaimer()) {
			showDisclaimerWindow();
			prefs.setShowedDisclaimer();
		}
		
		//If login info was saved, verify it.
		if (prefs.isLoggedIn()) {
			ArchipeloClient.getGame().getHollowBitServerConnectivity().sendVerifyQuery(prefs.getEmail(), prefs.getPassword(), new HollowBitServerQueryResponseHandler() {
				
				@Override
				public void responseReceived(int id, String[] data) {
					if (id != HollowBitServerConnectivity.CORRECT_LOGIN_RESPONSE_PACKET_ID)//Correct login.
						prefs.resetLogin();//Reset info to let the game know that there is no login credentials at the moment
				}
			});
		}
		
		//If there is a server saved, try to connect to it, if possible.
		if (prefs.isServerPicked()) {
			ArchipeloClient.getGame().getHollowBitServerConnectivity().sendGetServerByNameQuery(prefs.getServerName(), new HollowBitServerQueryResponseHandler() {
				
				@Override
				public void responseReceived(int id, String[] data) {
					if (id == HollowBitServerConnectivity.SERVER_GET_RESPONSE_PACKET_ID) {//If we found the server we last connected to, try to connect to it again.
						String hostname = data[0];
						ArchipeloClient.getGame().getNetworkManager().connect(hostname, ArchipeloClient.PORT);
					} else
						prefs.resetServer();
				}
			});
		}
		
		//If there is an error, show it
		if (error != null)
			QuickUi.showErrorWindow("!?!?!", error, stage);
		
		ArchipeloClient.getGame().getMusicManager().play("title-screen");
	}

	@Override
	public void update (float deltaTime) {
		stage.act();
		scrollingBackground.update(deltaTime);
		
		if (progression == 0) {
			//Update flash button
			flashTimer += deltaTime;
			if (flashTimer >= FLASH_TIME) {
				flashTimer -= FLASH_TIME;
				flashOn = !flashOn;
			}
			
			if (logoY < LOGO_PROGRESSION_ZERO_Y) {
				logoY += LOGO_SPEED * deltaTime;
				if (logoY > LOGO_PROGRESSION_ZERO_Y)
					logoY = LOGO_PROGRESSION_ZERO_Y;
			}
		} else if (progression == 1) {
			if (logoY > LOGO_PROGRESSION_ONE_Y) {
				logoY -= LOGO_SPEED * deltaTime;
				if (logoY < LOGO_PROGRESSION_ONE_Y) {
					logoY = LOGO_PROGRESSION_ONE_Y;
					startProgressionThree();
				}
			}
			
			if (logoY < LOGO_PROGRESSION_ONE_Y) {
				logoY += LOGO_SPEED * deltaTime;
				if (logoY > LOGO_PROGRESSION_ONE_Y) {
					logoY = LOGO_PROGRESSION_ONE_Y;
					startProgressionThree();
				}
			}
			
		} else if (progression == 3) {
			//If the user isn't logged in and the login window isn't already open, open it.
			if (!prefs.isLoggedIn() && !isLoginRegisterWindowOpen()) {
				loginRegisterWndw = new LoginRegisterWindow(this, stage);
				stage.addActor(loginRegisterWndw);
				loginRegisterWndw.centerOnScreen();
			}
			
			//If the user is logged in and the login window is still open, close it.
			if (prefs.isLoggedIn() && isLoginRegisterWindowOpen()) {
				loginRegisterWndw.remove();
				loginRegisterWndw = null;
			}
			
			//If no server is picked and the picker isn't already open, open it.
			if (prefs.isLoggedIn() && !isServerPickerWindowOpen() && !ArchipeloClient.getGame().getNetworkManager().isConnected() && !isLoginRegisterWindowOpen()) {//Don't open the server picker if the login window is open
				serverPickerWndw = new ServerPickerWindow();
				stage.addActor(serverPickerWndw);
				serverPickerWndw.centerOnScreen();
			}
			
			//If the server is picked and the server picker window is still open, close it.
			if (prefs.isLoggedIn() && ArchipeloClient.getGame().getNetworkManager().isConnected() && isServerPickerWindowOpen()) {
				serverPickerWndw.remove();
				serverPickerWndw = null;
			}
		}
	}

	@Override
	public void render (SpriteBatch batch, float width, float height) {
		scrollingBackground.render(batch);
	}

	@Override
	public void renderUi (SpriteBatch batch, float width, float height) {
		BitmapFont font = ArchipeloClient.getGame().getFontManager().getFont(Fonts.PIXELATED, Sizes.SMALL);
		if (ArchipeloClient.DEBUGMODE) {
			//Fps counter
			GlyphLayout layoutFPS = new GlyphLayout(font, "FPS: " + Gdx.graphics.getFramesPerSecond());
			font.draw(batch, layoutFPS, 10, height - layoutFPS.height);
		}
		
		GlyphLayout layoutCon = new GlyphLayout(font, LM.getMsg(Cat.UI, "netStatus") + " (" + prefs.getServerName() + "): " + (ArchipeloClient.getGame().getNetworkManager().isConnected() ? "[GREEN]" + LM.getMsg(Cat.UI, "connected") : "[RED]" + LM.getMsg(Cat.UI, "notConnected")));
		font.draw(batch, layoutCon, 4, 4 + layoutCon.height);
		
		if (flashOn && progression == 0)//Render flashing "Press Start" text
			ArchipeloClient.getGame().getFontManager().getFont(Fonts.PIXELATED, Sizes.MEDIUM).draw(batch, pressAnyGlyphLayout, width / 2 - pressAnyGlyphLayout.width / 2, height - logoY - 50);

		//Render logo
		batch.draw(logo, width / 2 - logo.getWidth() / 2, height - logoY);
		
		//Render stage
		batch.end();
		stage.draw();
		batch.begin();
	}

	@Override
	public void resize (int width, int height) {
		scrollingBackground.resize();
		
		//Adjust ui elements for new screen size
		if (playBtn != null)
			playBtn.setPosition(Gdx.graphics.getWidth() / 2 - playBtn.getWidth() / 2, Gdx.graphics.getHeight() / 2 - playBtn.getHeight() / 2 + 20);
		if (changeServerBtn != null)
			changeServerBtn.setPosition(Gdx.graphics.getWidth() / 2 - changeServerBtn.getWidth() / 2, Gdx.graphics.getHeight() / 2 - changeServerBtn.getHeight() / 2 - 20);
		if (logoutBtn != null)
				logoutBtn.setPosition(Gdx.graphics.getWidth() / 2 - logoutBtn.getWidth() / 2, Gdx.graphics.getHeight() / 2 - logoutBtn.getHeight() / 2 - 60);
		if (exitBtn != null)
			exitBtn.setPosition(Gdx.graphics.getWidth() / 2 - exitBtn.getWidth() / 2, Gdx.graphics.getHeight() / 2 - exitBtn.getHeight() / 2 - 100);
		if (loginWndw != null)
			loginWndw.centerOnScreen();
		if (registerWndw != null)
			registerWndw.centerOnScreen();
		if (loginRegisterWndw != null)
			loginRegisterWndw.centerOnScreen();
		if (serverPickerWndw != null)
			serverPickerWndw.centerOnScreen();
	}

	@Override
	public void dispose () {
		stage.dispose();
	}
	
	private void startProgressionThree () {
		progression = 3;
		
		//Load ui for main menu
		playBtn = new TextButton(LM.getMsg(Cat.UI, "play"), ArchipeloClient.getGame().getUiSkin());
		playBtn.addListener(new ClickListener() {
			
			@Override
			public void clicked (InputEvent event, float x, float y) {
				//Add listener to get login pack response
				ArchipeloClient.getGame().getNetworkManager().addPacketHandler(new PacketHandler() {
					
					@Override
					public boolean handlePacket (Packet packet) {
						if (packet.packetType == PacketType.LOGIN) {
							LoginPacket loginPacket = (LoginPacket) packet;
							//Handle packet results
							switch (loginPacket.result) {
							case LoginPacket.RESULT_BAD_VERSION:
								QuickUi.showErrorWindow(LM.getMsg(Cat.ERROR, "outOfDateTitle"), LM.getMsg(Cat.ERROR, "outOfDate") + "(" + loginPacket.version + ")", stage);
								break;
							case LoginPacket.RESULT_LOGIN_ERROR:
								QuickUi.showErrorWindow(LM.getMsg(Cat.ERROR, "loginHBInvalidTitle"), LM.getMsg(Cat.ERROR, "loginHBInvalid"), stage);
								break;
							case LoginPacket.RESULT_LOGIN_SUCCESSFUL:
								ArchipeloClient.getGame().getScreenManager().setScreen(new CharacterPickerScreen(ArchipeloClient.getGame().getPrefs().getEmail()));
								break;
							}
							ArchipeloClient.getGame().getNetworkManager().removePacketHandler(this);
							return true;
						}
						return false;
					}
				});
				
				//Send login packet
				Prefs prefs = ArchipeloClient.getGame().getPrefs();
				if (prefs.isLoggedIn()) {
					if (prefs.isServerPicked() && ArchipeloClient.getGame().getNetworkManager().isConnected()) {
						//Connected and logged in, so send packet
						ArchipeloClient.getGame().getNetworkManager().sendPacket(new LoginPacket(prefs.getEmail(), prefs.getPassword()));
					} else {
						QuickUi.showErrorWindow(LM.getMsg(Cat.UI, "notConnected"), LM.getMsg(Cat.ERROR, "notConnected"), stage);
					}
				} else {
					QuickUi.showErrorWindow(LM.getMsg(Cat.ERROR, "loginNotSetTitle"), LM.getMsg(Cat.ERROR, "loginNotSet"), stage);
				}
					
				super.clicked(event, x, y);
			}
			
		});
		playBtn.setPosition(Gdx.graphics.getWidth() / 2 - playBtn.getWidth() / 2, Gdx.graphics.getHeight() / 2 - playBtn.getHeight() / 2 + 20);
		stage.addActor(playBtn);
		
		changeServerBtn = new TextButton(LM.getMsg(Cat.UI, "changeServer"), ArchipeloClient.getGame().getUiSkin());
		changeServerBtn.addListener(new ClickListener() {
			
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (!isServerPickerWindowOpen()) {
					ArchipeloClient.getGame().getNetworkManager().disconnect();
					serverPickerWndw = new ServerPickerWindow();
					serverPickerWndw.centerOnScreen();
					stage.addActor(serverPickerWndw);
				}
				super.clicked(event, x, y);
			}
			
		});
		changeServerBtn.setPosition(Gdx.graphics.getWidth() / 2 - changeServerBtn.getWidth() / 2, Gdx.graphics.getHeight() / 2 - changeServerBtn.getHeight() / 2 - 20);
		stage.addActor(changeServerBtn);
		
		final MainMenuScreen mainMenuScreen = this;
		
		logoutBtn = new TextButton(LM.getMsg(Cat.UI, "logout"), ArchipeloClient.getGame().getUiSkin());
		logoutBtn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (loginRegisterWndw == null || loginRegisterWndw.getStage() == null) {
					//Reset login credentials
					prefs.resetLogin();
					if (ArchipeloClient.getGame().getNetworkManager().isConnected())
						ArchipeloClient.getGame().getNetworkManager().sendPacket(new LogoutPacket());
					
					loginRegisterWndw = new LoginRegisterWindow(mainMenuScreen, stage);
					loginRegisterWndw.centerOnScreen();
					stage.addActor(loginRegisterWndw);
				}
				super.clicked(event, x, y);
			}
		});
		logoutBtn.setPosition(Gdx.graphics.getWidth() / 2 - logoutBtn.getWidth() / 2, Gdx.graphics.getHeight() / 2 - logoutBtn.getHeight() / 2 - 60);
		stage.addActor(logoutBtn);
		
		if (!ArchipeloClient.IS_GWT) {
			exitBtn = new TextButton(LM.getMsg(Cat.UI, "exit"), ArchipeloClient.getGame().getUiSkin());
			exitBtn.addListener(new ClickListener() {
				
				@Override
				public void clicked(InputEvent event, float x, float y) {
					Gdx.app.exit();
					super.clicked(event, x, y);
				}
				
			});
			exitBtn.setPosition(Gdx.graphics.getWidth() / 2 - exitBtn.getWidth() / 2, Gdx.graphics.getHeight() / 2 - exitBtn.getHeight() / 2 - 100);
			stage.addActor(exitBtn);
		}
	}
	
	private void showDisclaimerWindow () {
		final Dialog dialog = new Dialog(LM.getMsg(Cat.UI, "disclaimerTitle"), ArchipeloClient.getGame().getUiSkin(), "dialog") {
		    public void result(Object obj) {
		        remove();
		    }
		};
		Label label = new Label(LM.getMsg(Cat.UI, "disclaimer"), ArchipeloClient.getGame().getUiSkin());
		label.setWrap(true);
		dialog.add(label).width(500);
		dialog.row();
		TextButton closeButton = new TextButton(LM.getMsg(Cat.UI, "okay"), ArchipeloClient.getGame().getUiSkin());
		closeButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				dialog.remove();
				super.clicked(event, x, y);
			}
		});
		dialog.add(closeButton);
		dialog.key(Keys.ENTER, true);
		dialog.key(Keys.ESCAPE, true);
		dialog.show(stage);
	}
	
	public void setLoginWindow (LoginWindow loginWndw) {
		this.loginWndw = loginWndw;
	}
	
	public boolean isThereAlreadyALoginWindow () {
		return loginWndw != null && stage.getActors().contains(loginWndw, true);
	}
	
	public void setRegisterWindow (RegisterWindow registerWndw) {
		this.registerWndw = registerWndw;
	}
	
	public boolean isThereAlreadyARegisterWindow () {
		return registerWndw != null && stage.getActors().contains(registerWndw, true);
	}
	
	public boolean isLoginRegisterWindowOpen () {
		return loginRegisterWndw != null && stage.getActors().contains(loginRegisterWndw, true);
	}
	
	public boolean isServerPickerWindowOpen () {
		return serverPickerWndw != null && stage.getActors().contains(serverPickerWndw, true);
	}
	
}
