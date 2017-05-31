package net.hollowbit.archipelo.particles;

import com.badlogic.gdx.graphics.Texture;

import net.hollowbit.archipelo.particles.types.EntityChunkParticle;
import net.hollowbit.archipelo.particles.types.HealthParticle;

public enum ParticleType {
	
	HEALTH(HealthParticle.class),
	ENTITY_CHUNK(EntityChunkParticle.class);
	
	private Class<? extends Particle> blueprint;
	private Texture image;
	private int imgWidth, imgHeight;
	
	private ParticleType(Class<? extends Particle> blueprint) {
		this.blueprint = blueprint;
	}
	
	private ParticleType(Class<? extends Particle> blueprint, String imgName, int imgSize) {
		this(blueprint, imgName, imgSize, imgSize);
	}
	
	private ParticleType(Class<? extends Particle> blueprint, String imgName, int imgWidth, int imgHeight) {
		this(blueprint);
		image = new Texture("particles/" + imgName + ".png");
		this.imgWidth = imgWidth;
		this.imgHeight = imgHeight;
	}
	
	public Texture getImage() {
		return image;
	}
	
	public int getImageWidth() {
		return imgWidth;
	}
	
	public int getImageHeight() {
		return imgHeight;
	}
	
	public Class<? extends Particle> getBlueprint() {
		return blueprint;
	}
	
	public static Particle createUsingData(int typeId, int x, int y, int wildcard, String meta) {
		try {
			ParticleType type = ParticleType.values()[typeId];
			Particle particle = type.getBlueprint().newInstance();
			particle.create(type, x, y, wildcard, meta);
			return particle;
		} catch (Exception e) {
			System.out.println("Could not instantiate particle of type: " + typeId);
		}
		return null;
	}
	
}
