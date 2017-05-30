package net.hollowbit.archipelo.particles.types;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.particles.Particle;
import net.hollowbit.archipelo.particles.ParticleType;
import net.hollowbit.archipelo.tools.FontManager.Fonts;
import net.hollowbit.archipelo.tools.FontManager.Sizes;

public class HealthParticle extends Particle {
	
	private static final float MAX_DAMAGE = 100;
	private static final int SPEED = 12;
	private static final int DISTANCE_TO_TRAVEL = 8;
	
	private static BitmapFont font;
	String text;
	Color color;
	float distanceTraveled;
	
	public void create(ParticleType type, float x, float y, int wildcard, String meta) {
		super.create(type, x, y, wildcard, meta);
		if (font == null)
			font = ArchipeloClient.getGame().getFontManager().getFont(Fonts.GAMEWORLD, Sizes.VERY_SMALL);
		
		this.distanceTraveled = 0;
		
		int damage = Integer.parseInt(meta);
		float intensity = damage / MAX_DAMAGE;
		if (intensity > 1)
			intensity = 1;
		if (intensity < -1)
			intensity = -1;
		
		//Calculate color based on damage
		float r, g, b;
		if (intensity > 0) {
			r = 0;
			g = 1 - intensity;
			b = 0;
				
			if (g < 0.2f)
				g = 0.2f;
		} else {
			float absIntensity = Math.abs(intensity);
			r = 1 - absIntensity;
			g = 0;
			b = 0;
			
			if (r < 0.2f)
				r = 0.2f;
		}
		color = new Color(r, g, b, 1);
		text = "" + Math.abs(damage);
		GlyphLayout layout = new GlyphLayout(font, text, color, 0, Align.center, false);
		this.x -= layout.width / 2;
	}

	@Override
	public void update(float deltaTime) {
		float delta = deltaTime * SPEED;
		distanceTraveled += delta;
		y += delta;
		
		color.a = distanceTraveled / DISTANCE_TO_TRAVEL;
	}

	@Override
	public boolean isExpired() {
		return distanceTraveled >= DISTANCE_TO_TRAVEL;
	}

	@Override
	public void renderObject(SpriteBatch batch) {
		font.setColor(color);
		BitmapFontCache cache = font.getCache();
		cache.clear();
		cache.addText(text, x, y);
		cache.setAlphas(color.a);
		cache.draw(batch);
		
		font.setColor(1, 1, 1, 1);
	}

}
