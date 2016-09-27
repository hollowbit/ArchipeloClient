package net.hollowbit.archipelo.entity.lifeless;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.hollowbit.archipelo.entity.LifelessEntity;

public class Teleporter extends LifelessEntity {
	
	@Override
	public void render(SpriteBatch batch) {
		batch.draw(entityType.getAnimationFrame("default", style), location.getX(), location.getY());
		super.render(batch);
	}
	
}
