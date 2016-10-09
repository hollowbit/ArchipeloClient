package net.hollowbit.archipelo.screen.screens.mainmenu;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.tools.GameCamera;
import net.hollowbit.archipeloshared.CollisionRect;

public class ScrollingBackground {
	
	private static final float CAM_SPEED_X = 10;
	private static final float CAM_SPEED_Y = 5;
	
	private static final float BACKGROUND_IMAGE_SCALE = 1.5f;

	Texture background;
	float backgroundWidth, backgroundHeight;
	
	float camVelocityX = CAM_SPEED_X, camVelocityY = CAM_SPEED_Y;
	GameCamera cam;
	
	/**
	 * Background image that moves around
	 */
	public ScrollingBackground () {
		background = ArchipeloClient.getGame().getAssetManager().getTexture("mainmenu-background");
		
		cam = ArchipeloClient.getGame().getCamera();
		cam.focusOnEntity(null);
		cam.move(200, 200, 0);
		backgroundWidth = cam.getWidth() * BACKGROUND_IMAGE_SCALE;
		backgroundHeight = cam.getHeight() * BACKGROUND_IMAGE_SCALE;
	}
	
	/**
	 * Call in screen update method. Moves camera around background image
	 * @param deltaTime
	 */
	public void update (float deltaTime) {
		//Update game camera to move around map
		CollisionRect rect = cam.getViewRect();
		float camX = rect.x + camVelocityX * deltaTime;
		float camY = rect.y + camVelocityY * deltaTime;
		
		if (camX < 0) {
			camX = 0;
			camVelocityX = -camVelocityX;
		}
		
		if (camY < 0) {
			camY = 0;
			camVelocityY = -camVelocityY;
		}
		
		if (camX + rect.width > backgroundWidth) {
			camX = backgroundWidth - rect.width;
			camVelocityX = -camVelocityX;
		}
		
		if (camY + rect.height > backgroundHeight) {
			camY = backgroundHeight - rect.height;
			camVelocityY = -camVelocityY;
		}
		
		cam.move(camX, camY, 0);
	}
	
	/**
	 * Render background image. Call in pixelated render, not render ui.
	 * @param batch
	 */
	public void render (SpriteBatch batch) {
		batch.draw(background, 0, 0, backgroundWidth, backgroundHeight);//Render blurred background image
	}
	
	/**
	 * Adjust background image size
	 */
	public void resize () {
		backgroundWidth = cam.getWidth() * BACKGROUND_IMAGE_SCALE;
		backgroundHeight = cam.getHeight() * BACKGROUND_IMAGE_SCALE;
	}
	
}
