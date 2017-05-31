package net.hollowbit.archipelo.particles.types;

import java.util.Random;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.hollowbit.archipelo.entity.EntityType;
import net.hollowbit.archipelo.particles.Particle;
import net.hollowbit.archipelo.particles.ParticleType;
import net.hollowbit.archipelo.tools.StaticTools;
import net.hollowbit.archipeloshared.CollisionRect;
import net.hollowbit.archipeloshared.Direction;

public class EntityChunkParticle extends Particle {
	
	private static final int MIN_SIZE = 2;
	private static final int MAX_SIZE = 4;
	private static final int MAX_VELOCITY = 50;
	private static final float LIFE_TIME = 1;
	private static final float DECELERATION_TIME = 0.8f;
	
	private TextureRegion image;
	private int size;
	private float velocityX;
	private float velocityY;
	private float decelerationX;
	private float decelerationY;
	private float timer;
	
	@Override
	public void create(ParticleType type, float x, float y, int wildcard, String meta) {
		super.create(type, x, y, wildcard, meta);
		String[] metaSplit = meta.split(";");
		EntityType entityType = EntityType.getById(metaSplit[0]);
		image = entityType.getAnimationFrame(entityType.getDefaultAnimationId(), Direction.DOWN, 0, Integer.parseInt(metaSplit[1]));
		
		Random random = new Random(wildcard);
		
		this.size = random.nextInt(MAX_SIZE - MIN_SIZE) + MIN_SIZE;
		this.velocityX = random.nextInt(MAX_VELOCITY * 2) - MAX_VELOCITY;
		this.velocityY = random.nextInt(MAX_VELOCITY * 2) - MAX_VELOCITY;
		this.decelerationX = velocityX / DECELERATION_TIME;
		this.decelerationY = velocityY / DECELERATION_TIME;
	}
	
	@Override
	public void renderObject(SpriteBatch batch) {
		if (timer > DECELERATION_TIME && timer <= LIFE_TIME) {
			float fraction = 1 - StaticTools.singleDimentionLerpFraction(DECELERATION_TIME, LIFE_TIME, timer);
			batch.setColor(1, 1, 1, fraction);
		}
		batch.draw(image, x, y, size, size);
		batch.setColor(1, 1, 1, 1);
	}
	
	@Override
	public CollisionRect getViewRect() {
		return new CollisionRect(x, y, size, size);
	}
	
	@Override
	public void update(float deltaTime) {
		timer += deltaTime;
		this.x += deltaTime * velocityX;
		this.y += deltaTime * velocityY;
		
		this.velocityX -= decelerationX * deltaTime;
		this.velocityY -= decelerationY * deltaTime;
	}

	@Override
	public boolean isExpired() {
		return timer >= LIFE_TIME;
	}

}
