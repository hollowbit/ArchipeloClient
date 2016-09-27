package net.hollowbit.archipelo.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class LifelessEntity extends Entity {

	Texture texture;

	@Override
	public void render (SpriteBatch batch) {
		super.render(batch);
		if (texture != null)
			batch.draw(texture, location.getX(), location.getY());
	}

	@Override
	public void update (float deltaTime) {}
	
}
