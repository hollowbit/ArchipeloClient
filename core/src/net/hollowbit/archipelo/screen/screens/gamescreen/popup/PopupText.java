package net.hollowbit.archipelo.screen.screens.gamescreen.popup;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.hollowbit.archipelo.tools.QuickUi;

public abstract class PopupText {
	
	public static final int TYPE_NORMAL = 0;
	
	protected String text;
	protected float x, y;
	protected PopupTextManager manager;
	public boolean remove = false;
	
	public PopupText (String text, float x, float y, PopupTextManager manager) {
		this.text = QuickUi.processMessageString(text);
		this.x = x;
		this.y = y;
		this.manager = manager;
	}
	
	public abstract void update (float deltatime);
	public abstract void render (SpriteBatch batch);
	
}
