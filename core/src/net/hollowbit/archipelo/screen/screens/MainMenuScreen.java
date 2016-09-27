package net.hollowbit.archipelo.screen.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Input.Keys;
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
import net.hollowbit.archipelo.screen.Screen;
import net.hollowbit.archipelo.screen.ScreenType;
import net.hollowbit.archipelo.screen.screens.mainmenu.*;
import net.hollowbit.archipelo.tools.FontManager.Fonts;
import net.hollowbit.archipelo.tools.FontManager.Sizes;
import net.hollowbit.archipelo.tools.GameCamera;
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
	TextButton loginBtn;
	TextButton registerBtn;
	TextButton exitBtn;
	
	LoginWindow loginWndw;
	RegisterWindow registerWndw;
	
	Texture background;
	Texture logo;
	float camVelocityX = CAM_SPEED_X, camVelocityY = CAM_SPEED_Y;
	GameCamera cam;
	
	float logoY;
	
	float flashTimer;
	boolean flashOn;
	
	GlyphLayout pressAnyGlyphLayout;
	
	float backgroundWidth, backgroundHeight;
	
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
		
		//Show disclaimer message on first time opening app
		Preferences prefs = Gdx.app.getPreferences("archipelo");
		if (!ArchipeloClient.DEBUGMODE && !prefs.getBoolean("disclaimer-shown", false)) {
			showErrorWindow("Please note that Archipelo is very early and development. Please judge it accordingly. Also note that server-wide account deletion is a possibility during this development stage. Thank you.");
			prefs.putBoolean("disclaimer-shown", true);
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
		
		//Update flash button
		if (progression == 0) {
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
		
		GlyphLayout layoutCon = new GlyphLayout(font, "Connection (US-East): " + (ArchipeloClient.getGame().getNetworkManager().isConnected() ? "[GREEN]Connected!" : "[RED]Not Connected."));
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
		if (loginBtn != null)
			loginBtn.setPosition(Gdx.graphics.getWidth() / 2 - loginBtn.getWidth() / 2, Gdx.graphics.getHeight() / 2 - loginBtn.getHeight() / 2 + 20);
		if (registerBtn != null)
			registerBtn.setPosition(Gdx.graphics.getWidth() / 2 - registerBtn.getWidth() / 2, Gdx.graphics.getHeight() / 2 - registerBtn.getHeight() / 2 - 20);
		if (exitBtn != null)
			exitBtn.setPosition(Gdx.graphics.getWidth() / 2 - exitBtn.getWidth() / 2, Gdx.graphics.getHeight() / 2 - exitBtn.getHeight() / 2 - 60);
		if (loginWndw != null)
			loginWndw.setPosition(Gdx.graphics.getWidth() / 2 - loginWndw.getWidth() / 2, Gdx.graphics.getHeight() / 2 - loginWndw.getHeight() / 2);
		if (registerWndw != null)
			registerWndw.setPosition(Gdx.graphics.getWidth() / 2 - registerWndw.getWidth() / 2, Gdx.graphics.getHeight() / 2 - registerWndw.getHeight() / 2);
		
		backgroundWidth = cam.getWidth() * BACKGROUND_IMAGE_SCALE;
		backgroundHeight = cam.getHeight() * BACKGROUND_IMAGE_SCALE;
	}

	@Override
	public void dispose () {
		stage.dispose();
	}
	
	private void startProgressionThree () {
		progression = 3;
		
		final MainMenuScreen screen = this;
		
		//Load ui
		loginBtn = new TextButton("Login", ArchipeloClient.getGame().getUiSkin());
		loginBtn.addListener(new ClickListener() {
			
			@Override
			public void clicked (InputEvent event, float x, float y) {
				if (loginWndw == null || loginWndw.getStage() == null) {
					loginWndw = new LoginWindow(screen, stage);
					loginWndw.setPosition(Gdx.graphics.getWidth() / 2 - loginWndw.getWidth() / 2, Gdx.graphics.getHeight() / 2 - loginWndw.getHeight() / 2);
					stage.addActor(loginWndw);
				}
				super.clicked(event, x, y);
			}
			
		});
		loginBtn.setPosition(Gdx.graphics.getWidth() / 2 - loginBtn.getWidth() / 2, Gdx.graphics.getHeight() / 2 - loginBtn.getHeight() / 2 + 20);
		stage.addActor(loginBtn);
		
		registerBtn = new TextButton("Register", ArchipeloClient.getGame().getUiSkin());
		registerBtn.addListener(new ClickListener() {
			
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (registerWndw == null || registerWndw.getStage() == null) {
					registerWndw = new RegisterWindow(screen, stage);
					registerWndw.setPosition(Gdx.graphics.getWidth() / 2 - registerWndw.getWidth() / 2, Gdx.graphics.getHeight() / 2 - registerWndw.getHeight() / 2);
					stage.addActor(registerWndw);
				}
				super.clicked(event, x, y);
			}
			
		});
		registerBtn.setPosition(Gdx.graphics.getWidth() / 2 - registerBtn.getWidth() / 2, Gdx.graphics.getHeight() / 2 - registerBtn.getHeight() / 2 - 20);
		stage.addActor(registerBtn);
		
		if (!ArchipeloClient.IS_GWT) {
			exitBtn = new TextButton("Exit", ArchipeloClient.getGame().getUiSkin());
			exitBtn.addListener(new ClickListener() {
				
				@Override
				public void clicked(InputEvent event, float x, float y) {
					Gdx.app.exit();
					super.clicked(event, x, y);
				}
				
			});
			exitBtn.setPosition(Gdx.graphics.getWidth() / 2 - exitBtn.getWidth() / 2, Gdx.graphics.getHeight() / 2 - exitBtn.getHeight() / 2 - 60);
			stage.addActor(exitBtn);
		}
	}
	
	private void showErrorWindow (String error) {
		final Dialog dialog = new Dialog("Disclaimer", ArchipeloClient.getGame().getUiSkin(), "dialog") {
		    public void result(Object obj) {
		        remove();
		    }
		};
		Label label = new Label(error, ArchipeloClient.getGame().getUiSkin());
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

}
