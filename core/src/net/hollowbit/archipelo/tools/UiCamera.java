package net.hollowbit.archipelo.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class UiCamera {
	
	OrthographicCamera cam;
	ScreenViewport viewport;
	
	public UiCamera () {
		cam = new OrthographicCamera();
		viewport = new ScreenViewport(cam);
		viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		viewport.apply();
		cam.position.set(cam.viewportWidth / 2, cam.viewportHeight / 2, 0);
		cam.update();
	}
	
	public void resize (int width, int height) {
		viewport.update(width, height);
		cam.position.set(width / 2, height / 2, 0);
		cam.update();
	}
	
	public ScreenViewport getScreenViewport () {
		return viewport;
	}
	
	public Matrix4 combined () {
		return cam.combined;
	}
	
	public float getWidth () {
		return viewport.getScreenWidth();
	}
	
	public float getHeight () {
		return viewport.getScreenHeight();
	}
	
}
