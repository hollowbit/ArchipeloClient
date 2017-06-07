package net.hollowbit.archipelo.screen.screens.gamescreen.popup;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.tools.FontManager.Fonts;
import net.hollowbit.archipelo.tools.FontManager.Sizes;

public class MapTagPopupText extends PopupText {
	
	private static final int Y = 250;
	//private static final float WAIT_TIME = 1;//2 seconds to wait until doing animation
	private static final float ANIMATION_TIME = 0.5f;//0.5 second
	private static final float LIFE_TIME = 4;//5 seconds
	private static final int TAG_WIDTH = 450;

	private float timer;
	private float width;
	
	public MapTagPopupText(String text, PopupTextManager manager) {
		super(text, 0, Y, manager);
		GlyphLayout layout = new GlyphLayout(ArchipeloClient.getGame().getFontManager().getFont(Fonts.PIXELATED, Sizes.MEDIUM), text);
		width = layout.width;
		timer = 0;
	}

	@Override
	public void update(float deltatime) {
		timer += deltatime;
		
		if (timer >= LIFE_TIME)
			remove = true;
		
		if (timer < ANIMATION_TIME) {
			x = TAG_WIDTH / ANIMATION_TIME * (timer);
		} else if (timer > LIFE_TIME - ANIMATION_TIME) {
			float delta = LIFE_TIME - timer;
			x = TAG_WIDTH / ANIMATION_TIME * delta;
		} else {
			x = TAG_WIDTH;
		}
	}

	@Override
	public void render(SpriteBatch batch) {
		batch.draw(ArchipeloClient.getGame().getAssetManager().getTexture("maptag"), Gdx.graphics.getWidth() - x, Y);
		BitmapFont font = ArchipeloClient.getGame().getFontManager().getFont(Fonts.PIXELATED, Sizes.MEDIUM);
		font.draw(batch, text, Gdx.graphics.getWidth() - x + TAG_WIDTH / 2 - width / 2, Y + 38);
	}

}
