package net.hollowbit.archipelo.world.elements;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.world.MapElement;
import net.hollowbit.archipeloshared.ElementData;

public class AnimatedElement extends MapElement {
	
	Animation animation;
	
	public AnimatedElement (ElementData elementData) {
		super(elementData.id, elementData.name, elementData.width, elementData.height, elementData.collisionTable, elementData.flipX, elementData.flipY, elementData.rotation, elementData.offsetX, elementData.offsetY);
		TextureRegion[] frames = new TextureRegion[elementData.animationFrames];
		for (int i = 0; i < frames.length; i++) {
			frames[i] = new TextureRegion(ArchipeloClient.getGame().getAssetManager().getTexture("elements"), elementData.x * ArchipeloClient.TILE_SIZE + i * elementData.width * ArchipeloClient.TILE_SIZE, elementData.y * ArchipeloClient.TILE_SIZE, elementData.width * ArchipeloClient.TILE_SIZE, elementData.height * ArchipeloClient.TILE_SIZE);
			frames[i].flip(elementData.flipX,  elementData.flipY);
		}
		this.animation = new Animation(elementData.animationTime, frames);
	}

	@Override
	public void draw(SpriteBatch batch, float x, float y) {
		batch.draw(animation.getKeyFrame(ArchipeloClient.STATE_TIME, true), getDrawX(x), getDrawY(y), getOriginX(), getOriginY(), getDrawWidth(), getDrawHeight(), 1, 1, rotation * 90);
	}
	
}
