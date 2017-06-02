package net.hollowbit.archipelo.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class FontManager {
	
	public static final int LOADED_FONTS = 1;
	
	public enum Fonts { PIXELATED, GAMEWORLD }
	public enum Sizes { VERY_SMALL, SMALL, MEDIUM, LARGE }
	
	private BitmapFont[][] fonts;
	
	public FontManager () {
		fonts = new BitmapFont[Fonts.values().length][Sizes.values().length];
		load();
	}
	
	//Load all fonts here
	private void load () {
		fonts[Fonts.PIXELATED.ordinal()][Sizes.VERY_SMALL.ordinal()] = new BitmapFont(Gdx.files.internal("ui/fonts/pixelated.fnt"));
		fonts[Fonts.PIXELATED.ordinal()][Sizes.VERY_SMALL.ordinal()].getData().markupEnabled = true;
		fonts[Fonts.PIXELATED.ordinal()][Sizes.SMALL.ordinal()] = new BitmapFont(Gdx.files.internal("ui/fonts/pixelated.fnt"));
		fonts[Fonts.PIXELATED.ordinal()][Sizes.SMALL.ordinal()].getData().setScale(2);
		fonts[Fonts.PIXELATED.ordinal()][Sizes.SMALL.ordinal()].getData().markupEnabled = true;
		fonts[Fonts.PIXELATED.ordinal()][Sizes.MEDIUM.ordinal()] = new BitmapFont(Gdx.files.internal("ui/fonts/pixelated.fnt"));
		fonts[Fonts.PIXELATED.ordinal()][Sizes.MEDIUM.ordinal()].getData().setScale(3);
		fonts[Fonts.PIXELATED.ordinal()][Sizes.MEDIUM.ordinal()].getData().markupEnabled = true;
		fonts[Fonts.PIXELATED.ordinal()][Sizes.LARGE.ordinal()] = new BitmapFont(Gdx.files.internal("ui/fonts/pixelated.fnt"));
		fonts[Fonts.PIXELATED.ordinal()][Sizes.LARGE.ordinal()].getData().setScale(4);
		fonts[Fonts.PIXELATED.ordinal()][Sizes.LARGE.ordinal()].getData().markupEnabled = true;
		
		fonts[Fonts.GAMEWORLD.ordinal()][Sizes.LARGE.ordinal()] = new BitmapFont(Gdx.files.internal("ui/fonts/gameworld.fnt"));
		fonts[Fonts.GAMEWORLD.ordinal()][Sizes.LARGE.ordinal()].getData().markupEnabled = true;
		fonts[Fonts.GAMEWORLD.ordinal()][Sizes.VERY_SMALL.ordinal()] = fonts[Fonts.GAMEWORLD.ordinal()][Sizes.LARGE.ordinal()];
		fonts[Fonts.GAMEWORLD.ordinal()][Sizes.SMALL.ordinal()] = fonts[Fonts.GAMEWORLD.ordinal()][Sizes.LARGE.ordinal()];
		fonts[Fonts.GAMEWORLD.ordinal()][Sizes.MEDIUM.ordinal()] = fonts[Fonts.GAMEWORLD.ordinal()][Sizes.LARGE.ordinal()];
	}
	
	public BitmapFont getFont (Fonts font, Sizes size) {
		return fonts[font.ordinal()][size.ordinal()];
	}
	
}
