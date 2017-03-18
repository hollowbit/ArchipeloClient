package net.hollowbit.archipelo.screen;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.hollowbit.archipelo.ArchipeloClient;

public class ScreenManager {
	
	private Screen currentScreen;
	private Screen newScreen;
	
	public ScreenManager () {
		newScreen = null;
	}
	
	public void render (SpriteBatch batch, float width, float height) {
		if (currentScreen != null)
			currentScreen.render(batch, width, height);
	}
	
	public void renderUi (SpriteBatch batch, float width, float height) {
		if (currentScreen != null)
			currentScreen.renderUi(batch, width, height);
	}
	
	public void resize (int width, int height) {
		if (currentScreen != null)
			currentScreen.resize(width, height);
	}
	
	public void update (float deltaTime) {
		if (newScreen != null) {
			if (currentScreen != null) {
				ArchipeloClient.getGame().clearWindows();
				currentScreen.dispose();
			}
			currentScreen = newScreen;
			newScreen.create();
			newScreen = null;
		}
		currentScreen.update(deltaTime);
	}
	
	public void pause () {
		if (currentScreen != null)
			currentScreen.pause();
	}
	
	public void setScreen (Screen screen) {
		newScreen = screen;
	}
	
	public Screen getScreen () {
		return currentScreen;
	}
	
	public int getScreenType () {
		return currentScreen.type;
	}
	
}
