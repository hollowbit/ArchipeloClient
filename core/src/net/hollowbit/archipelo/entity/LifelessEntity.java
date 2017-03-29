package net.hollowbit.archipelo.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.hollowbit.archipeloshared.Direction;
import net.hollowbit.archipeloshared.EntitySnapshot;

public abstract class LifelessEntity extends Entity {

	Texture texture;

	@Override
	public void render (SpriteBatch batch) {
		super.render(batch);
		if (texture != null)
			batch.draw(texture, location.getX(), location.getY());
	}
	
	@Override
	public void applyChangesSnapshot(EntitySnapshot snapshot) {
		location.set(snapshot.getFloat("x", location.getX()), snapshot.getFloat("y", location.getY()), Direction.values()[snapshot.getInt("direction", location.direction.ordinal())]);
		super.applyChangesSnapshot(snapshot);
	}
	
}
