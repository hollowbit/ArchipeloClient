package net.hollowbit.archipelo.tools.rendering;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.hollowbit.archipeloshared.CollisionRect;

public interface RenderableGameWorldObject {
	
	public abstract float getRenderY();
	public abstract CollisionRect getViewRect();
	public abstract void renderObject(SpriteBatch batch);
	
}
