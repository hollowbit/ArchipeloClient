/*
 * Author: vedi0boy
 * Company: HollowBit
 * Please see the Github README.md before using this code or any code in this project.
 */
package net.hollowbit.archipelo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.ScreenUtils;

import net.hollowbit.archipelo.audio.MusicManager;
import net.hollowbit.archipelo.audio.SoundManager;
import net.hollowbit.archipelo.entity.EntityType;
import net.hollowbit.archipelo.form.MobileCompatibleWindow;
import net.hollowbit.archipelo.hollowbitserver.HollowBitServerConnectivity;
import net.hollowbit.archipelo.items.ItemType;
import net.hollowbit.archipelo.network.NetworkManager;
import net.hollowbit.archipelo.screen.ScreenManager;
import net.hollowbit.archipelo.screen.screens.*;
import net.hollowbit.archipelo.tools.AssetManager;
import net.hollowbit.archipelo.tools.FontManager;
import net.hollowbit.archipelo.tools.GameCamera;
import net.hollowbit.archipelo.tools.LanguageSpecificMessageManager;
import net.hollowbit.archipelo.tools.LanguageSpecificMessageManager.Cat;
import net.hollowbit.archipelo.tools.PlayerInformationManager;
import net.hollowbit.archipelo.tools.Prefs;
import net.hollowbit.archipelo.tools.QuickUi;
import net.hollowbit.archipelo.tools.ShaderManager;
import net.hollowbit.archipelo.tools.ShaderManager.ShaderType;
import net.hollowbit.archipelo.tools.UiCamera;
import net.hollowbit.archipelo.world.MapElementManager;
import net.hollowbit.archipelo.world.World;

public class ArchipeloClient extends ApplicationAdapter {
	
	public static final int PORT = 22122;
	
	public static final String VERSION = "0.1a";
	public static final int TILE_SIZE = 16;
	public static final int PLAYER_SIZE = 90;
	public static final float UNITS_PER_PIXEL = 1 / 3f;//World pixels per screen pixel.
	public static final int MAX_CHARACTERS_PER_PLAYER = 4;
	public static boolean IS_MOBILE = false;
	public static boolean IS_GWT = false;
	public static final float SCREEN_INSET_FOR_WINDOWS = 0.8f;//Used in MobileCompatibleWindow.java
	
	public static float DELTA_TIME = 0;
	public static float STATE_TIME = 0;//this is for looping animations where it doesn't matter where it starts.
	public static boolean DEBUGMODE = false;
	public static boolean INVERT = false;
	public static boolean PLACEHOLDER_ART_MODE = DEBUGMODE = false;
	public static boolean CINEMATIC_MODE = false;
	public static boolean SHOW_COLLISION_RECTS = false;
	
	private static ArchipeloClient game;
	
	SpriteBatch batch;
	ShaderManager shaderManager;
	
	AssetManager assetManager;
	NetworkManager networkManager;
	ScreenManager screenManager;
	MapElementManager elementManager;
	FontManager fontManager;
	MusicManager musicManager;
	SoundManager soundManager;
	Skin skin;
	HollowBitServerConnectivity hollowBitServerConnectivity;
	LanguageSpecificMessageManager languageSpecificMessageManager;
	Prefs prefs;
	PlayerInformationManager playerInformationManager;
	
	GameCamera cameraGame;
	UiCamera cameraUi;
	
	World world = null;
	
	LinkedList<MobileCompatibleWindow> windows;
	
	@Override
	public void create () {
		game = this;
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		
		//Load prefs
		prefs = new Prefs();
		
		batch = new SpriteBatch();
		shaderManager = new ShaderManager();
		
		skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
		
		//Enable color markup on skin fonts
		skin.getFont("default-font").getData().markupEnabled = true;
		skin.getFont("medium-font").getData().markupEnabled = true;
		skin.getFont("large-font").getData().markupEnabled = true;
		skin.getFont("chat-font").getData().markupEnabled = true;
		
		//Temporary way to add assets
		//Art
		assetManager = new AssetManager();
		assetManager.putTextureMap("tiles", "tiles.png", TILE_SIZE, TILE_SIZE);
		assetManager.putTextureMap("icons", "ui/icons.png", QuickUi.ICON_SIZE, QuickUi.ICON_SIZE, true);
		assetManager.putTexture("blank", "blank.png");
		assetManager.putTexture("blank-border", "blank_border.png");
		assetManager.putTexture("elements", "map_elements.png");
		assetManager.putTexture("maptag", "maptag.png", true);
		assetManager.putTexture("mainmenu-background", "mainmenu_background.png", true);
		assetManager.putTexture("logo", "logo.png", true);
		assetManager.putTexture("invalid", "invalid.png");//For some reason this image cannot be loaded by html. Fix later.
		assetManager.putTexture("health-bar", "ui/statusbar/health.png");
		assetManager.putTexture("mana-bar", "ui/statusbar/mana.png");
		assetManager.putTexture("exp-bar", "ui/statusbar/exp.png");
		assetManager.putTexture("status-bar-overlay", "ui/statusbar/overlay.png");
		assetManager.putTexture("status-bar-background", "ui/statusbar/background.png");
		assetManager.putTexture("garbage", "ui/garbage.png");
		
		//Audio
		musicManager = new MusicManager();
		soundManager = new SoundManager();

		elementManager = new MapElementManager();
		elementManager.loadMapElements();
		
		ItemType.loadAllAssets();
		EntityType.loadAllImages();
		
		//Cameras
		cameraGame = new GameCamera();
		cameraUi = new UiCamera();
		
		//Load cacert for SSL
		//System.setProperty("javax.net.ssl.trustStore", "cacerts");
		//System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
		
		windows = new LinkedList<MobileCompatibleWindow>();
		
		//Managers
		networkManager = new NetworkManager();
		fontManager = new FontManager();
		screenManager = new ScreenManager();
		languageSpecificMessageManager = new LanguageSpecificMessageManager();
		languageSpecificMessageManager.reloadWithNewLanguage();
		hollowBitServerConnectivity = new HollowBitServerConnectivity();
		playerInformationManager = new PlayerInformationManager();
		
		if (hollowBitServerConnectivity.connect()) {
			if (DEBUGMODE) {
				screenManager.setScreen(new DebugStartScreen());
				//screenManager.setScreen(new FontTestScreen());
			} else
				screenManager.setScreen(new MainMenuScreen());
		} else
			screenManager.setScreen(new ErrorScreen(languageSpecificMessageManager.getMessage(Cat.UI, "couldNotConnectToHB")));
		
		//For testing purposes
		//IS_MOBILE = true;
		//IS_GWT = true;
		
		//If on mobile device, set IS_MOBILE to true
		if (Gdx.app.getType() == ApplicationType.Android || Gdx.app.getType() == ApplicationType.iOS)
			IS_MOBILE = true;
		
		if (Gdx.app.getType() == ApplicationType.WebGL)
			IS_GWT = true;
	}
	
	@Override
	public void render () {
		/*if (!networkManager.isConnected())
			return;*/
		
		//Enable/disable debug mode
		if (Gdx.input.isKeyJustPressed(Keys.F1))
			CINEMATIC_MODE = !CINEMATIC_MODE;
		
		if (Gdx.input.isKeyJustPressed(Keys.F2) && !IS_GWT) {
			byte[] pixels = ScreenUtils.getFrameBufferPixels(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), true);

			Pixmap pixmap = new Pixmap(Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), Pixmap.Format.RGBA8888);
			BufferUtils.copy(pixels, 0, pixmap.getPixels(), pixels.length);
			PixmapIO.writePNG(Gdx.files.external("archipelo/screenshots/" + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()) + "_screenshot.png"), pixmap);
			pixmap.dispose();
		}
		
		if (Gdx.input.isKeyJustPressed(Keys.F3))
			DEBUGMODE = !DEBUGMODE;
		
		//Enable/disable placeholder art mode
		if (Gdx.input.isKeyJustPressed(Keys.F4))
			PLACEHOLDER_ART_MODE = !PLACEHOLDER_ART_MODE;
		
		if (Gdx.input.isKeyJustPressed(Keys.F5))
			INVERT = !INVERT;
		
		if (Gdx.input.isKeyJustPressed(Keys.F8))
			SHOW_COLLISION_RECTS = !SHOW_COLLISION_RECTS;
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		DELTA_TIME = Gdx.graphics.getDeltaTime();
		STATE_TIME += DELTA_TIME;
		
		if (batch.isDrawing())
			batch.end();
		
		hollowBitServerConnectivity.update(DELTA_TIME);
		
		cameraGame.update(DELTA_TIME);
		batch.setProjectionMatrix(cameraGame.combined());
		batch.begin();
		networkManager.update();
		screenManager.update(DELTA_TIME);
		if (INVERT)
			shaderManager.applyShader(batch, ShaderType.EXPERIMENTAL);
		screenManager.render(batch, cameraGame.getWidth(), cameraGame.getHeight());
		shaderManager.resetShader(batch);
		batch.end();
		
		if (batch.isDrawing())
			batch.end();
		
		if (!CINEMATIC_MODE) {
			batch.begin();
			batch.setProjectionMatrix(cameraUi.combined());
			screenManager.renderUi(batch, cameraUi.getWidth(), cameraUi.getHeight());
			batch.end();
		}
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		cameraGame.resize(width, height);
		cameraUi.resize(width, height);
		screenManager.resize(width, height);
		
		for (MobileCompatibleWindow window : windows) {
			window.resize(width, height);
		}
	}
	
	@Override
	public void dispose() {
		super.dispose();
		batch.dispose();
	}
	
	@Override
	public void pause() {
		screenManager.pause();
		super.pause();
	}
	
	public SpriteBatch getBatch () {
		return batch;
	}
	
	public ShaderManager getShaderManager () {
		return shaderManager;
	}
	
	public GameCamera getCamera () {
		return cameraGame;
	}
	
	public UiCamera getCameraUi () {
		return cameraUi;
	}
	
	public AssetManager getAssetManager () {
		return assetManager;
	}
	
	public NetworkManager getNetworkManager () {
		return networkManager;
	}
	
	public ScreenManager getScreenManager () {
		return screenManager;
	}
	
	public MapElementManager getMapElementManager () {
		return elementManager;
	}
	
	public FontManager getFontManager () {
		return fontManager;
	}
	
	public MusicManager getMusicManager () {
		return musicManager;
	}
	
	public SoundManager getSoundManager () {
		return soundManager;
	}
	
	public LanguageSpecificMessageManager getLanguageSpecificMessageManager () {
		return languageSpecificMessageManager;
	}
	
	public HollowBitServerConnectivity getHollowBitServerConnectivity () {
		return hollowBitServerConnectivity;
	}
	
	public World getWorld () {
		return world;
	}
	
	public void setWorld (World world) {
		this.world = world;
	}
	
	public Skin getUiSkin () {
		return skin;
	}
	
	public Prefs getPrefs () {
		return prefs;
	}
	
	public PlayerInformationManager getPlayerInfoManager() {
		return playerInformationManager;
	}
	
	public void addWindow (MobileCompatibleWindow window) {
		windows.add(window);
	}
	
	public void removeWindow (MobileCompatibleWindow window) {
		windows.remove(window);
	}
	
	public boolean isWindowOpen () {
		return windows.size() > 0;
	}
	
	public void clearWindows () {
		windows.removeAll(windows);
	}
	
	public static ArchipeloClient getGame () {
		return game;
	}
	
}
