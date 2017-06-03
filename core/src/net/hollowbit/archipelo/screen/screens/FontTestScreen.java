package net.hollowbit.archipelo.screen.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;

import net.hollowbit.archipelo.screen.Screen;

public class FontTestScreen extends Screen {

	public FontTestScreen() {
		super(-1);//Don't bother assigning a real screen type id.
	}

	private static final String CHARS = "!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";
	
	GlyphLayout layout;
	BitmapFont font;
	
	@Override
	public void create() {
		font = new BitmapFont(Gdx.files.external("archipelo/font_test/pixelated.fnt"));
		font.getData().setScale(2);
		layout = new GlyphLayout(font, CHARS, Color.WHITE, 1000, Align.left, true);
	}

	@Override
	public void update(float deltaTime) {
		if (Gdx.input.isKeyJustPressed(Keys.F5)) {
			font = new BitmapFont(Gdx.files.external("archipelo/font_test/pixelated.fnt"));
			font.getData().setScale(2);
			layout = new GlyphLayout(font, CHARS, Color.WHITE, 1000, Align.left, true);
		}
	}

	@Override
	public void render(SpriteBatch batch, float width, float height) {}

	@Override
	public void renderUi(SpriteBatch batch, float width, float height) {
		font.draw(batch, layout, 20, height - 20);
	}

	@Override
	public void resize(int width, int height) {}

	@Override
	public void dispose() {}

}
