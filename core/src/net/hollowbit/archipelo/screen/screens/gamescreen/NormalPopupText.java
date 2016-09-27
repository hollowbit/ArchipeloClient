package net.hollowbit.archipelo.screen.screens.gamescreen;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.tools.FontManager.Fonts;
import net.hollowbit.archipelo.tools.FontManager.Sizes;

public class NormalPopupText extends PopupText {
	
	private static final int START_Y = 120;
	private static final float FADE_TIME = 1;//1 second
	private static final float LIFE_TIME = 5;//5 seconds
	private static final float SPACING = 3;//pixels
	
	private float timer;
	private float width;
	
	public NormalPopupText (String text, PopupTextManager manager) {
		super(text, 0, START_Y, manager);
		GlyphLayout layout = new GlyphLayout(ArchipeloClient.getGame().getFontManager().getFont(Fonts.PIXELATED, Sizes.SMALL), text);
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
		BitmapFont font = ArchipeloClient.getGame().getFontManager().getFont(Fonts.PIXELATED, Sizes.SMALL);
		if (timer >= LIFE_TIME - FADE_TIME)
			font.setColor(1, 1, 1, 1 - (1 / FADE_TIME) * (timer - (LIFE_TIME - FADE_TIME)));
		else
			font.setColor(1, 1, 1, 1);
		
		x = ArchipeloClient.getGame().getCameraUi().getWidth() / 2 - width / 2;
		font.draw(batch, text, x, y);
		font.setColor(1, 1, 1, 1);
	}
	
	public void addToY (float delta) {
		y += delta;
	}

}
