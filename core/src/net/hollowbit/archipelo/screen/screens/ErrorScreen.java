package net.hollowbit.archipelo.screen.screens;

import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.screen.Screen;
import net.hollowbit.archipelo.screen.ScreenType;
import net.hollowbit.archipelo.tools.FontManager.Fonts;
import net.hollowbit.archipelo.tools.FontManager.Sizes;
import net.hollowbit.archipelo.tools.GameCamera;
import net.hollowbit.archipeloshared.CollisionRect;

public class ErrorScreen extends Screen {
	
	private static final float CAM_SPEED_X = 10;
	private static final float CAM_SPEED_Y = 5;
	
	private static final float BACKGROUND_IMAGE_SCALE = 1.5f;
	
	Texture background;
	float camVelocityX = CAM_SPEED_X, camVelocityY = CAM_SPEED_Y;
	GameCamera cam;
	String title;
	Exception e;
	
	float backgroundWidth, backgroundHeight;
	
	public ErrorScreen(String title) {
		super(ScreenType.ERROR);
		this.title = title;
	}
	
	public ErrorScreen(Exception e) {
		super(ScreenType.ERROR);
		this.title = e.getMessage();
		this.e = e;
		Gdx.app.error("Error", "", e);
	}
	
	public ErrorScreen(String title, Exception e) {
		super(ScreenType.ERROR);
		this.title = title;
		this.e = e;
		Gdx.app.error("Error", "", e);
	}

	@Override
	public void create() {
		background = ArchipeloClient.getGame().getAssetManager().getTexture("mainmenu-background");
		cam = ArchipeloClient.getGame().getCamera();
		cam.focusOnEntity(null);
		cam.move(200, 200, 0);
		backgroundWidth = cam.getWidth() * BACKGROUND_IMAGE_SCALE;
		backgroundHeight = cam.getHeight() * BACKGROUND_IMAGE_SCALE;
		
		Gdx.input.setInputProcessor(new InputAdapter() {
			
			@Override
			public boolean keyDown(int keycode) {
				if (keycode == Keys.ESCAPE)
					Gdx.app.exit();
				return super.keyDown(keycode);
			}
			
			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, int button) {
				Gdx.app.exit();
				return super.touchUp(screenX, screenY, pointer, button);
			}
			
		});
	}

	@Override
	public void update(float deltaTime) {
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
	}

	@Override
	public void render(SpriteBatch batch, float width, float height) {
		batch.draw(background, 0, 0, backgroundWidth, backgroundHeight);//Render blurred background image
	}

	@Override
	public void renderUi(SpriteBatch batch, float width, float height) {
		GlyphLayout layoutTitle = new GlyphLayout(ArchipeloClient.getGame().getFontManager().getFont(Fonts.PIXELATED, Sizes.MEDIUM), "Error: " + title);
		ArchipeloClient.getGame().getFontManager().getFont(Fonts.PIXELATED, Sizes.MEDIUM).draw(batch, layoutTitle, width / 2 - layoutTitle.width / 2, height - 50);
		
		BitmapFont fontSmall = ArchipeloClient.getGame().getFontManager().getFont(Fonts.PIXELATED, Sizes.SMALL);
		if (e != null) {
			GlyphLayout layoutErrorTitle = new GlyphLayout(ArchipeloClient.getGame().getFontManager().getFont(Fonts.PIXELATED, Sizes.SMALL), e.getMessage(), Color.RED, width - 100, Align.left, true);
			ArchipeloClient.getGame().getFontManager().getFont(Fonts.PIXELATED, Sizes.SMALL).draw(batch, layoutErrorTitle, width / 2 - layoutErrorTitle.width / 2, height - layoutTitle.height - 60);
			
			GlyphLayout layoutDev = new GlyphLayout(ArchipeloClient.getGame().getFontManager().getFont(Fonts.PIXELATED, Sizes.SMALL), "Error has been sent to the developers.");
			ArchipeloClient.getGame().getFontManager().getFont(Fonts.PIXELATED, Sizes.SMALL).draw(batch, layoutDev, width / 2 - layoutDev.width / 2, 60);
			
			//Put exception on screen
			GlyphLayout layoutException = new GlyphLayout(ArchipeloClient.getGame().getFontManager().getFont(Fonts.PIXELATED, Sizes.SMALL), Arrays.toString(e.getStackTrace()).replaceAll(", ", "\n"));
			ArchipeloClient.getGame().getFontManager().getFont(Fonts.PIXELATED, Sizes.SMALL).draw(batch, layoutException, 10, height - layoutTitle.height - layoutErrorTitle.height - 80);
			
			//Send exception to us :P*/
		}
		if (!ArchipeloClient.IS_GWT) {
			GlyphLayout layoutExit = new GlyphLayout(fontSmall, (ArchipeloClient.IS_MOBILE ? "Tap to exit" : "Press ESC to exit"));
			fontSmall.draw(batch, layoutExit, width / 2 - layoutExit.width / 2, 20 + layoutExit.height);
		}
	}

	@Override
	public void resize(int width, int height) {
		backgroundWidth = cam.getWidth() * BACKGROUND_IMAGE_SCALE;
		backgroundHeight = cam.getHeight() * BACKGROUND_IMAGE_SCALE;
	}

	@Override
	public void dispose() {

	}

}
