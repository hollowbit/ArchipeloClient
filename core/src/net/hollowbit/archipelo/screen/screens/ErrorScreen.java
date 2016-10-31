package net.hollowbit.archipelo.screen.screens;

import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.screen.Screen;
import net.hollowbit.archipelo.screen.ScreenType;
import net.hollowbit.archipelo.screen.screens.mainmenu.ScrollingBackground;
import net.hollowbit.archipelo.tools.LM;
import net.hollowbit.archipelo.tools.LanguageSpecificMessageManager.Cat;
import net.hollowbit.archipelo.tools.FontManager.Fonts;
import net.hollowbit.archipelo.tools.FontManager.Sizes;

public class ErrorScreen extends Screen {
	
	String title;
	Exception e;
	
	ScrollingBackground scrollingBackground;
	
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
		scrollingBackground = new ScrollingBackground();
		
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
		scrollingBackground.update(deltaTime);
	}

	@Override
	public void render(SpriteBatch batch, float width, float height) {
		scrollingBackground.render(batch);
	}

	@Override
	public void renderUi(SpriteBatch batch, float width, float height) {
		GlyphLayout layoutTitle = new GlyphLayout(ArchipeloClient.getGame().getFontManager().getFont(Fonts.PIXELATED, Sizes.MEDIUM), LM.getMsg(Cat.UI, "error") + ": " + title, Color.WHITE, width - 20, Align.left, true);
		ArchipeloClient.getGame().getFontManager().getFont(Fonts.PIXELATED, Sizes.MEDIUM).draw(batch, layoutTitle, width / 2 - layoutTitle.width / 2, height - 50);
		
		BitmapFont fontSmall = ArchipeloClient.getGame().getFontManager().getFont(Fonts.PIXELATED, Sizes.SMALL);
		if (e != null) {
			GlyphLayout layoutErrorTitle = new GlyphLayout(ArchipeloClient.getGame().getFontManager().getFont(Fonts.PIXELATED, Sizes.SMALL), e.getMessage(), Color.RED, width - 100, Align.left, true);
			ArchipeloClient.getGame().getFontManager().getFont(Fonts.PIXELATED, Sizes.SMALL).draw(batch, layoutErrorTitle, width / 2 - layoutErrorTitle.width / 2, height - layoutTitle.height - 60);
			
			GlyphLayout layoutDev = new GlyphLayout(ArchipeloClient.getGame().getFontManager().getFont(Fonts.PIXELATED, Sizes.SMALL), LM.getMsg(Cat.UI, "errorSent"));
			ArchipeloClient.getGame().getFontManager().getFont(Fonts.PIXELATED, Sizes.SMALL).draw(batch, layoutDev, width / 2 - layoutDev.width / 2, 60);
			
			//Put exception on screen
			GlyphLayout layoutException = new GlyphLayout(ArchipeloClient.getGame().getFontManager().getFont(Fonts.PIXELATED, Sizes.SMALL), Arrays.toString(e.getStackTrace()).replaceAll(", ", "\n"));
			ArchipeloClient.getGame().getFontManager().getFont(Fonts.PIXELATED, Sizes.SMALL).draw(batch, layoutException, 10, height - layoutTitle.height - layoutErrorTitle.height - 80);
			
			//Send exception to us :P*/
		}
		if (!ArchipeloClient.IS_GWT) {
			GlyphLayout layoutExit = new GlyphLayout(fontSmall, (ArchipeloClient.IS_MOBILE ? LM.getMsg(Cat.UI, "tapExit") : LM.getMsg(Cat.UI, "pressEscExit")));
			fontSmall.draw(batch, layoutExit, width / 2 - layoutExit.width / 2, 20 + layoutExit.height);
		}
	}

	@Override
	public void resize(int width, int height) {
		scrollingBackground.resize();
	}

	@Override
	public void dispose() {

	}

}
