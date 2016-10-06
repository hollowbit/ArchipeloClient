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
import net.hollowbit.archipelo.hollowbitserver.HollowBitServerQueryResponseHandler;
import net.hollowbit.archipelo.screen.Screen;
import net.hollowbit.archipelo.screen.ScreenType;
import net.hollowbit.archipelo.screen.screens.mainmenu.LoginRegisterWindow;
import net.hollowbit.archipelo.screen.screens.mainmenu.LoginWindow;
import net.hollowbit.archipelo.screen.screens.mainmenu.RegisterWindow;
import net.hollowbit.archipelo.screen.screens.mainmenu.ServerPickerWindow;
import net.hollowbit.archipelo.tools.FontManager.Fonts;
import net.hollowbit.archipelo.tools.FontManager.Sizes;
import net.hollowbit.archipelo.tools.GameCamera;
import net.hollowbit.archipelo.tools.Prefs;
import net.hollowbit.archipeloshared.CollisionRect;

public class MainMenuScreen extends Screen {
	
	private static final float CAM_SPEED_X = 10;
	private static final float CAM_SPEED_Y = 5;
	
	private static final float LOGO_SPEED = 450;
	private int LOGO_PROGRESSION_ZERO_Y = 150;
	private int LOGO_PROGRESSION_ONE_Y = 15;
	
	private static final float FLASH_TIME = 0.8f;
	
	private static final float BACKGROUND_IMAGE_SCALE = 1.5f;
	
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
	
	Texture background;
	Texture logo;
	float camVelocityX = CAM_SPEED_X, camVelocityY = CAM_SPEED_Y;
	GameCamera cam;
	
	float logoY;
	
	float flashTimer;
	boolean flashOn;
	
	GlyphLayout pressAnyGlyphLayout;
	
	float backgroundWidth, backgroundHeight;
	Prefs prefs;
	
	public MainMenuScreen () {
		super(ScreenType.MAIN_MENU);
	}

	@Override
	public void create () {
		progression = 0;
		stage = new Stage(ArchipeloClient.getGame().getCameraUi().getScreenViewport(), ArchipeloClient.getGame().getBatch());
		background = ArchipeloClient.getGame().getAssetManager().getTexture("mainmenu-background");
		logo = ArchipeloClient.getGame().getAssetManager().getTexture("logo");
		cam = ArchipeloClient.getGame().getCamera();
		cam.focusOnEntity(null);
		cam.move(200, 200, 0);
		backgroundWidth = cam.getWidth() * BACKGROUND_IMAGE_SCALE;
		backgroundHeight = cam.getHeight() * BACKGROUND_IMAGE_SCALE;
		
		pressAnyGlyphLayout = new GlyphLayout(ArchipeloClient.getGame().getFontManager().getFont(Fonts.PIXELATED, Sizes.MEDIUM), ArchipeloClient.IS_MOBILE ? "Tap to Start!" : "Press Any Key!");
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
			ArchipeloClient.getGame().getHollowBitServerConnectivity().sendVerifyQuery(prefs.getUsername(), prefs.getPassword(), new HollowBitServerQueryResponseHandler() {
				
				@Override
				public void responseReceived(int id, String[] data) {
					if (id != 3)//3 means there was a correct login. If it's not 3, it failed.
						prefs.resetLogin();//Reset info to let the game know that there is no login credentials at the moment
				}
			});
		}
		
		//If there is a server saved, try to connect to it, if possible.
		if (prefs.isServerPicked()) {
			ArchipeloClient.getGame().getHollowBitServerConnectivity().sendGetServerByNameQuery(prefs.getServerName(), new HollowBitServerQueryResponseHandler() {
				
				@Override
				public void responseReceived(int id, String[] data) {
					if (id == 15) {//If we found the server we last connected to, try to connect to it again.
						String hostname = data[0];
						ArchipeloClient.getGame().getNetworkManager().connect(hostname, ArchipeloClient.PORT);
					} else
						prefs.resetServer();
				}
			});
		}
	}

	@Override
	public void update (float deltaTime) {
		stage.act();
		
		//Update game camera to move around map
		CollisionRect rect = cam.getViewRect();
		float camX = rect.x + camVelocityX * deltaTime;
		float camY = rect.y + camVelocityY * deltaTime;
		
		if (camX < 0) {
			camX = 0;
			camVelocityX = -camVelocityX;
		}
		
		if (camY < 0) {
			camY = 0;
			camVelocityY = -camVelocityY;
		}
		
		if (camX + rect.width > backgroundWidth) {
			camX = backgroundWidth - rect.width;
			camVelocityX = -camVelocityX;
		}
		
		if (camY + rect.height > backgroundHeight) {
			camY = backgroundHeight - rect.height;
			camVelocityY = -camVelocityY;
		}
		
		cam.move(camX, camY, 0);
		
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
				loginRegisterWndw.setPosition(Gdx.graphics.getWidth() / 2 - loginRegisterWndw.getWidth() / 2, Gdx.graphics.getHeight() / 2 - loginRegisterWndw.getHeight() / 2);
			}
			
			//If the user is logged in and the login window is still open, close it.
			if (prefs.isLoggedIn() && isLoginRegisterWindowOpen()) {
				loginRegisterWndw.remove();
				loginRegisterWndw = null;
			}
			
			//If no server is picked and the picker isn't already open, open it.
			if (!prefs.isLoggedIn() && !isServerPickerWindowOpen() && !isLoginRegisterWindowOpen()) {//Don't open the server picker if the login window is open
				serverPickerWndw = new ServerPickerWindow();
				stage.addActor(serverPickerWndw);
				serverPickerWndw.setPosition(Gdx.graphics.getWidth() / 2 - serverPickerWndw.getWidth() / 2, Gdx.graphics.getHeight() / 2 - serverPickerWndw.getHeight() / 2);
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
		batch.draw(background, 0, 0, backgroundWidth, backgroundHeight);//Render blurred background image
	}

	@Override
	public void renderUi (SpriteBatch batch, float width, float height) {
		BitmapFont font = ArchipeloClient.getGame().getFontManager().getFont(Fonts.PIXELATED, Sizes.SMALL);
		if (ArchipeloClient.DEBUGMODE) {
			//Fps counter
			GlyphLayout layoutFPS = new GlyphLayout(font, "FPS: " + Gdx.graphics.getFramesPerSecond());
			font.draw(batch, layoutFPS, 10, height - layoutFPS.height);
		}
		
		GlyphLayout layoutCon = new GlyphLayout(font, "Connection (" + prefs.getServerName() + "): " + (ArchipeloClient.getGame().getNetworkManager().isConnected() ? "[GREEN]Connected!" : "[RED]Not Connected."));
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
		stage.getViewport().update(width, height);
		if (playBtn != null)
			playBtn.setPosition(Gdx.graphics.getWidth() / 2 - playBtn.getWidth() / 2, Gdx.graphics.getHeight() / 2 - playBtn.getHeight() / 2 + 20);
		if (changeServerBtn != null)
			changeServerBtn.setPosition(Gdx.graphics.getWidth() / 2 - changeServerBtn.getWidth() / 2, Gdx.graphics.getHeight() / 2 - changeServerBtn.getHeight() / 2 - 20);
		if (logoutBtn != null)
				logoutBtn.setPosition(Gdx.graphics.getWidth() / 2 - logoutBtn.getWidth() / 2, Gdx.graphics.getHeight() / 2 - logoutBtn.getHeight() / 2 - 60);
		if (exitBtn != null)
			exitBtn.setPosition(Gdx.graphics.getWidth() / 2 - exitBtn.getWidth() / 2, Gdx.graphics.getHeight() / 2 - exitBtn.getHeight() / 2 - 100);
		if (loginWndw != null)
			loginWndw.setPosition(Gdx.graphics.getWidth() / 2 - loginWndw.getWidth() / 2, Gdx.graphics.getHeight() / 2 - loginWndw.getHeight() / 2);
		if (registerWndw != null)
			registerWndw.setPosition(Gdx.graphics.getWidth() / 2 - registerWndw.getWidth() / 2, Gdx.graphics.getHeight() / 2 - registerWndw.getHeight() / 2);
		if (loginRegisterWndw != null)
			loginRegisterWndw.setPosition(Gdx.graphics.getWidth() / 2 - loginRegisterWndw.getWidth() / 2, Gdx.graphics.getHeight() / 2 - loginRegisterWndw.getHeight() / 2);
		if (serverPickerWndw != null)
			serverPickerWndw.setPosition(Gdx.graphics.getWidth() / 2 - serverPickerWndw.getWidth() / 2, Gdx.graphics.getHeight() / 2 - serverPickerWndw.getHeight() / 2);
		
		backgroundWidth = cam.getWidth() * BACKGROUND_IMAGE_SCALE;
		backgroundHeight = cam.getHeight() * BACKGROUND_IMAGE_SCALE;
	}

	@Override
	public void dispose () {
		stage.dispose();
	}
	
	private void startProgressionThree () {
		progression = 3;
		
		//Load ui
		playBtn = new TextButton("Play", ArchipeloClient.getGame().getUiSkin());
		playBtn.addListener(new ClickListener() {
			
			@Override
			public void clicked (InputEvent event, float x, float y) {
				ArchipeloClient.getGame().getScreenManager().setScreen(new CharacterPickerScreen());
				super.clicked(event, x, y);
			}
			
		});
		playBtn.setPosition(Gdx.graphics.getWidth() / 2 - playBtn.getWidth() / 2, Gdx.graphics.getHeight() / 2 - playBtn.getHeight() / 2 + 20);
		stage.addActor(playBtn);
		
		changeServerBtn = new TextButton("Change Server", ArchipeloClient.getGame().getUiSkin());
		changeServerBtn.addListener(new ClickListener() {
			
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (!isServerPickerWindowOpen()) {
					ArchipeloClient.getGame().getNetworkManager().disconnect();
					serverPickerWndw = new ServerPickerWindow();
					serverPickerWndw.setPosition(Gdx.graphics.getWidth() / 2 - serverPickerWndw.getWidth() / 2, Gdx.graphics.getHeight() / 2 - serverPickerWndw.getHeight() / 2);
					stage.addActor(serverPickerWndw);
				}
				super.clicked(event, x, y);
			}
			
		});
		changeServerBtn.setPosition(Gdx.graphics.getWidth() / 2 - changeServerBtn.getWidth() / 2, Gdx.graphics.getHeight() / 2 - changeServerBtn.getHeight() / 2 - 20);
		stage.addActor(changeServerBtn);
		
		final MainMenuScreen mainMenuScreen = this;
		
		logoutBtn = new TextButton("Logout", ArchipeloClient.getGame().getUiSkin());
		logoutBtn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (loginRegisterWndw == null || loginRegisterWndw.getStage() == null) {
					//Reset login credentials
					prefs.resetLogin();
					
					loginRegisterWndw = new LoginRegisterWindow(mainMenuScreen, stage);
					loginRegisterWndw.setPosition(Gdx.graphics.getWidth() / 2 - loginRegisterWndw.getWidth() / 2, Gdx.graphics.getHeight() / 2 - loginRegisterWndw.getHeight() / 2);
					stage.addActor(loginRegisterWndw);
				}
				super.clicked(event, x, y);
			}
		});
		logoutBtn.setPosition(Gdx.graphics.getWidth() / 2 - logoutBtn.getWidth() / 2, Gdx.graphics.getHeight() / 2 - logoutBtn.getHeight() / 2 - 60);
		stage.addActor(logoutBtn);
		
		if (!ArchipeloClient.IS_GWT) {
			exitBtn = new TextButton("Exit", ArchipeloClient.getGame().getUiSkin());
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
		final Dialog dialog = new Dialog("Disclaimer", ArchipeloClient.getGame().getUiSkin(), "dialog") {
		    public void result(Object obj) {
		        remove();
		    }
		};
		Label label = new Label("Please note that Archipelo is very early in development. Please judge it accordingly. Also note that server-wide account deletion is a possibility during this development stage. Thank you.", ArchipeloClient.getGame().getUiSkin());
		label.setWrap(true);
		dialog.add(label).width(500);
		dialog.row();
		TextButton closeButton = new TextButton("Okay", ArchipeloClient.getGame().getUiSkin());
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
