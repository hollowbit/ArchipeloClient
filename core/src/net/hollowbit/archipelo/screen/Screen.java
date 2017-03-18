package net.hollowbit.archipelo.screen;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class Screen {
	
	public int type = 0;
	
	public Screen (int type) {
		this.type = type;
	}
	
	public abstract void create ();
	public abstract void update (float deltaTime);
	public abstract void render (SpriteBatch batch, float width, float height);
	public abstract void renderUi (SpriteBatch batch, float width, float height);
	public abstract void resize (int width, int height);
	public abstract void dispose ();
	public void pause () {}
	
}
