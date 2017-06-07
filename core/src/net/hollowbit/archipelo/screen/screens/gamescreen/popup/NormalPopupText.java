package net.hollowbit.archipelo.screen.screens.gamescreen.popup;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.tools.FontManager.Fonts;
import net.hollowbit.archipelo.tools.FontManager.Sizes;

public class NormalPopupText extends PopupText {
	
	private static final int START_Y = 150;
	private static final float FADE_TIME = 1;//1 second
	private static final float LIFE_TIME = 5;//5 seconds
	private static final float SPACING = 3;//pixels
	
	private float timer;
	private float width;
	private BitmapFont font;
	
	public NormalPopupText (String text, PopupTextManager manager) {
		super(text, 0, START_Y, manager);
		font = ArchipeloClient.getGame().getFontManager().getFont(Fonts.PIXELATED, Sizes.VERY_SMALL);
		GlyphLayout layout = new GlyphLayout(font, text);
		width = layout.width;
		timer = 0;
		for (PopupText popupText : manager.getPopupTexts()) {
			if (popupText instanceof NormalPopupText)
				((NormalPopupText) popupText).addToY(layout.height + SPACING);
		}
	}

	@Override
	public void update (float deltatime) {
		timer += deltatime;
		
		if (timer >= LIFE_TIME)
			remove = true;
	}

	@Override
	public void render (SpriteBatch batch) {
		if (timer >= LIFE_TIME - FADE_TIME)
			font.setColor(1, 1, 1, 1 - (1 / FADE_TIME) * (timer - (LIFE_TIME - FADE_TIME)));
		else
			font.setColor(1, 1, 1, 1);
		
		x = ArchipeloClient.getGame().getCameraUi().getWidth() / 2 - width / 2;
		
		BitmapFontCache cache = font.getCache();
		cache.clear();
		cache.addText(text, x, y);
		if (timer >= LIFE_TIME - FADE_TIME)
			cache.setAlphas(1 - (1 / FADE_TIME) * (timer - (LIFE_TIME - FADE_TIME)));
		cache.draw(batch);
		
		font.setColor(1, 1, 1, 1);
	}
	
	public void addToY (float delta) {
		y += delta;
	}

}
