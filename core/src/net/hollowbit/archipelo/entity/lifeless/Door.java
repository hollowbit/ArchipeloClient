package net.hollowbit.archipelo.entity.lifeless;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.hollowbit.archipelo.entity.EntitySnapshot;
import net.hollowbit.archipelo.entity.LifelessEntity;

public class Door extends LifelessEntity {
	
	boolean open = false;
	
	@Override
	public void render(SpriteBatch batch) {
		if (open)
			batch.draw(entityType.getAnimationFrame("open", 0, style), location.getX(), location.getY());
		else
			batch.draw(entityType.getAnimationFrame("closed", 0, style), location.getX(), location.getY());
		super.render(batch);
	}
	
	/**
	 * Used by door subtypes to avoid rendering open and closed while still being able to render supertype things
	 * @param batch
	 */
	public void renderSuper(SpriteBatch batch) {
		super.render(batch);
	}
	
	@Override
	public void applyChangesSnapshot(EntitySnapshot snapshot) {
		//System.out.println("Door.java test!");
		open = snapshot.getBoolean("open", open);
		super.applyChangesSnapshot(snapshot);
	}
	
}
