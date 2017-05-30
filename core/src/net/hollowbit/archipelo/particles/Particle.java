package net.hollowbit.archipelo.particles;

import net.hollowbit.archipelo.tools.rendering.RenderableGameWorldObject;
import net.hollowbit.archipeloshared.CollisionRect;

public abstract class Particle implements RenderableGameWorldObject {

	protected ParticleType type;
	protected float x, y;
	
	public void create(ParticleType type, float x, float y, int wildcard, String meta) {
		this.type = type;
		this.x = x;
		this.y = y;
	}
	
	public float getRenderY() {
		return y;
	}
	
	@Override
	public CollisionRect getViewRect() {
		return new CollisionRect(x, y, type.getImageWidth(), type.getImageHeight());
	}
	
	public abstract void update(float deltaTime);
	public abstract boolean isExpired();
	
}
