package net.hollowbit.archipelo.form;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

import net.hollowbit.archipelo.ArchipeloClient;

public abstract class MobileCompatibleWindow extends Window {
	
	protected float inset;
	
	/**
	 * Create a new window that is compatible with mobile screens.
	 * @param title
	 * @param skin
	 */
	public MobileCompatibleWindow(String title, Skin skin) {
		this(title, skin, ArchipeloClient.SCREEN_INSET_FOR_WINDOWS);
	}
	
	/**
	 * Create a new window that is compatible with mobile screens.
	 * @param title
	 * @param skin
	 * @param inset Used as a buffer between the window and the screen. 0-1.0. 1.0 means no buffer.
	 */
	public MobileCompatibleWindow(String title, Skin skin, float inset) {
		super(title, skin);
		ArchipeloClient.getGame().addWindow(this);
		this.inset = inset;
	}
	
	public void resize (int width, int height) {
		this.pack();
	}
	
	@Override
	public void pack() {
		super.pack();
		
		if (ArchipeloClient.IS_MOBILE) {
			//Screen inset is used to have a buffer between the window border and the screen border
			float screenWidth = Gdx.graphics.getWidth() * inset;
			float screenHeight = Gdx.graphics.getHeight() * inset;
			
			float ratioWidth = screenWidth / this.getWidth();
			float ratioHeight =  screenHeight / this.getHeight();
			
			if (ratioHeight > ratioWidth)
				this.setScale(screenWidth / this.getWidth());
			else
				this.setScale(screenHeight / this.getHeight());
			
			centerOnScreen();
			this.setMovable(false);
		}
	}
	
	/**
	 * Move window to the center of the screen
	 */
	public void centerOnScreen () {
		this.setPosition(Gdx.graphics.getWidth() / 2 - this.getWidth() * this.getScaleX() / 2 , Gdx.graphics.getHeight() / 2 - this.getHeight() * this.getScaleY() / 2);
	}
	
	@Override
	public boolean remove () {
		ArchipeloClient.getGame().removeWindow(this);
		return super.remove();
	}
	
}
