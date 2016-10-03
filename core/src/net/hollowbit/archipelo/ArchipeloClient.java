/*
 * Author: vedi0boy
 * Company: HollowBit
 * Please see the Github README.md before using this code or any code in this project.
 */
package net.hollowbit.archipelo;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import net.hollowbit.archipelo.entity.EntityType;
import net.hollowbit.archipelo.hollowbitserver.HollowBitServerConnectivity;
import net.hollowbit.archipelo.items.ItemType;
import net.hollowbit.archipelo.network.NetworkManager;
import net.hollowbit.archipelo.screen.ScreenManager;
import net.hollowbit.archipelo.screen.screens.MainMenuScreen;
import net.hollowbit.archipelo.tools.AssetManager;
import net.hollowbit.archipelo.tools.FontManager;
import net.hollowbit.archipelo.tools.GameCamera;
import net.hollowbit.archipelo.tools.UiCamera;
import net.hollowbit.archipelo.world.MapElementManager;
import net.hollowbit.archipelo.world.World;

public class ArchipeloClient extends ApplicationAdapter {
	
	public static final int PORT = 22122;
	
	public static final String PREFS_NAME = "archipelo";
	public static final String VERSION = "0.1a";
	public static final int TILE_SIZE = 16;
	public static final int PLAYER_SIZE = 32;
	public static final float UNITS_PER_PIXEL = 1 / 3f;//World pixels per screen pixel.
	public static boolean IS_MOBILE = false;
	public static boolean IS_GWT = false;
	
	public static float DELTA_TIME = 0;
	public static float STATE_TIME = 0;//this is for looping animations where it doesn't matter where it starts.
	public static boolean DEBUGMODE = true;
	
	public static String SERVER = "No Server Chosen";
	public static boolean SERVER_PICKED = false;
	public static String USERNAME = "";
	public static String PASSWORD = "";
	public static boolean LOGGED_IN = false;
	
	private static ArchipeloClient game;
	
	SpriteBatch batch;
	
	AssetManager assetManager;
	NetworkManager networkManager;
	ScreenManager screenManager;
	MapElementManager elementManager;
	FontManager fontManager;
	Skin skin;
	HollowBitServerConnectivity hollowBitServerConnectivity;
	
	GameCamera cameraGame;
	UiCamera cameraUi;
	
	World world = null;
	
	@Override
	public void create () {
		game = this;
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		
		//Load prefs
		Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
		if (prefs.getBoolean("logged-in", false)) {
			LOGGED_IN = true;
			USERNAME = prefs.getString("username");
			PASSWORD = prefs.getString("password");
		}
		
		if (prefs.getBoolean("server-picked", false)) {
			SERVER_PICKED = true;
			SERVER = prefs.getString("server-name");
		}
		
		batch = new SpriteBatch();
		
		skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
		
		//Enable color markup on skin fonts
		skin.getFont("default-font").getData().markupEnabled = true;
		skin.getFont("large-font").getData().markupEnabled = true;
		skin.getFont("chat-font").getData().markupEnabled = true;

		//Temporary way to add assets
		assetManager = new AssetManager();
		assetManager.putTextureMap("tiles", new Texture("tiles.png"), TILE_SIZE, TILE_SIZE);
		assetManager.putTexture("blank", new Texture("blank.png"));
		assetManager.putTexture("blank-border", new Texture("blank_border.png"));
		assetManager.putTexture("elements", new Texture("map_elements.png"));
		assetManager.putTexture("maptag", new Texture("maptag.png"));
		assetManager.putTexture("mainmenu-background", new Texture("mainmenu_background.png"));
		assetManager.putTexture("logo", new Texture("logo.png"));
		ItemType.loadAllImages();
		//assetManager.putTexture("invalid", new Texture("invalid.png"));//For some reason this image cannot be loaded by html. Fix later.

		elementManager = new MapElementManager();
		elementManager.loadMapElements();
		
		EntityType.loadAllImages();
		
		//Cameras
		cameraGame = new GameCamera();
		cameraUi = new UiCamera();
		
		//Load cacert for SSL
		//System.setProperty("javax.net.ssl.trustStore", "cacerts");
		//System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
		
		//Managers
		networkManager = new NetworkManager();
		fontManager = new FontManager();
		hollowBitServerConnectivity = new HollowBitServerConnectivity();
		screenManager = new ScreenManager(new MainMenuScreen());
		
		//For testing purposes
		//IS_MOBILE = true;
		//IS_GWT = true;
		
		//If on mobile device, set IS_MOBILE to true
		if (Gdx.app.getType() == ApplicationType.Android || Gdx.app.getType() == ApplicationType.iOS)
			IS_MOBILE = true;
		
		if (Gdx.app.getType() == ApplicationType.WebGL)
			IS_GWT = true;
		
		world = new World();
	}

	@Override
	public void render () {
		/*if (!networkManager.isConnected())
			return;*/
		
		//Enable/disable debug mode
		if (Gdx.input.isKeyJustPressed(Keys.F3))
			DEBUGMODE = !DEBUGMODE;
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		DELTA_TIME = Gdx.graphics.getDeltaTime();
		STATE_TIME += DELTA_TIME;
		
		if (batch.isDrawing())
			batch.end();
		
		cameraGame.update(DELTA_TIME);
		batch.setProjectionMatrix(cameraGame.combined());
		batch.begin();
		networkManager.update();
		screenManager.update(DELTA_TIME);
		screenManager.render(batch, cameraGame.getWidth(), cameraGame.getHeight());
		batch.end();
		
		if (batch.isDrawing())
			batch.end();

		batch.setProjectionMatrix(cameraUi.combined());
		batch.begin();
		screenManager.renderUi(batch, cameraUi.getWidth(), cameraUi.getHeight());
		batch.end();
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		cameraGame.resize(width, height);
		cameraUi.resize(width, height);
		screenManager.resize(width, height);
	}
	
	public SpriteBatch getBatch () {
		return batch;
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
	
	public HollowBitServerConnectivity getHollowBitServerConnectivity () {
		return hollowBitServerConnectivity;
	}
	
	public World getWorld () {
		return world;
	}
	
	public Skin getUiSkin () {
		return skin;
	}
	
	public static ArchipeloClient getGame () {
		return game;
	}
	
}
